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

public class InvestmentProduct {
   
    private Long id;

    private InvestmentType typeInvestment;

    private BigDecimal returnRate;

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
