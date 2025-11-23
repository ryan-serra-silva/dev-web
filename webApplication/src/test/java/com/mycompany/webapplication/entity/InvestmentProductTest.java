package com.mycompany.webapplication.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class InvestmentProductTest {

    @Test
    void testEmptyConstructorAndSetters() {
        InvestmentProduct product = new InvestmentProduct();

        product.setId(1L);
        product.setTypeInvestment(InvestmentType.CDB);
        product.setReturnRate(new BigDecimal("0.15"));

        assertEquals(1L, product.getId());
        assertEquals(InvestmentType.CDB, product.getTypeInvestment());
        assertEquals(new BigDecimal("0.15"), product.getReturnRate());
    }

    @Test
    void testFullConstructor() {
        InvestmentProduct product = new InvestmentProduct(
                10L,
                InvestmentType.CDB,
                new BigDecimal("0.12")
        );

        assertEquals(10L, product.getId());
        assertEquals(InvestmentType.CDB, product.getTypeInvestment());
        assertEquals(new BigDecimal("0.12"), product.getReturnRate());
    }
}

