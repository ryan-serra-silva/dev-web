package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.Investment;
import com.mycompany.webapplication.entity.InvestmentTransactional;
import com.mycompany.webapplication.entity.InvestmentTransactionalType;

import java.sql.*;
import java.util.*;

public class InvestmentTransactionalDAO {

    private final JDBC conexao;

    public InvestmentTransactionalDAO(JDBC conexao) {
        this.conexao = conexao;
    }

    public void insert(InvestmentTransactional it) throws SQLException {
        String sql = "INSERT INTO investment_transaction (type, amount, timestamp, description, investment_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, it.getType().name());
            stmt.setBigDecimal(2, it.getAmount());
            stmt.setTimestamp(3, Timestamp.valueOf(it.getTimestamp()));
            stmt.setString(4, it.getDescription());
            stmt.setLong(5, it.getInvestment().getId());

            stmt.executeUpdate();
        }
    }

    public InvestmentTransactional findById(Long id) throws SQLException {
        String sql = "SELECT * FROM investment_transaction WHERE id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<InvestmentTransactional> findAll() throws SQLException {
        String sql = "SELECT * FROM investment_transaction";
        List<InvestmentTransactional> list = new ArrayList<>();

        try (Connection conn = conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(parseResultSet(rs));
            }
        }
        return list;
    }

    private InvestmentTransactional parseResultSet(ResultSet rs) throws SQLException {
        InvestmentTransactional trans = new InvestmentTransactional();
        trans.setId(rs.getLong("id"));
        trans.setType(InvestmentTransactionalType.valueOf(rs.getString("type")));
        trans.setAmount(rs.getBigDecimal("amount"));
        trans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        trans.setDescription(rs.getString("description"));

        Investment investment = new Investment();
        investment.setId(rs.getLong("investment_id"));
        trans.setInvestment(investment);

        return trans;
    }
}
