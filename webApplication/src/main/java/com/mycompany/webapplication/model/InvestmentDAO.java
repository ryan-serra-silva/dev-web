package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.Investment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvestmentDAO implements Dao<Investment> {

    private final JDBC jdbc;
    private final AccountDAO accountDAO;
    private final InvestmentTransactionalDAO investmentProductDAO;

    public InvestmentDAO(JDBC jdbc, AccountDAO accountDAO, InvestmentTransactionalDAO investmentProductDAO) {
        this.jdbc = jdbc;
        this.accountDAO = accountDAO;
        this.investmentProductDAO = investmentProductDAO;
    }

    private Connection conn() throws SQLException {
        return jdbc.getConexao();
    }

    @Override
    public Investment get(int id) {
        Investment inv = null;

        try (Connection c = conn();
             PreparedStatement sql = c.prepareStatement("SELECT * FROM investment WHERE id = ?")) {

            sql.setInt(1, id);
            try (ResultSet r = sql.executeQuery()) {
                if (r.next()) {
                    inv = mapResultSetToInvestment(r);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar investimento: " + e.getMessage());
        }

        return inv;
    }

    public Investment getById(long id) {
        return get((int) id);
    }

    @Override
    public ArrayList<Investment> getAll() {
        ArrayList<Investment> list = new ArrayList<>();

        try (Connection c = conn();
             PreparedStatement sql = c.prepareStatement("SELECT * FROM investment");
             ResultSet r = sql.executeQuery()) {

            while (r.next()) {
                list.add(mapResultSetToInvestment(r));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos investimentos: " + e.getMessage());
        }

        return list;
    }

    public List<Investment> getAllByAccountId(long accountId) {
        List<Investment> list = new ArrayList<>();

        String query = "SELECT * FROM investment WHERE account_id = ? ORDER BY start_date DESC";

        try (Connection c = conn();
             PreparedStatement sql = c.prepareStatement(query)) {

            sql.setLong(1, accountId);

            try (ResultSet r = sql.executeQuery()) {
                while (r.next()) {
                    list.add(mapResultSetToInvestment(r));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar investimentos: " + e.getMessage());
        }

        return list;
    }

    @Override
    public void insert(Investment investment) {
        try (Connection c = conn()) {
            insert(investment, c);
        } catch (SQLException e) {
            System.err.println("Erro ao inserir investimento: " + e.getMessage());
        }
    }

    public void insert(Investment investment, Connection c) throws SQLException {
        String q = "INSERT INTO investment (amount, start_date, end_date, account_id, investment_product_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement sql = c.prepareStatement(q)) {

            sql.setBigDecimal(1, investment.getAmount());
            sql.setDate(2, Date.valueOf(investment.getStartDate()));
            sql.setDate(3, Date.valueOf(investment.getEndDate()));
            sql.setLong(4, investment.getAccount().getId());
            sql.setLong(5, investment.getInvestmentProduct().getId());

            sql.executeUpdate();
        }
    }

    @Override
    public void update(Investment investment) {
        String q = "UPDATE investment SET amount=?, start_date=?, end_date=?, account_id=?, investment_product_id=? WHERE id=?";

        try (Connection c = conn();
             PreparedStatement sql = c.prepareStatement(q)) {

            sql.setBigDecimal(1, investment.getAmount());
            sql.setDate(2, Date.valueOf(investment.getStartDate()));
            sql.setDate(3, Date.valueOf(investment.getEndDate()));
            sql.setLong(4, investment.getAccount().getId());
            sql.setLong(5, investment.getInvestmentProduct().getId());
            sql.setLong(6, investment.getId());

            sql.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar investimento: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String q = "DELETE FROM investment WHERE id = ?";

        try (Connection c = conn();
             PreparedStatement sql = c.prepareStatement(q)) {

            sql.setInt(1, id);
            sql.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao deletar investimento: " + e.getMessage());
        }
    }

    private Investment mapResultSetToInvestment(ResultSet r) throws SQLException {
        Investment i = new Investment();

        i.setId(r.getLong("id"));
        i.setAmount(r.getBigDecimal("amount"));

        Date sd = r.getDate("start_date");
        if (sd != null) i.setStartDate(sd.toLocalDate());

        Date ed = r.getDate("end_date");
        if (ed != null) i.setEndDate(ed.toLocalDate());

        long accId = r.getLong("account_id");
        long prodId = r.getLong("investment_product_id");

        i.setAccount(accountDAO.get((int) accId));
        i.setInvestmentProduct(investmentProductDAO.get((int) prodId));

        return i;
    }
}