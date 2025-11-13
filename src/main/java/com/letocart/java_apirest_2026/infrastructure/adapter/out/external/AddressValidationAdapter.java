package com.letocart.java_apirest_2026.infrastructure.adapter.out.external;

import com.letocart.java_apirest_2026.domain.port.out.AddressValidationPort;
import com.letocart.java_apirest_2026.dto.AddressValidationResponse;
import com.letocart.java_apirest_2026.model.Address;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Adapter pour validation d'adresse via API Gouvernementale française
 * Implémente le port de sortie AddressValidationPort en utilisant HttpClient (HTTP/2)
 * Couche Infrastructure - Architecture Hexagonale
 * 
 * API: https://api-gouv.lab.rioc.fr/search (BAN - Base Adresse Nationale)
 * Pattern: Adapter (transforme l'API REST externe vers notre port métier)
 * 
 * @author LetoCart Team
 * @version 1.0
 */
@Component
public class AddressValidationAdapter implements AddressValidationPort {

    private final HttpClient httpClient;
    private static final String API_BASE_URL = "https://api-gouv.lab.rioc.fr/search";
    private static final double SCORE_THRESHOLD = 0.5; // Seuil de confiance minimum

    /**
     * Construction de l'adapter avec HttpClient HTTP/2
     * Note: HTTP/2 requis car l'API renvoie des réponses vides avec HTTP/1.1
     */
    public AddressValidationAdapter() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2) // IMPORTANT: HTTP/2 requis
                .build();
    }

    /**
     * Valide une adresse via l'API gouvernementale BAN
     * Utilise les Streams Java (exigence TD)
     * 
     * @param address L'adresse à valider
     * @return true si l'adresse existe avec score > 0.5, false sinon
     */
    @Override
    public boolean validateAddress(Address address) {
        try {
            String fullAddress = buildFullAddress(address);
            AddressValidationResponse response = callGeocodingApi(fullAddress);
            
            if (response == null || response.getFeatures() == null || response.getFeatures().isEmpty()) {
                return false;
            }

            // STREAM JAVA (exigence TD): traitement fonctionnel des résultats
            return response.getFeatures().stream()
                    .findFirst()                     // Premier résultat
                    .map(feature -> feature.getProperties().getScore())  // Extraire le score
                    .filter(score -> score != null && score > SCORE_THRESHOLD) // Filtrer par seuil
                    .isPresent();                    // Vérifier présence

        } catch (Exception e) {
            System.err.println("Erreur validation adresse: " + e.getMessage());
            return false;
        }
    }

    /**
     * Construit l'adresse complète pour la requête API
     * @param address L'objet adresse
     * @return String formatée "rue codePostal ville"
     */
    private String buildFullAddress(Address address) {
        return String.format("%s %s %s",
                address.getStreet(),
                address.getPostalCode(),
                address.getCity()
        );
    }

    /**
     * Appel de l'API de géocodage avec HttpClient HTTP/2
     * @param fullAddress L'adresse complète à valider
     * @return Réponse désérialisée ou null en cas d'erreur
     */
    private AddressValidationResponse callGeocodingApi(String fullAddress) {
        String url = UriComponentsBuilder
                .fromUriString(API_BASE_URL)
                .queryParam("q", fullAddress)
                .queryParam("limit", "1")
                .toUriString();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (compatible; JavaApp/1.0)")
                    .header("Accept", "*/*")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();
            
            if (json == null || json.isBlank()) {
                return null;
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, AddressValidationResponse.class);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à l'API de géocodage: " + e.getMessage());
            return null;
        }
    }
}
