package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Account;

import java.math.BigDecimal;

public class MockGenerator {
    public static Account createAccount(){
        Account conta = new Account();
        conta.setAccountNumber("300625-3");
        conta.setId(1L);
        conta.setAgency("0001");
        conta.setUserId(14L);
        conta.setBalance(BigDecimal.valueOf(4000));
        return conta;
    }
}
