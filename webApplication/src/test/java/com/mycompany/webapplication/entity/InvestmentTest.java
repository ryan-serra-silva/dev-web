package com.mycompany.webapplication.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class InvestmentTest {

    @Test
    void testSettersAndGetters() {

        Investment investment = new Investment();

        Account account = new Account(
                1L,
                "12345",
                "001",
                new BigDecimal("500.00"),
                10L
        );

        InvestmentProduct product = new InvestmentProduct();
        // Assumindo que InvestmentProduct possui setters (adapte se necessário)
        product.setId(2L);

        LocalDate start = LocalDate.of(2024, 1, 10);
        LocalDate end = LocalDate.of(2024, 12, 31);

        investment.setId(1L);
        investment.setAmount(new BigDecimal("1000.00"));
        investment.setStartDate(start);
        investment.setEndDate(end);
        investment.setAccount(account);
        investment.setInvestmentProduct(product);

        assertEquals(1L, investment.getId());
        assertEquals(new BigDecimal("1000.00"), investment.getAmount());
        assertEquals(start, investment.getStartDate());
        assertEquals(end, investment.getEndDate());
        assertEquals(account, investment.getAccount());
        assertEquals(product, investment.getInvestmentProduct());
    }
}
