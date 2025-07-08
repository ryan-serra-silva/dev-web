package com.mycompany.webapplication.entity;

import java.math.BigDecimal;

public class InvestmentProduct {
    private Long id;
    private InvestmentType typeInvestiment;
    private BigDecimal returnRate;

    public InvestmentProduct() {
    }

    public InvestmentProduct(Long id, InvestmentType typeInvestiment, BigDecimal returnRate) {
        this.id = id;
        this.typeInvestiment = typeInvestiment;
        this.returnRate = returnRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // --- CORREÇÃO APLICADA AQUI ---
    public InvestmentType getTypeInvestiment() {
        // O método estava lançando uma exceção. Agora ele retorna o valor correto.
        return typeInvestiment;
    }

    // --- CORREÇÃO APLICADA AQUI ---
    public void setTypeInvestiment(InvestmentType typeInvestiment) {
        // O método estava lançando uma exceção. Agora ele define o valor correto.
        this.typeInvestiment = typeInvestiment;
    }

    public BigDecimal getReturnRate() {
        return returnRate;
    }

    public void setReturnRate(BigDecimal returnRate) {
        this.returnRate = returnRate;
    }
}