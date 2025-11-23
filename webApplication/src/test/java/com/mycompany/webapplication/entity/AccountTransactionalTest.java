package com.mycompany.webapplication.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class AccountTransactionalTest {

    @Test
    void testEmptyConstructorAndSetters() {
        AccountTransactional at = new AccountTransactional();

        Account acc = new Account(1L, "12345", "001",
                new BigDecimal("100.00"), 10L);

        LocalDateTime now = LocalDateTime.now();

        at.setId(1L);
        at.setTypeTransaction(TransactionType.DEPOSIT);
        at.setAmount(new BigDecimal("200.00"));
        at.setTimestamp(now);
        at.setDescription("Depósito realizado");
        at.setAccount(acc);

        assertEquals(1L, at.getId());
        assertEquals(TransactionType.DEPOSIT, at.getTypeTransaction());
        assertEquals(new BigDecimal("200.00"), at.getAmount());
        assertEquals(now, at.getTimestamp());
        assertEquals("Depósito realizado", at.getDescription());
        assertEquals(acc, at.getAccount());
    }

    @Test
    void testFullConstructor() {
        Account acc = new Account(1L, "98765", "002",
                new BigDecimal("500.00"), 20L);

        LocalDateTime moment = LocalDateTime.of(2024, 1, 1, 12, 30);

        AccountTransactional at = new AccountTransactional(
                TransactionType.WITHDRAW,
                new BigDecimal("50.00"),
                moment,
                "Saque realizado",
                acc
        );

        assertNull(at.getId()); // id não é passado no construtor
        assertEquals(TransactionType.WITHDRAW, at.getTypeTransaction());
        assertEquals(new BigDecimal("50.00"), at.getAmount());
        assertEquals(moment, at.getTimestamp());
        assertEquals("Saque realizado", at.getDescription());
        assertEquals(acc, at.getAccount());
    }
}
