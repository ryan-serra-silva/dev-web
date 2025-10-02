package com.mycompany.webapplication.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {
    private Connection conexao;

    public JDBC() {
        try {
            Class.forName("org.postgresql.Driver");
            // Construir configuração a partir de variáveis de ambiente com valores padrão
            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String port = System.getenv().getOrDefault("DB_PORT", "5433");
            String dbName = System.getenv().getOrDefault("DB_NAME", "postgres");
            String dbUser = System.getenv().getOrDefault("DB_USER", "postgres");
            String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "123");

            String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
            conexao = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
            System.out.println("Conectado ao banco: " + conexao.getMetaData().getURL());
        } catch (SQLException e) {
            throw new RuntimeException("Não foi possível efetuar uma conexão com o BD!", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver do PostgreSQL não encontrado. Adicione ao classpath!", e);
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
