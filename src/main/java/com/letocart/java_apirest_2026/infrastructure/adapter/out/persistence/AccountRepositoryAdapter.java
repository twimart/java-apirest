package com.letocart.java_apirest_2026.infrastructure.adapter.out.persistence;

import com.letocart.java_apirest_2026.domain.port.out.AccountRepositoryPort;
import com.letocart.java_apirest_2026.model.Account;
import com.letocart.java_apirest_2026.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adapter de persistance pour les comptes (Account)
 * Implémente le port de sortie AccountRepositoryPort en utilisant Spring Data JPA
 * Couche Infrastructure - Architecture Hexagonale
 * 
 * Pattern: Adapter (transforme l'interface Spring Data JPA vers notre port métier)
 * 
 * @author LetoCart Team
 * @version 1.0
 */
@Component
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountRepository accountRepository;

    /**
     * Injection du repository Spring Data JPA
     * @param accountRepository Repository JPA géré par Spring
     */
    public AccountRepositoryAdapter(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        accountRepository.findAll().forEach(accounts::add);
        return accounts;
    }

    @Override
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }
}
