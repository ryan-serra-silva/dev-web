package com.mycompany.webapplication.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Investment;
import com.mycompany.webapplication.entity.InvestmentProduct;
import com.mycompany.webapplication.entity.InvestmentType;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.InvestmentDAO;
import com.mycompany.webapplication.model.InvestmentProductDAO;
import com.mycompany.webapplication.model.JDBC;

import com.mycompany.webapplication.usecases.InvestimentoUC;
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

        String mensagem;

        try {
            String tipo = request.getParameter("tipo");
            BigDecimal valor = new BigDecimal(request.getParameter("valor"));
            int tempoMeses = Integer.parseInt(request.getParameter("tempo"));

            AccountDAO accountDAO = new AccountDAO();
            Account conta = accountDAO.getByUserId(usuario.getId());

            String erroValidacao = InvestimentoUC.validar(conta, tipo, valor, tempoMeses);

            if (erroValidacao != null) {
                mensagem = erroValidacao;
            } else {
                InvestimentoUC uc = new InvestimentoUC();
                String erroExecucao = uc.executar(usuario, tipo, valor, tempoMeses);

                if (erroExecucao == null) {
                    response.sendRedirect("Investir");
                    return;
                } else {
                    mensagem = erroExecucao;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mensagem = "Erro ao processar dados do investimento.";
        }

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