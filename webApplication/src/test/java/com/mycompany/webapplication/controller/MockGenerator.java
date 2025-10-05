package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Users;

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

    public static Users createUser(){
        Users users = new Users();
        users.setId(14L);
        users.setName("teste");
        users.setEmail("teste@gmail.com");
        users.setPassword("123R#111");
        return users;
    }
}