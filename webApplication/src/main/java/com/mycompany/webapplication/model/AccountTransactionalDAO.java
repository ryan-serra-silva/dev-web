package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.AccountTransactional;
import com.mycompany.webapplication.entity.TransactionType;

import java.sql.*;
import java.util.ArrayList;

// Mantendo a implementação da interface Dao como solicitado
public class AccountTransactionalDAO implements Dao<AccountTransactional> {

    @Override
    public AccountTransactional get(int id) { // Usando int como na interface
        JDBC conexao = new JDBC();
        AccountTransactional transacao = null;
        try (PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM transactions WHERE id = ?")) {
            sql.setInt(1, id);
            try (ResultSet rs = sql.executeQuery()) {
                if (rs.next()) {
                    transacao = parseResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar transação: " + e.getMessage());
            e.printStackTrace(); // Linha CRÍTICA para depuração
        } finally {
            conexao.closeConexao();
        }
        return transacao;
    }

    public ArrayList<AccountTransactional> getAllByAccountId(long accountId) {
        JDBC conexao = new JDBC();
        ArrayList<AccountTransactional> lista = new ArrayList<>();
        try (PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp DESC")) {
            sql.setLong(1, accountId);
            try (ResultSet rs = sql.executeQuery()) {
                while (rs.next()) {
                    lista.add(parseResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar transações por conta: " + e.getMessage());
            e.printStackTrace(); // Linha CRÍTICA para depuração
        } finally {
            conexao.closeConexao();
        }
        return lista;
    }

    @Override
    public ArrayList<AccountTransactional> getAll() {
        JDBC conexao = new JDBC();
        ArrayList<AccountTransactional> lista = new ArrayList<>();
        try (PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM transactions");
             ResultSet rs = sql.executeQuery()) {
            while (rs.next()) {
                lista.add(parseResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todas as transações: " + e.getMessage());
            e.printStackTrace(); // Linha CRÍTICA para depuração
        } finally {
            conexao.closeConexao();
        }
        return lista;
    }

    @Override
    public void insert(AccountTransactional transacao) {
        if (transacao.getAccount() == null || transacao.getAccount().getId() == null) {
            System.err.println("Erro ao inserir transação: a conta associada (Account) ou seu ID é nulo.");
            return;
        }

        JDBC conexao = new JDBC();
        try (PreparedStatement sql = conexao.getConexao().prepareStatement(
                "INSERT INTO transactions (type_transaction, amount, timestamp, description, account_id) VALUES (?, ?, ?, ?, ?)")) {
            
            sql.setString(1, transacao.getTypeTransaction().name());
            sql.setBigDecimal(2, transacao.getAmount());
            sql.setTimestamp(3, Timestamp.valueOf(transacao.getTimestamp()));
            sql.setString(4, transacao.getDescription());
            sql.setLong(5, transacao.getAccount().getId());

            int affectedRows = sql.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("Falha ao inserir transação, nenhuma linha afetada.");
            }

        } catch (SQLException e) {
            System.err.println("ERRO FATAL DE SQL AO INSERIR TRANSAÇÃO: " + e.getMessage());
            e.printStackTrace(); // Linha CRÍTICA para depuração
        } finally {
            conexao.closeConexao();
        }
    }

    @Override
    public void update(AccountTransactional transacao) {
        JDBC conexao = new JDBC();
        try (PreparedStatement sql = conexao.getConexao().prepareStatement(
                "UPDATE transactions SET type_transaction = ?, amount = ?, timestamp = ?, description = ?, account_id = ? WHERE id = ?")) {
            
            sql.setString(1, transacao.getTypeTransaction().name());
            sql.setBigDecimal(2, transacao.getAmount());
            sql.setTimestamp(3, Timestamp.valueOf(transacao.getTimestamp()));
            sql.setString(4, transacao.getDescription());
            sql.setLong(5, transacao.getAccount().getId());
            sql.setLong(6, transacao.getId());
            sql.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar transação: " + e.getMessage());
            e.printStackTrace(); // Linha CRÍTICA para depuração
        } finally {
            conexao.closeConexao();
        }
    }

    @Override
    public void delete(int id) { // Usando int como na interface
        JDBC conexao = new JDBC();
        try (PreparedStatement sql = conexao.getConexao().prepareStatement("DELETE FROM transactions WHERE id = ?")) {
            sql.setInt(1, id);
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar transação: " + e.getMessage());
            e.printStackTrace(); // Linha CRÍTICA para depuração
        } finally {
            conexao.closeConexao();
        }
    }

    private AccountTransactional parseResultSet(ResultSet rs) throws SQLException {
        AccountTransactional transacao = new AccountTransactional();
        transacao.setId(rs.getLong("id"));
        transacao.setTypeTransaction(TransactionType.valueOf(rs.getString("type_transaction")));
        transacao.setAmount(rs.getBigDecimal("amount"));
        transacao.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        transacao.setDescription(rs.getString("description"));

        Account account = new Account();
        account.setId(rs.getLong("account_id"));
        transacao.setAccount(account);

        return transacao;
    }
}