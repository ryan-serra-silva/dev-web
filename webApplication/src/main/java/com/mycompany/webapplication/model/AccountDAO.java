/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.webapplication.model;
import com.mycompany.webapplication.entity.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author ryan
 */
public class AccountDAO implements Dao<Account> {

    @Override
    public Account get(int id) {
        JDBC conexao = new JDBC();
        Account account = null;
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM Account WHERE id = ?");
            sql.setInt(1, id);
            ResultSet resultado = sql.executeQuery();
            if (resultado.next()) {
                account = new Account(
                    resultado.getLong("id"),
                    resultado.getString("accountNumber"),
                    resultado.getString("agency"),
                    resultado.getBigDecimal("balance"),
                    resultado.getLong("userId")
                );
            }
        } catch (SQLException e) {
            System.err.println("Query de select (get account) incorreta: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
        return account;
    }

    @Override
    public ArrayList<Account> getAll() {
        ArrayList<Account> accounts = new ArrayList<>();
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM Account");
            ResultSet resultado = sql.executeQuery();
            while (resultado.next()) {
                Account account = new Account(
                    resultado.getLong("id"),
                    resultado.getString("accountNumber"),
                    resultado.getString("agency"),
                    resultado.getBigDecimal("balance"),
                    resultado.getLong("userId")
                );
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.err.println("Query de select (getAll accounts) incorreta: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
        return accounts;
    }
    
    public Account getByUserId(Long userId) {
    JDBC conexao = new JDBC();
    Account account = null;
    try {
        PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM Account WHERE userId = ?");
        sql.setLong(1, userId);
        ResultSet resultado = sql.executeQuery();
        if (resultado.next()) {
            account = new Account(
                resultado.getLong("id"),
                resultado.getString("accountNumber"),
                resultado.getString("agency"),
                resultado.getBigDecimal("balance"),
                resultado.getLong("userId")
            );
        }
    } catch (SQLException e) {
        System.err.println("Erro ao buscar conta por userId: " + e.getMessage());
    } finally {
        conexao.closeConexao();
    }
    return account;
}


    @Override
    public void insert(Account account) {
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement(
                "INSERT INTO Account (accountNumber, agency, balance, userId) VALUES (?, ?, ?, ?)");
            sql.setString(1, account.getAccountNumber());
            sql.setString(2, account.getAgency());
            sql.setBigDecimal(3, account.getBalance());
            sql.setLong(4, account.getUserId());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de insert (account) incorreta: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
    }

    @Override
    public void update(Account account) {
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement(
                "UPDATE Account SET accountNumber = ?, agency = ?, balance = ?, userId = ? WHERE id = ?");
            sql.setString(1, account.getAccountNumber());
            sql.setString(2, account.getAgency());
            sql.setBigDecimal(3, account.getBalance());
            sql.setLong(4, account.getUserId());
            sql.setLong(5, account.getId());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de update (account) incorreta: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
    }

    @Override
    public void delete(int id) {
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("DELETE FROM Account WHERE id = ?");
            sql.setInt(1, id);
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de delete (account) incorreta: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
    }
}