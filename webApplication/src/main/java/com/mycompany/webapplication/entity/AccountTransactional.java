/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.webapplication.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author ryan
 */

public class AccountTransactional {

    private Long id;


    private TransactionType typeTransaction;

    private BigDecimal amount;

    private LocalDateTime timestamp;

    private String description;

    private Account account;

public AccountTransactional() {
    // necessário para frameworks, JDBC, reflection etc.
}

public AccountTransactional(TransactionType typeTransaction, BigDecimal amount,
                            LocalDateTime timestamp, String description, Account account) {
    this.typeTransaction = typeTransaction;
    this.amount = amount;
    this.timestamp = timestamp;
    this.description = description;
    this.account = account;
}

    
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(TransactionType typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
