package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.Investment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class InvestmentDAO implements Dao<Investment> {

    private final AccountDAO accountDAO = new AccountDAO();
    private final InvestmentProductDAO investmentProductDAO = new InvestmentProductDAO();

    public List<Investment> getAllByAccountId(long accountId) {
        List<Investment> investments = new ArrayList<>();
        JDBC conexao = new JDBC();
        String sqlQuery = "SELECT * FROM investment WHERE account_id = ? ORDER BY start_date DESC";
        
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement(sqlQuery)) {
            
            sql.setLong(1, accountId);
            try (ResultSet result = sql.executeQuery()) {
                while (result.next()) {
                    Investment investment = new Investment();
                    investment.setId(result.getLong("id"));
                    investment.setAmount(result.getBigDecimal("amount"));
                    
                    Date startDate = result.getDate("start_date");
                    if (startDate != null) investment.setStartDate(startDate.toLocalDate());
                    
                    Date endDate = result.getDate("end_date");
                    if (endDate != null) investment.setEndDate(endDate.toLocalDate());
                    

                    Long accId = result.getLong("account_id");
                    Long prodId = result.getLong("investment_product_id");
                    
                    investment.setAccount(accountDAO.get(accId.intValue()));
                    investment.setInvestmentProduct(investmentProductDAO.get(prodId.intValue()));
                    
                    investments.add(investment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar investimentos por ID da conta:");
            e.printStackTrace();
        }
        return investments;
    }
    
    @Override
    public Investment get(int id) {
        JDBC conexao = new JDBC();
        Investment investment = null;
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("SELECT * FROM investment WHERE id = ?")) {
            sql.setInt(1, id);
            try (ResultSet result = sql.executeQuery()) {
                if (result.next()) {
                    investment = new Investment();
                    investment.setId(result.getLong("id"));
                    investment.setAmount(result.getBigDecimal("amount"));
                    Date startDate = result.getDate("start_date");
                    if (startDate != null) investment.setStartDate(startDate.toLocalDate());
                    Date endDate = result.getDate("end_date");
                    if (endDate != null) investment.setEndDate(endDate.toLocalDate());
                    Long accountId = result.getLong("account_id");
                    Long productId = result.getLong("investment_product_id");
                    investment.setAccount(accountDAO.get(accountId.intValue()));
                    investment.setInvestmentProduct(investmentProductDAO.get(productId.intValue()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar investimento:");
            e.printStackTrace();
        }
        return investment;
    }
    
    public Investment getById(long id) {
    JDBC conexao = new JDBC();
    Investment investment = null;
    try (Connection conn = conexao.getConexao();
         PreparedStatement sql = conn.prepareStatement("SELECT * FROM investment WHERE id = ?")) {
        sql.setLong(1, id);
        try (ResultSet result = sql.executeQuery()) {
            if (result.next()) {
                investment = new Investment();
                investment.setId(result.getLong("id"));
                investment.setAmount(result.getBigDecimal("amount"));

                Date startDate = result.getDate("start_date");
                if (startDate != null) investment.setStartDate(startDate.toLocalDate());

                Date endDate = result.getDate("end_date");
                if (endDate != null) investment.setEndDate(endDate.toLocalDate());

                Long accountId = result.getLong("account_id");
                Long productId = result.getLong("investment_product_id");

                investment.setAccount(accountDAO.get(accountId.intValue()));
                investment.setInvestmentProduct(investmentProductDAO.get(productId.intValue()));
            }
        }
    } catch (SQLException e) {
        System.err.println("Erro ao buscar investimento por ID:");
        e.printStackTrace();
    }
    return investment;
}
    @Override
    public ArrayList<Investment> getAll() {
        JDBC conexao = new JDBC();
        ArrayList<Investment> investments = new ArrayList<>();
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("SELECT * FROM investment");
             ResultSet result = sql.executeQuery()) {
            while (result.next()) {
                Investment investment = new Investment();
                investment.setId(result.getLong("id"));
                investment.setAmount(result.getBigDecimal("amount"));
                Date startDate = result.getDate("start_date");
                if (startDate != null) investment.setStartDate(startDate.toLocalDate());
                Date endDate = result.getDate("end_date");
                if (endDate != null) investment.setEndDate(endDate.toLocalDate());
                Long accountId = result.getLong("account_id");
                Long productId = result.getLong("investment_product_id");
                investment.setAccount(accountDAO.get(accountId.intValue()));
                investment.setInvestmentProduct(investmentProductDAO.get(productId.intValue()));
                investments.add(investment);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os investimentos:");
            e.printStackTrace();
        }
        return investments;
    }

    public void insert(Investment investment, Connection conn) throws SQLException {
        if (investment.getAccount() == null || investment.getAccount().getId() == null) {
            throw new SQLException("A conta associada ao investimento é nula.");
        }
        if (investment.getInvestmentProduct() == null || investment.getInvestmentProduct().getId() == null) {
            throw new SQLException("O produto de investimento associado é nulo.");
        }
        String sqlQuery = "INSERT INTO investment (amount, start_date, end_date, account_id,  investment_product_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement sql = conn.prepareStatement(sqlQuery)) {
            sql.setBigDecimal(1, investment.getAmount());
            sql.setDate(2, Date.valueOf(investment.getStartDate()));
            sql.setDate(3, Date.valueOf(investment.getEndDate()));
            sql.setLong(4, investment.getAccount().getId());
            sql.setLong(5, investment.getInvestmentProduct().getId());
            int affectedRows = sql.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir investimento, nenhuma linha foi afetada.");
            }
        }
    }

    @Override
    public void insert(Investment investment) {
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao()) {
            insert(investment, conn);
        } catch (SQLException e) {
            System.err.println("Erro ao inserir investimento (operação isolada):");
            e.printStackTrace();
        }
    }

    @Override
    public void update(Investment investment) {
        if (investment.getAccount() == null || investment.getInvestmentProduct() == null) {
            System.err.println("Conta ou Produto de Investimento não podem ser nulos.");
            return;
        }
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement(
                "UPDATE investment SET amount = ?, start_date = ?, end_date = ?, account_id = ?, investment_product_id = ? WHERE id = ?")) {
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
        }
    }

    @Override
    public void delete(int id) {
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("DELETE FROM investment WHERE id = ?")) {
            sql.setInt(1, id);
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar investimento:");
            e.printStackTrace();
        }
    }
}