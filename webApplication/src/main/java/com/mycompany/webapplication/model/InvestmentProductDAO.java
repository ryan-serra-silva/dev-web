package com.mycompany.webapplication.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mycompany.webapplication.entity.InvestmentProduct;
import com.mycompany.webapplication.entity.InvestmentType;

public class InvestmentProductDAO implements Dao<InvestmentProduct> {

    @Override
    public InvestmentProduct get(int id) {
        JDBC conexao = new JDBC();
        InvestmentProduct product = null;
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("SELECT * FROM investment_product WHERE id = ?")) {
            sql.setInt(1, id);
            try (ResultSet result = sql.executeQuery()) {
                if (result.next()) {
                    product = new InvestmentProduct();
                    product.setId(result.getLong("id"));
                    product.setTypeInvestment(InvestmentType.valueOf(result.getString("type_investment")));
                    product.setReturnRate(result.getBigDecimal("return_rate"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public InvestmentProduct getByType(InvestmentType type) {
        JDBC conexao = new JDBC();
        InvestmentProduct produto = null;
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("SELECT * FROM investment_product WHERE type_investment = ?")) {
            sql.setString(1, type.name());
            try (ResultSet result = sql.executeQuery()) {
                if (result.next()) {
                    produto = new InvestmentProduct();
                    produto.setId(result.getLong("id"));
                    produto.setTypeInvestment(InvestmentType.valueOf(result.getString("type_investment")));
                    produto.setReturnRate(result.getBigDecimal("return_rate"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produto;
    }

    @Override
    public ArrayList<InvestmentProduct> getAll() {
        JDBC conexao = new JDBC();
        ArrayList<InvestmentProduct> products = new ArrayList<>();
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("SELECT * FROM investment_product");
             ResultSet result = sql.executeQuery()) {
            while (result.next()) {
                InvestmentProduct product = new InvestmentProduct();
                product.setId(result.getLong("id"));
                product.setTypeInvestment(InvestmentType.valueOf(result.getString("type_investment")));
                product.setReturnRate(result.getBigDecimal("return_rate"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public void insert(InvestmentProduct product) {
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("INSERT INTO investment_product (type_investment, return_rate) VALUES (?, ?)")) {
            sql.setString(1, product.getTypeInvestment().name());
            sql.setBigDecimal(2, product.getReturnRate());
            sql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(InvestmentProduct product) {
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("UPDATE investment_product SET type_investment = ?, return_rate = ? WHERE id = ?")) {
            sql.setString(1, product.getTypeInvestment().name());
            sql.setBigDecimal(2, product.getReturnRate());
            sql.setLong(3, product.getId());
            sql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        JDBC conexao = new JDBC();
        try (Connection conn = conexao.getConexao();
             PreparedStatement sql = conn.prepareStatement("DELETE FROM investment_product WHERE id = ?")) {
            sql.setInt(1, id);
            sql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}