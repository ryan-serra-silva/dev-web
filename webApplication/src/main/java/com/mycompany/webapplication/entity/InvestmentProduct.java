package com.mycompany.webapplication.entity;

import java.math.BigDecimal;

public class InvestmentProduct {
    private Long id;
    private InvestmentType typeInvestment;
    private BigDecimal returnRate;

    public InvestmentProduct() {
    }

    public InvestmentProduct(Long id, InvestmentType typeInvestment, BigDecimal returnRate) {
        this.id = id;
        this.typeInvestment = typeInvestment;
        this.returnRate = returnRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InvestmentType getTypeInvestment() {
        return typeInvestment;
    }

    public void setTypeInvestment(InvestmentType typeInvestment) {
        this.typeInvestment = typeInvestment;
    }

    public BigDecimal getReturnRate() {
        return returnRate;
    }

    public void setReturnRate(BigDecimal returnRate) {
        this.returnRate = returnRate;
    }
}