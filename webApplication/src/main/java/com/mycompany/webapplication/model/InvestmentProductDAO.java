/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.InvestmentProduct;
import com.mycompany.webapplication.entity.InvestmentType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author ryan
 */

public class InvestmentProductDAO implements Dao<InvestmentProduct> {

    @Override
    public InvestmentProduct get(int id) {
        JDBC conexao = new JDBC();
        InvestmentProduct product = null;
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement(
                "SELECT * FROM investment_product WHERE id = ?");
            sql.setInt(1, id);
            ResultSet result = sql.executeQuery();
            if (result.next()) {
                product = new InvestmentProduct();
                product.setId(result.getLong("id"));
                // Supondo que o tipo de investimento está salvo como string no banco, 
                // você precisará converter para o enum InvestmentType
                String typeStr = result.getString("type_investiment");
                product.setTypeInvestment(InvestmentType.valueOf(typeStr));
                product.setReturnRate(result.getBigDecimal("returnRate"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar InvestmentProduct: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
        return product;
    }
    
    public InvestmentProduct getByType(InvestmentType type) {
    JDBC conexao = new JDBC();
    InvestmentProduct produto = null;
    try (PreparedStatement sql = conexao.getConexao().prepareStatement(
        "SELECT * FROM investment_product WHERE type_investiment = ?")) {
        sql.setString(1, type.name());
        try (ResultSet result = sql.executeQuery()) {
            if (result.next()) {
                produto = new InvestmentProduct();
                produto.setId(result.getLong("id"));
                produto.setTypeInvestment(type);
                produto.setReturnRate(result.getBigDecimal("return_rate"));
            }
        }
    } catch (SQLException e) {
        System.err.println("Erro ao buscar produto de investimento por tipo.");
    } finally {
        conexao.closeConexao();
    }
    return produto;
}


    @Override
    public ArrayList<InvestmentProduct> getAll() {
        JDBC conexao = new JDBC();
        ArrayList<InvestmentProduct> products = new ArrayList<>();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM investment_product");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                InvestmentProduct product = new InvestmentProduct();
                product.setId(result.getLong("id"));
                String typeStr = result.getString("type_investiment");
                product.setTypeInvestment(InvestmentType.valueOf(typeStr));
                product.setReturnRate(result.getBigDecimal("returnRate"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos InvestmentProduct: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
        return products;
    }

    @Override
    public void insert(InvestmentProduct product) {
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement(
                "INSERT INTO investment_product (typeInvestment, returnRate) VALUES (?, ?)");
            sql.setString(1, product.getTypeInvestment().name());
            sql.setBigDecimal(2, product.getReturnRate());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir InvestmentProduct: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
    }

    @Override
    public void update(InvestmentProduct product) {
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement(
                "UPDATE investment_product SET type_investiment = ?, returnRate = ? WHERE id = ?");
            sql.setString(1, product.getTypeInvestment().name());
            sql.setBigDecimal(2, product.getReturnRate());
            sql.setLong(3, product.getId());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar InvestmentProduct: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
    }

    @Override
    public void delete(int id) {
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("DELETE FROM investment_product WHERE id = ?");
            sql.setInt(1, id);
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar InvestmentProduct: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
    }
}
