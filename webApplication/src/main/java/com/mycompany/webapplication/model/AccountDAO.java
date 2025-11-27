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
            System.err.println("Erro ao buscar conta por id: " + e.getMessage());
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
                accounts.add(
                        new Account(
                                resultado.getLong("id"),
                                resultado.getString("account_number"),
                                resultado.getString("agency"),
                                resultado.getBigDecimal("balance"),
                                resultado.getLong("user_id")
                        )
                );
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar contas: " + e.getMessage());
        }

        return accounts;
    }

    public Account getByUserId(Long userId, Connection conn) throws SQLException {
        Account account = null;
        String query = "SELECT * FROM Account WHERE user_id = ? FOR UPDATE";

        try (PreparedStatement sql = conn.prepareStatement(query)) {
            sql.setLong(1, userId);

            try (ResultSet rs = sql.executeQuery()) {
                if (rs.next()) {
                    account = new Account(
                            rs.getLong("id"),
                            rs.getString("account_number"),
                            rs.getString("agency"),
                            rs.getBigDecimal("balance"),
                            rs.getLong("user_id")
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
            System.err.println("Erro ao inserir conta: " + e.getMessage());
        }
    }

    public void update(Account account, Connection conn) throws SQLException {
        String query = "UPDATE Account SET account_number = ?, agency = ?, balance = ?, user_id = ? WHERE id = ?";

        try (PreparedStatement sql = conn.prepareStatement(query)) {
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
            System.err.println("Erro ao atualizar conta: " + e.getMessage());
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
            System.err.println("Erro ao deletar conta: " + e.getMessage());
        }
    }
}