package com.letocart.java_apirest_2026.controller;

import com.letocart.java_apirest_2026.application.port.in.CreateAccountUseCase;
import com.letocart.java_apirest_2026.application.port.in.ManageAccountUseCase;
import com.letocart.java_apirest_2026.model.Account;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller pour la gestion des comptes (Account)
 * Point d'entrée HTTP de l'application
 * Couche Infrastructure - Architecture Hexagonale (Adapter IN)
 * 
 * Pattern: Adapter (transforme les requêtes HTTP vers les use cases métier)
 * 
 * @author LetoCart Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "API de gestion des comptes utilisateurs")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final ManageAccountUseCase manageAccountUseCase;

    /**
     * Injection des use cases via constructeur
     * @param createAccountUseCase Use case de création de compte
     * @param manageAccountUseCase Use case de gestion des comptes
     */
    public AccountController(
            CreateAccountUseCase createAccountUseCase,
            ManageAccountUseCase manageAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.manageAccountUseCase = manageAccountUseCase;
    }

    /**
     * POST /api/accounts - Créer un nouveau compte avec validation d'adresse
     */
    @PostMapping
    @Operation(summary = "Créer un compte", description = "Crée un nouveau compte avec validation de l'adresse via l'API BAN")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Compte créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou adresse inexistante")
    })
    public ResponseEntity<?> createAccount(@RequestBody Account account) {
        try {
            Account createdAccount = createAccountUseCase.createAccount(account);
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET /api/accounts - Récupérer tous les comptes
     */
    @GetMapping
    @Operation(summary = "Liste des comptes", description = "Récupère tous les comptes enregistrés")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = manageAccountUseCase.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    /**
     * GET /api/accounts/{id} - Récupérer un compte par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un compte", description = "Récupère un compte par son identifiant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compte trouvé"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        try {
            Account account = manageAccountUseCase.getAccountById(id);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT /api/accounts/{id} - Mettre à jour un compte
     */
    @PutMapping("/{id}")
    @Operation(summary = "Modifier un compte", description = "Met à jour les informations d'un compte existant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compte mis à jour"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public ResponseEntity<?> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        try {
            Account updatedAccount = manageAccountUseCase.updateAccount(id, account);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DELETE /api/accounts/{id} - Supprimer un compte
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un compte", description = "Supprime un compte par son identifiant")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Compte supprimé"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        try {
            manageAccountUseCase.deleteAccount(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
