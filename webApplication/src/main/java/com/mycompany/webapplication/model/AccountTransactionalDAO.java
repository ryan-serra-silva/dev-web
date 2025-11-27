package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.AccountTransactional;
import com.mycompany.webapplication.entity.TransactionType;

import java.sql.*;
import java.util.ArrayList;

public class AccountTransactionalDAO implements Dao<AccountTransactional> {

    @Override
    public AccountTransactional get(int id) {
        JDBC conexao = new JDBC();
        AccountTransactional transacao = null;

        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("SELECT * FROM transactions WHERE id = ?")) {

            sql.setInt(1, id);

            try (ResultSet rs = sql.executeQuery()) {
                if (rs.next()) {
                    transacao = parseResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar transação: " + e.getMessage());
        }

        return transacao;
    }

    public ArrayList<AccountTransactional> getAllByAccountId(long accountId) {
        JDBC conexao = new JDBC();
        ArrayList<AccountTransactional> lista = new ArrayList<>();

        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement(
                     "SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp DESC")) {

            sql.setLong(1, accountId);

            try (ResultSet rs = sql.executeQuery()) {
                while (rs.next()) {
                    lista.add(parseResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar transações por conta: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public ArrayList<AccountTransactional> getAll() {

        JDBC conexao = new JDBC();
        ArrayList<AccountTransactional> lista = new ArrayList<>();

        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("SELECT * FROM transactions");
             ResultSet rs = sql.executeQuery()) {

            while (rs.next()) {
                lista.add(parseResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todas as transações: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public void insert(AccountTransactional transacao) {

        if (transacao.getAccount() == null || transacao.getAccount().getId() == null) {
            System.err.println("Erro ao inserir transação: Account ou ID nulo.");
            return;
        }

        JDBC conexao = new JDBC();
        String sqlInsert =
                "INSERT INTO transactions (type_transaction, amount, timestamp, description, account_id) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            sql.setString(1, transacao.getTypeTransaction().name());
            sql.setBigDecimal(2, transacao.getAmount());
            sql.setTimestamp(3, Timestamp.valueOf(transacao.getTimestamp()));
            sql.setString(4, transacao.getDescription());
            sql.setLong(5, transacao.getAccount().getId());

            sql.executeUpdate();

            // retorna o ID gerado
            try (ResultSet keys = sql.getGeneratedKeys()) {
                if (keys.next()) {
                    transacao.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir transação: " + e.getMessage());
        }
    }

    @Override
    public void update(AccountTransactional transacao) {

        JDBC conexao = new JDBC();

        String sqlUpdate =
                "UPDATE transactions SET type_transaction = ?, amount = ?, timestamp = ?, description = ?, account_id = ? " +
                        "WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement(sqlUpdate)) {

            sql.setString(1, transacao.getTypeTransaction().name());
            sql.setBigDecimal(2, transacao.getAmount());
            sql.setTimestamp(3, Timestamp.valueOf(transacao.getTimestamp()));
            sql.setString(4, transacao.getDescription());
            sql.setLong(5, transacao.getAccount().getId());
            sql.setLong(6, transacao.getId());

            sql.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar transação: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {

        JDBC conexao = new JDBC();

        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("DELETE FROM transactions WHERE id = ?")) {

            sql.setInt(1, id);
            sql.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao deletar transação: " + e.getMessage());
        }
    }

    private AccountTransactional parseResultSet(ResultSet rs) throws SQLException {

        AccountTransactional transacao = new AccountTransactional();

        transacao.setId(rs.getLong("id"));
        transacao.setTypeTransaction(TransactionType.valueOf(rs.getString("type_transaction")));
        transacao.setAmount(rs.getBigDecimal("amount"));
        transacao.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        transacao.setDescription(rs.getString("description"));

        Account conta = new Account();
        conta.setId(rs.getLong("account_id"));

        transacao.setAccount(conta);
        return transacao;
    }
}
