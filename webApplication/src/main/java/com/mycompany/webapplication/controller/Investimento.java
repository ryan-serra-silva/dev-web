package com.mycompany.webapplication.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Investment;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.InvestmentDAO;
import com.mycompany.webapplication.model.InvestmentTransactionalDAO;
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
            String valorStr = request.getParameter("valor");
            String tempoStr = request.getParameter("tempo");

            if (valorStr == null || valorStr.isBlank() ||
                    tempoStr == null || tempoStr.isBlank() ||
                    tipo == null || tipo.isBlank()) {

                mensagem = "Dados inválidos. Preencha todos os campos.";
                carregarDadosTela(request, usuario, mensagem);
                request.getRequestDispatcher("/views/investir.jsp").forward(request, response);
                return;
            }

            BigDecimal valor = new BigDecimal(valorStr);
            int tempoMeses = Integer.parseInt(tempoStr);

            AccountDAO accountDAO = new AccountDAO();
            Account conta = accountDAO.getByUserId(usuario.getId());

            // Validar antes de executar
            String erroValidacao = InvestimentoUC.validar(conta, tipo, valor, tempoMeses);

            if (erroValidacao != null) {
                mensagem = erroValidacao;
            } else {
                JDBC jdbc = new JDBC();

                InvestmentTransactionalDAO productDAO = new InvestmentTransactionalDAO(jdbc);
                InvestmentDAO investmentDAO = new InvestmentDAO(jdbc,accountDAO, productDAO);
                // Agora o UC usa o construtor padrão com dependências internas
                InvestimentoUC uc = new InvestimentoUC(accountDAO, investmentDAO, productDAO, jdbc);
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

        carregarDadosTela(request, usuario, mensagem);
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
        JDBC jdbc = new JDBC();
        InvestmentTransactionalDAO investmentTransactionalDAO = new InvestmentTransactionalDAO(jdbc);
        InvestmentDAO investmentDAO = new InvestmentDAO(jdbc,accountDAO, investmentTransactionalDAO);
        List<Investment> listaInvestimentos = investmentDAO.getAllByAccountId(conta.getId());

        request.setAttribute("usuario", usuario);
        request.setAttribute("conta", conta);
        // Envia a lista para o JSP
        request.setAttribute("listaInvestimentos", listaInvestimentos);

        request.getRequestDispatcher("/views/investir.jsp").forward(request, response);
    }

    private void carregarDadosTela(HttpServletRequest request, Users usuario, String mensagem) {
        try {
            AccountDAO accountDAO = new AccountDAO();
            Account conta = accountDAO.getByUserId(usuario.getId());

            JDBC jdbc = new JDBC();
            InvestmentTransactionalDAO investmentTransactionalDAO = new InvestmentTransactionalDAO(jdbc);
            InvestmentDAO investmentDAO = new InvestmentDAO(jdbc,accountDAO, investmentTransactionalDAO);
            List<Investment> listaInvest = investmentDAO.getAllByAccountId(conta.getId());

            request.setAttribute("usuario", usuario);
            request.setAttribute("conta", conta);
            request.setAttribute("listaInvestimentos", listaInvest);
            request.setAttribute("mensagem", mensagem);

        } catch (Exception e) {
            request.setAttribute("mensagem", "Erro ao carregar dados da conta.");
        }
    }
}