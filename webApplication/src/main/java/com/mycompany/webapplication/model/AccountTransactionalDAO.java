package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.*;
import java.sql.*;
import java.util.ArrayList;

public class AccountTransactionalDAO implements Dao<AccountTransactional> {

    private final JDBC jdbc;

    public AccountTransactionalDAO(JDBC jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public AccountTransactional get(int id) {
        String query = "SELECT * FROM transactions WHERE id = ?";

        try (Connection conn = jdbc.getConexao();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? parseResultSet(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transação", e);
        }
    }

    public ArrayList<AccountTransactional> getAllByAccountId(long accountId) {

        String query = """
                SELECT * FROM transactions 
                WHERE account_id = ? 
                ORDER BY timestamp DESC
                """;

        ArrayList<AccountTransactional> lista = new ArrayList<>();

        try (Connection conn = jdbc.getConexao();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(parseResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transações da conta", e);
        }

        return lista;
    }

    @Override
    public ArrayList<AccountTransactional> getAll() {

        String query = "SELECT * FROM transactions";

        ArrayList<AccountTransactional> lista = new ArrayList<>();

        try (Connection conn = jdbc.getConexao();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(parseResultSet(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as transações", e);
        }

        return lista;
    }

    @Override
    public void insert(AccountTransactional transacao) {

        if (transacao.getAccount() == null || transacao.getAccount().getId() == null) {
            throw new IllegalArgumentException("Conta da transação não pode ser nula");
        }

        String query = """
                INSERT INTO transactions 
                (type_transaction, amount, timestamp, description, account_id) 
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = jdbc.getConexao();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, transacao.getTypeTransaction().name());
            ps.setBigDecimal(2, transacao.getAmount());
            ps.setTimestamp(3, Timestamp.valueOf(transacao.getTimestamp()));
            ps.setString(4, transacao.getDescription());
            ps.setLong(5, transacao.getAccount().getId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    transacao.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir transação", e);
        }
    }

    @Override
    public void update(AccountTransactional transacao) {

        String query = """
                UPDATE transactions 
                SET type_transaction = ?, amount = ?, timestamp = ?, description = ?, account_id = ? 
                WHERE id = ?
                """;

        try (Connection conn = jdbc.getConexao();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, transacao.getTypeTransaction().name());
            ps.setBigDecimal(2, transacao.getAmount());
            ps.setTimestamp(3, Timestamp.valueOf(transacao.getTimestamp()));
            ps.setString(4, transacao.getDescription());
            ps.setLong(5, transacao.getAccount().getId());
            ps.setLong(6, transacao.getId());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("Transação não encontrada para atualização");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar transação", e);
        }
    }

    @Override
    public void delete(int id) {

        String query = "DELETE FROM transactions WHERE id = ?";

        try (Connection conn = jdbc.getConexao();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar transação", e);
        }
    }

    AccountTransactional parseResultSet(ResultSet rs) throws SQLException {

        AccountTransactional t = new AccountTransactional();

        t.setId(rs.getLong("id"));
        t.setTypeTransaction(TransactionType.valueOf(rs.getString("type_transaction")));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        t.setDescription(rs.getString("description"));

        Account conta = new Account();
        conta.setId(rs.getLong("account_id"));

        t.setAccount(conta);

        return t;
    }
}
