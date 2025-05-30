package com.mycompany.webapplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/db")
public class JDBC extends HttpServlet {
    String url = "jdbc:postgresql://localhost:5432/postgres";
    String user = "postgres";
    String password = "123";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Servlet JDBC</title></head>");
            out.println("<body>");
            out.println("<h1>Conectando ao PostgreSQL...</h1>");

            try {
                // Força o carregamento do driver
                Class.forName("org.postgresql.Driver");

                // Conectar ao banco
                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT version()");

                    if (rs.next()) {
                        out.println("<p>Versão do PostgreSQL: " + rs.getString(1) + "</p>");
                    }
                }

            } catch (Exception e) {
                out.println("<p style='color:red;'>Erro ao conectar: " + e.getMessage() + "</p>");
                e.printStackTrace(out);
            }

            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para testar conexão com PostgreSQL";
    }
}
