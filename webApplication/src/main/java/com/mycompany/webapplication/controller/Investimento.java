package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.*;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.InvestmentDAO;
import com.mycompany.webapplication.model.InvestmentProductDAO;
import com.mycompany.webapplication.model.JDBC;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List; // Importar List

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "Investir", urlPatterns = {"/Investir"})
public class Investimento extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        JDBC jdbc = new JDBC();
        Connection conn = jdbc.getConexao();

        try {
            conn.setAutoCommit(false);

            String tipo = request.getParameter("tipo");
            BigDecimal valor = new BigDecimal(request.getParameter("valor"));
            int tempoMeses = Integer.parseInt(request.getParameter("tempo"));

            HttpSession session = request.getSession();
            Users usuario = (Users) session.getAttribute("usuario");
            if (usuario == null) {
                response.sendRedirect("Login");
                return;
            }
            Long userId = usuario.getId();

            AccountDAO accountDAO = new AccountDAO();
            InvestmentProductDAO productDAO = new InvestmentProductDAO();
            InvestmentDAO investmentDAO = new InvestmentDAO();

            Account conta = accountDAO.getByUserId(userId, conn); 

            if (conta != null && valor.compareTo(BigDecimal.ZERO) > 0 && tipo != null && !tipo.isEmpty()) {
                if (conta.getBalance().compareTo(valor) >= 0) {

                    conta.setBalance(conta.getBalance().subtract(valor));
                    accountDAO.update(conta, conn);

                    InvestmentType tipoEnum = InvestmentType.valueOf(tipo.toUpperCase());
                    InvestmentProduct produto = productDAO.getByType(tipoEnum);

                    if (produto == null) {
                        throw new Exception("Produto de investimento '" + tipo + "' não encontrado.");
                    }

                    Investment investimento = new Investment();
                    investimento.setAmount(valor);
                    investimento.setStartDate(LocalDate.now());
                    investimento.setEndDate(LocalDate.now().plusMonths(tempoMeses));
                    investimento.setAccount(conta);
                    investimento.setInvestmentProduct(produto);
                    investmentDAO.insert(investimento, conn);

                    conn.commit();
                    
                    response.sendRedirect("Home");
                    return;

                } else {
                    request.setAttribute("mensagem", "Erro: saldo insuficiente.");
                }
            } else {
                request.setAttribute("mensagem", "Erro: dados inválidos ou conta inexistente.");
            }

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace(); 
            request.setAttribute("mensagem", "Erro interno ao processar o investimento. Consulte o log do servidor.");

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("/views/investir.jsp").forward(request, response);
    }

    /**
     * MÉTODO ATUALIZADO
     * Agora busca a lista de investimentos do usuário e a envia para a página.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Users usuario = (Users) session.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        AccountDAO accountDAO = new AccountDAO();
        Account conta = accountDAO.getByUserId(usuario.getId());

        // Busca a lista de investimentos da conta
        InvestmentDAO investmentDAO = new InvestmentDAO();
        List<Investment> listaInvestimentos = investmentDAO.getAllByAccountId(conta.getId());

        request.setAttribute("usuario", usuario);
        request.setAttribute("conta", conta);
        // Envia a lista para o JSP
        request.setAttribute("listaInvestimentos", listaInvestimentos);

        request.getRequestDispatcher("/views/investir.jsp").forward(request, response);
    }
}