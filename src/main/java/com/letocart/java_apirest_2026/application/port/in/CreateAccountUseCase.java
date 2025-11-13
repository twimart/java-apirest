package com.letocart.java_apirest_2026.application.port.in;

import com.letocart.java_apirest_2026.model.Account;

/**
 * Port d'entrée pour la création de compte
 * (Hexagonal Architecture - Input Port / Use Case Interface)
 */
public interface CreateAccountUseCase {
    
    /**
     * Crée un nouveau compte avec validation d'adresse
     * @param account Compte à créer
     * @return Compte créé
     * @throws IllegalArgumentException si l'email existe déjà ou l'adresse est invalide
     */
    Account createAccount(Account account);
}
