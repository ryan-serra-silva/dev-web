/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.webapplication.model;


import com.mycompany.webapplication.entity.Investment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
/**
 *
 * @author ryan
 */


public class InvestmentDAO implements Dao<Investment> {

    private final AccountDAO accountDAO = new AccountDAO();
    private final InvestmentProductDAO investmentProductDAO = new InvestmentProductDAO();

    @Override
    public Investment get(int id) {
        JDBC conexao = new JDBC();
        Investment investment = null;
        try (PreparedStatement sql = conexao.getConexao().prepareStatement(
                "SELECT * FROM Investment WHERE id = ?")) {
            sql.setInt(1, id);
            try (ResultSet result = sql.executeQuery()) {
                if (result.next()) {
                    investment = new Investment();
                    investment.setId(result.getLong("id"));
                    investment.setAmount(result.getBigDecimal("amount"));
                    Date startDate = result.getDate("startDate");
                    if (startDate != null) investment.setStartDate(startDate.toLocalDate());
                    Date endDate = result.getDate("endDate");
                    if (endDate != null) investment.setEndDate(endDate.toLocalDate());

                    Long accountId = result.getLong("accountId");
                    Long productId = result.getLong("investmentProductId");

                    investment.setAccount(accountDAO.get(accountId.intValue()));
                    investment.setInvestmentProduct(investmentProductDAO.get(productId.intValue()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar investimento:");
        } finally {
            conexao.closeConexao();
        }
        return investment;
    }

    @Override
    public ArrayList<Investment> getAll() {
        JDBC conexao = new JDBC();
        ArrayList<Investment> investments = new ArrayList<>();
        try (PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM Investment");
             ResultSet result = sql.executeQuery()) {
            while (result.next()) {
                Investment investment = new Investment();
                investment.setId(result.getLong("id"));
                investment.setAmount(result.getBigDecimal("amount"));

                Date startDate = result.getDate("startDate");
                if (startDate != null) investment.setStartDate(startDate.toLocalDate());
                Date endDate = result.getDate("endDate");
                if (endDate != null) investment.setEndDate(endDate.toLocalDate());

                Long accountId = result.getLong("accountId");
                Long productId = result.getLong("investmentProductId");

                investment.setAccount(accountDAO.get(accountId.intValue()));
                investment.setInvestmentProduct(investmentProductDAO.get(productId.intValue()));

                investments.add(investment);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os investimentos:");
        } finally {
            conexao.closeConexao();
        }
        return investments;
    }

    @Override
    public void insert(Investment investment) {
        if (investment.getAccount() == null || investment.getInvestmentProduct() == null) {
            System.err.println("Conta ou Produto de Investimento não podem ser nulos.");
            return;
        }
        JDBC conexao = new JDBC();
        try (PreparedStatement sql = conexao.getConexao().prepareStatement(
                "INSERT INTO Investment (amount, startDate, endDate, accountId, investmentProductId) VALUES (?, ?, ?, ?, ?)")) {
            sql.setBigDecimal(1, investment.getAmount());
            sql.setDate(2, Date.valueOf(investment.getStartDate()));
            sql.setDate(3, Date.valueOf(investment.getEndDate()));
            sql.setLong(4, investment.getAccount().getId());
            sql.setLong(5, investment.getInvestmentProduct().getId());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir investimento:");
        } finally {
            conexao.closeConexao();
        }
    }

    @Override
    public void update(Investment investment) {
        if (investment.getAccount() == null || investment.getInvestmentProduct() == null) {
            System.err.println("Conta ou Produto de Investimento não podem ser nulos.");
            return;
        }
        JDBC conexao = new JDBC();
        try (PreparedStatement sql = conexao.getConexao().prepareStatement(
                "UPDATE Investment SET amount = ?, startDate = ?, endDate = ?, accountId = ?, investmentProductId = ? WHERE id = ?")) {
            sql.setBigDecimal(1, investment.getAmount());
            sql.setDate(2, Date.valueOf(investment.getStartDate()));
            sql.setDate(3, Date.valueOf(investment.getEndDate()));
            sql.setLong(4, investment.getAccount().getId());
            sql.setLong(5, investment.getInvestmentProduct().getId());
            sql.setLong(6, investment.getId());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar investimento:");
            e.printStackTrace();
        } finally {
            conexao.closeConexao();
        }
    }

    @Override
    public void delete(int id) {
        JDBC conexao = new JDBC();
        try (PreparedStatement sql = conexao.getConexao().prepareStatement("DELETE FROM Investment WHERE id = ?")) {
            sql.setInt(1, id);
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar investimento:");
            e.printStackTrace();
        } finally {
            conexao.closeConexao();
        }
    }
}