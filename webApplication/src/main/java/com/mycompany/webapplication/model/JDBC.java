package com.mycompany.webapplication.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {
    private String url = "jdbc:postgresql://localhost:5432/postgres";
    private String user = "postgres";
    private String password = "123";

    private Connection conexao;

    public JDBC() {
        try {
            Class.forName("org.postgresql.Driver");
            conexao = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Não foi possível efetuar uma conexão com o BD!", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Não foi possível encontrar o driver do banco! Verifique se o driver está no classpath.", e);
        }
    }

    public Connection getConexao() {
        return this.conexao;
    }

    public void closeConexao() {
        try {
            if (this.conexao != null && !this.conexao.isClosed()) {
                this.conexao.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
