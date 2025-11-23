package com.mycompany.webapplication.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class InvestmentTransactionalTest {

    @Test
    void testSettersAndGetters() {

        InvestmentTransactional it = new InvestmentTransactional();

        Investment investment = new Investment();
        investment.setId(5L);
        investment.setAmount(new BigDecimal("1000.00"));
        investment.setStartDate(LocalDate.of(2024, 1, 1));
        investment.setEndDate(LocalDate.of(2024, 12, 31));

        LocalDateTime now = LocalDateTime.of(2025, 1, 20, 10, 45);

        it.setId(1L);
        it.setType(InvestmentTransactionalType.INVEST);
        it.setAmount(new BigDecimal("300.00"));
        it.setTimestamp(now);
        it.setDescription("Aplicação em investimento");
        it.setInvestment(investment);

        assertEquals(1L, it.getId());
        assertEquals(InvestmentTransactionalType.INVEST, it.getType());
        assertEquals(new BigDecimal("300.00"), it.getAmount());
        assertEquals(now, it.getTimestamp());
        assertEquals("Aplicação em investimento", it.getDescription());
        assertEquals(investment, it.getInvestment());
    }
}
