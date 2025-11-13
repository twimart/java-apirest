package com.letocart.java_apirest_2026.application.usecase;

import com.letocart.java_apirest_2026.application.port.in.CreateAccountUseCase;
import com.letocart.java_apirest_2026.application.port.in.ManageAccountUseCase;
import com.letocart.java_apirest_2026.domain.port.out.AccountRepositoryPort;
import com.letocart.java_apirest_2026.domain.port.out.AddressValidationPort;
import com.letocart.java_apirest_2026.model.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Use Case pour la gestion des comptes (Account)
 * Implémente la logique métier de l'application
 * Couche Application - Architecture Hexagonale
 * 
 * @author LetoCart Team
 * @version 1.0
 */
@Service
@Transactional
public class AccountUseCaseImpl implements CreateAccountUseCase, ManageAccountUseCase {

    private final AccountRepositoryPort accountRepository;
    private final AddressValidationPort addressValidation;

    /**
     * Injection de dépendances via constructeur (best practice Spring)
     * @param accountRepository Port pour la persistance des comptes
     * @param addressValidation Port pour la validation des adresses
     */
    public AccountUseCaseImpl(
            AccountRepositoryPort accountRepository,
            AddressValidationPort addressValidation) {
        this.accountRepository = accountRepository;
        this.addressValidation = addressValidation;
    }

    /**
     * Créer un nouveau compte avec validation métier
     * @param account Le compte à créer
     * @return Le compte créé avec son ID
     * @throws IllegalArgumentException si l'email existe déjà ou l'adresse est invalide
     */
    @Override
    public Account createAccount(Account account) {
        // 1. Vérifier l'unicité de l'email (règle métier)
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà");
        }

        // 2. Valider l'adresse via API gouvernementale (exigence TD)
        if (account.getAddress() != null) {
            if (!addressValidation.validateAddress(account.getAddress())) {
                throw new IllegalArgumentException("L'adresse fournie n'est pas valide ou n'existe pas");
            }
            // Établir la relation bidirectionnelle
            account.getAddress().setAccount(account);
        }

        // 3. Persister le compte
        return accountRepository.save(account);
    }

    /**
     * Récupérer tous les comptes
     * @return Liste de tous les comptes
     */
    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    /**
     * Récupérer un compte par son ID
     * @param id L'identifiant du compte
     * @return Le compte trouvé
     * @throws RuntimeException si le compte n'existe pas
     */
    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé avec l'ID : " + id));
    }

    /**
     * Mettre à jour un compte existant
     * @param id L'identifiant du compte à modifier
     * @param account Les nouvelles données
     * @return Le compte mis à jour
     * @throws RuntimeException si le compte n'existe pas
     */
    @Override
    public Account updateAccount(Long id, Account account) {
        Account existingAccount = getAccountById(id);
        
        // Mise à jour des champs (pattern: modifier les champs un par un)
        existingAccount.setFirstName(account.getFirstName());
        existingAccount.setLastName(account.getLastName());
        existingAccount.setEmail(account.getEmail());
        existingAccount.setPassword(account.getPassword());
        
        // Gestion de l'adresse
        if (account.getAddress() != null) {
            existingAccount.setAddress(account.getAddress());
            existingAccount.getAddress().setAccount(existingAccount);
        }
        
        return accountRepository.save(existingAccount);
    }

    /**
     * Supprimer un compte
     * @param id L'identifiant du compte à supprimer
     * @throws RuntimeException si le compte n'existe pas
     */
    @Override
    public void deleteAccount(Long id) {
        if (!accountRepository.findById(id).isPresent()) {
            throw new RuntimeException("Compte non trouvé avec l'ID : " + id);
        }
        accountRepository.deleteById(id);
    }
}
