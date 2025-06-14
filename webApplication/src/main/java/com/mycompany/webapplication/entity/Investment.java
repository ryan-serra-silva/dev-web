/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.webapplication.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author ryan
 */

public class Investment{

  
    private Long id;

    private BigDecimal amount;

    private LocalDate startDate;

 
    private LocalDate endDate;

    
    private Account account;


    private InvestmentProduct investmentProduct;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public LocalDate getStartDate() {
        return startDate;
    }


    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public LocalDate getEndDate() {
        return endDate;
    }


    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


    public Account getAccount() {
        return account;
    }


    public void setAccount(Account account) {
        this.account = account;
    }


    public InvestmentProduct getInvestmentProduct() {
        return investmentProduct;
    }


    public void setInvestmentProduct(InvestmentProduct investmentProduct) {
        this.investmentProduct = investmentProduct;
    }
}
