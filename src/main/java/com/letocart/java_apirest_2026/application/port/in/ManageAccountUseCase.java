package com.letocart.java_apirest_2026.application.port.in;

import com.letocart.java_apirest_2026.model.Account;
import java.util.List;

/**
 * Port d'entrée pour la gestion des comptes
 * (Hexagonal Architecture - Input Port / Use Case Interface)
 */
public interface ManageAccountUseCase {
    
    List<Account> getAllAccounts();
    
    Account getAccountById(Long id);
    
    Account updateAccount(Long id, Account account);
    
    void deleteAccount(Long id);
}
