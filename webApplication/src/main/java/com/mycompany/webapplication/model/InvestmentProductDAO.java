package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.InvestmentProduct;
import com.mycompany.webapplication.entity.InvestmentType;

import java.sql.*;
import java.util.ArrayList;

public class InvestmentProductDAO implements Dao<InvestmentProduct> {

    private final JDBC jdbc;

    public InvestmentProductDAO(JDBC jdbc) {
        this.jdbc = jdbc;
    }

    // ---------------------------------
    // GET BY ID
    // ---------------------------------
    @Override
    public InvestmentProduct get(int id) {
        InvestmentProduct product = null;
        String sql = "SELECT * FROM investment_product WHERE id = ?";

        try (Connection conn = jdbc.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    product = parseResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    // ---------------------------------
    // GET BY TYPE
    // ---------------------------------
    public InvestmentProduct getByType(InvestmentType type) {
        InvestmentProduct product = null;
        String sql = "SELECT * FROM investment_product WHERE type_investment = ?";

        try (Connection conn = jdbc.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    product = parseResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    // ---------------------------------
    // GET ALL
    // ---------------------------------
    @Override
    public ArrayList<InvestmentProduct> getAll() {
        ArrayList<InvestmentProduct> products = new ArrayList<>();
        String sql = "SELECT * FROM investment_product";

        try (Connection conn = jdbc.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(parseResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    // ---------------------------------
    // INSERT
    // ---------------------------------
    @Override
    public void insert(InvestmentProduct product) {
        String sql = "INSERT INTO investment_product (type_investment, return_rate) VALUES (?, ?)";

        try (Connection conn = jdbc.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getTypeInvestment().name());
            stmt.setBigDecimal(2, product.getReturnRate());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------
    // UPDATE
    // ---------------------------------
    @Override
    public void update(InvestmentProduct product) {
        String sql = "UPDATE investment_product SET type_investment = ?, return_rate = ? WHERE id = ?";

        try (Connection conn = jdbc.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getTypeInvestment().name());
            stmt.setBigDecimal(2, product.getReturnRate());
            stmt.setLong(3, product.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------
    // DELETE
    // ---------------------------------
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM investment_product WHERE id = ?";

        try (Connection conn = jdbc.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------
    // RESULTSET → ENTITY MAPPER
    // ---------------------------------
    private InvestmentProduct parseResultSet(ResultSet rs) throws SQLException {
        InvestmentProduct product = new InvestmentProduct();
        product.setId(rs.getLong("id"));
        product.setTypeInvestment(InvestmentType.valueOf(rs.getString("type_investment")));
        product.setReturnRate(rs.getBigDecimal("return_rate"));
        return product;
    }
}
