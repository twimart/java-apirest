package com.letocart.java_apirest_2026.domain.port.out;

import com.letocart.java_apirest_2026.model.Account;
import java.util.List;
import java.util.Optional;

/**
 * Port de sortie pour la persistance des comptes
 * (Hexagonal Architecture - Output Port)
 */
public interface AccountRepositoryPort {
    
    Account save(Account account);
    
    Optional<Account> findById(Long id);
    
    Optional<Account> findByEmail(String email);
    
    List<Account> findAll();
    
    void deleteById(Long id);
    
    boolean existsByEmail(String email);
}
