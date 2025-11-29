package com.mycompany.webapplication.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {

    private final String jdbcUrl;
    private final String dbUser;
    private final String dbPassword;

    public JDBC() {
        try {
            Class.forName("org.postgresql.Driver");

            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String port = System.getenv().getOrDefault("DB_PORT", "5433");
            String dbName = System.getenv().getOrDefault("DB_NAME", "postgres");
            this.dbUser = System.getenv().getOrDefault("DB_USER", "postgres");
            this.dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "123");

            this.jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver do PostgreSQL não encontrado!", e);
        }
    }

    public Connection getConexao() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }
}
