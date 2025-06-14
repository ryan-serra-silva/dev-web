/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.webapplication.entity;

import java.math.BigDecimal;

/**
 *
 * @author ryan
 */

public class Account {

    
    private Long id;

    private String accountNumber;

    private String agency;

    private BigDecimal balance;

    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account(Long id, String accountNumber, String agency, BigDecimal balance, Long userId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.agency = agency;
        this.balance = balance;
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Account [id=" + id + ", accountNumber=" + accountNumber + ", agency=" + agency + ", balance=" + balance
                + ", userId=" + userId + "]";
    }
    
    
}
