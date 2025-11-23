package com.mycompany.webapplication.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    @Test
    void testEmptyConstructorAndSetters() {
        Account account = new Account();

        account.setId(1L);
        account.setAccountNumber("12345");
        account.setAgency("001");
        account.setBalance(new BigDecimal("100.50"));
        account.setUserId(10L);

        assertEquals(1L, account.getId());
        assertEquals("12345", account.getAccountNumber());
        assertEquals("001", account.getAgency());
        assertEquals(new BigDecimal("100.50"), account.getBalance());
        assertEquals(10L, account.getUserId());
    }

    @Test
    void testFullConstructor() {
        Account account = new Account(
                1L,
                "98765",
                "002",
                new BigDecimal("500.00"),
                20L
        );

        assertEquals(1L, account.getId());
        assertEquals("98765", account.getAccountNumber());
        assertEquals("002", account.getAgency());
        assertEquals(new BigDecimal("500.00"), account.getBalance());
        assertEquals(20L, account.getUserId());
    }

    @Test
    void testConstructorWithoutId() {
        Account account = new Account(
                "77777",
                "003",
                new BigDecimal("250.00"),
                30L
        );

        assertNull(account.getId());
        assertEquals("77777", account.getAccountNumber());
        assertEquals("003", account.getAgency());
        assertEquals(new BigDecimal("250.00"), account.getBalance());
        assertEquals(30L, account.getUserId());
    }

    @Test
    void testToString() {
        Account account = new Account(
                9L,
                "11111",
                "004",
                new BigDecimal("999.99"),
                99L
        );

        String result = account.toString();

        assertTrue(result.contains("id=9"));
        assertTrue(result.contains("accountNumber=11111"));
        assertTrue(result.contains("agency=004"));
        assertTrue(result.contains("balance=999.99"));
        assertTrue(result.contains("userId=99"));
    }
}
