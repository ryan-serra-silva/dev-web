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

@WebServlet(name = "Investir", urlPatterns = { "/Investir" })
public class Investimento extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Users usuario = (Users) session.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect("Login");
            return;
        }

        String mensagem = null;

        try {
            String tipo = request.getParameter("tipo");
            BigDecimal valor = new BigDecimal(request.getParameter("valor"));
            int tempoMeses = Integer.parseInt(request.getParameter("tempo"));

            AccountDAO accountDAO = new AccountDAO();
            Account conta = accountDAO.getByUserId(usuario.getId());

            if (conta == null) {
                mensagem = "Erro: Conta não encontrada.";
            } else if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                mensagem = "Erro: Valor deve ser maior que zero.";
            } else if (tipo == null || tipo.isEmpty()) {
                mensagem = "Erro: Tipo de investimento deve ser selecionado.";
            } else if (conta.getBalance().compareTo(valor) < 0) {
                mensagem = "Erro: Saldo insuficiente para realizar o investimento. Saldo disponível: R$ "
                        + conta.getBalance();
            } else {
                // Processo de investimento
                JDBC jdbc = new JDBC();
                Connection conn = jdbc.getConexao();

                try {
                    conn.setAutoCommit(false);

                    // Atualiza saldo
                    conta.setBalance(conta.getBalance().subtract(valor));
                    accountDAO.update(conta, conn);

                    // Busca produto
                    InvestmentProductDAO productDAO = new InvestmentProductDAO();
                    InvestmentType tipoEnum = InvestmentType.valueOf(tipo.toUpperCase());
                    InvestmentProduct produto = productDAO.getByType(tipoEnum);

                    if (produto == null) {
                        throw new Exception("Produto de investimento '" + tipo + "' não encontrado.");
                    }

                    // Cria investimento
                    Investment investimento = new Investment();
                    investimento.setAmount(valor);
                    investimento.setStartDate(LocalDate.now());
                    investimento.setEndDate(LocalDate.now().plusMonths(tempoMeses));
                    investimento.setAccount(conta);
                    investimento.setInvestmentProduct(produto);

                    InvestmentDAO investmentDAO = new InvestmentDAO();
                    investmentDAO.insert(investimento, conn);

                    conn.commit();

                    // Sucesso - redireciona para a página de investimentos atualizada
                    response.sendRedirect("Investir");
                    return;

                } catch (Exception e) {
                    if (conn != null) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    e.printStackTrace();
                    mensagem = "Erro interno ao processar o investimento. Tente novamente.";
                } finally {
                    if (conn != null) {
                        try {
                            conn.setAutoCommit(true);
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mensagem = "Erro ao processar dados do investimento.";
        }

        // Carrega dados para exibir na página (caso de erro)
        try {
            AccountDAO accountDAO = new AccountDAO();
            Account conta = accountDAO.getByUserId(usuario.getId());

            InvestmentDAO investmentDAO = new InvestmentDAO();
            List<Investment> listaInvestimentos = investmentDAO.getAllByAccountId(conta.getId());

            request.setAttribute("usuario", usuario);
            request.setAttribute("conta", conta);
            request.setAttribute("listaInvestimentos", listaInvestimentos);
            request.setAttribute("mensagem", mensagem);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensagem", "Erro ao carregar dados da conta.");
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
            response.sendRedirect(request.getContextPath() + "/Login");
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