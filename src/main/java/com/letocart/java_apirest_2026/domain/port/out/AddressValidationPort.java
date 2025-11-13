package com.letocart.java_apirest_2026.domain.port.out;

import com.letocart.java_apirest_2026.model.Address;

/**
 * Port de sortie pour la validation d'adresse via API externe
 * (Hexagonal Architecture - Output Port)
 */
public interface AddressValidationPort {
    
    /**
     * Valide une adresse via une API de géocodage externe
     * @param address Adresse à valider
     * @return true si l'adresse est valide (score > 0.5)
     */
    boolean validateAddress(Address address);
}
