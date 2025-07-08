package com.mycompany.webapplication.model;
import com.mycompany.webapplication.entity.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;

public class AccountDAO implements Dao<Account> {

    @Override
    public Account get(int id) {
        JDBC conexao = new JDBC();
        Account account = null;
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("SELECT * FROM Account WHERE id = ?")) {
            sql.setInt(1, id);
            ResultSet resultado = sql.executeQuery();
            if (resultado.next()) {
                account = new Account(
                    resultado.getLong("id"),
                    resultado.getString("account_number"),
                    resultado.getString("agency"),
                    resultado.getBigDecimal("balance"),
                    resultado.getLong("user_id")
                );
            }
        } catch (SQLException e) {
            System.err.println("Query de select (get account) incorreta: " + e.getMessage());
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public ArrayList<Account> getAll() {
        ArrayList<Account> accounts = new ArrayList<>();
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("SELECT * FROM Account")) {
            ResultSet resultado = sql.executeQuery();
            while (resultado.next()) {
                Account account = new Account(
                    resultado.getLong("id"),
                    resultado.getString("account_number"),
                    resultado.getString("agency"),
                    resultado.getBigDecimal("balance"),
                    resultado.getLong("user_id")
                );
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.err.println("Query de select (getAll accounts) incorreta: " + e.getMessage());
            e.printStackTrace();
        }
        return accounts;
    }

    public Account getByUserId(Long userId, Connection conn) throws SQLException {
        Account account = null;
        String sqlQuery = "SELECT * FROM Account WHERE user_id = ? FOR UPDATE"; // FOR UPDATE para travar a linha
        try (PreparedStatement sql = conn.prepareStatement(sqlQuery)) {
            sql.setLong(1, userId);
            try (ResultSet resultado = sql.executeQuery()) {
                if (resultado.next()) {
                    account = new Account(
                        resultado.getLong("id"),
                        resultado.getString("account_number"),
                        resultado.getString("agency"),
                        resultado.getBigDecimal("balance"),
                        resultado.getLong("user_id")
                    );
                }
            }
        }
        return account;
    }

    public Account getByUserId(Long userId) {
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao()) {
            return getByUserId(userId, conn);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar conta por userId: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void insert(Account account) {
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement(
                "INSERT INTO Account (account_number, agency, balance, user_id) VALUES (?, ?, ?, ?)")) {
            sql.setString(1, account.getAccountNumber());
            sql.setString(2, account.getAgency());
            sql.setBigDecimal(3, account.getBalance());
            sql.setLong(4, account.getUserId());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de insert (account) incorreta: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void update(Account account, Connection conn) throws SQLException {
        String sqlQuery = "UPDATE Account SET account_number = ?, agency = ?, balance = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement sql = conn.prepareStatement(sqlQuery)) {
            sql.setString(1, account.getAccountNumber());
            sql.setString(2, account.getAgency());
            sql.setBigDecimal(3, account.getBalance());
            sql.setLong(4, account.getUserId());
            sql.setLong(5, account.getId());
            sql.executeUpdate();
        }
    }

    @Override
    public void update(Account account) {
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao()) {
             update(account, conn);
        } catch (SQLException e) {
            System.err.println("Query de update (account) incorreta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("DELETE FROM Account WHERE id = ?")) {
            sql.setInt(1, id);
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de delete (account) incorreta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}