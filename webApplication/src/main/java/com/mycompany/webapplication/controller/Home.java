package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.AccountTransactional;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.AccountTransactionalDAO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "Home", urlPatterns = { "/Home" })
public class Home extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar se é uma ação de logout
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession();
            session.invalidate(); // Invalidar sessão
            response.sendRedirect("views/login.jsp"); // Redirecionar para login
            return;
        }

        HttpSession session = request.getSession();
        Users usuario = (Users) session.getAttribute("usuario");

        if (usuario == null) {
            // Se não estiver logado, redireciona para login
            response.sendRedirect("Login");
            return;
        }

        // Obtém conta bancária do usuário
        AccountDAO contaDAO = new AccountDAO();
        Account conta = contaDAO.getByUserId(usuario.getId());

        if (conta == null) {
            // Conta não encontrada — exibe mensagem na mesma tela
            request.setAttribute("erro", "Conta bancária não encontrada para este usuário.");
            RequestDispatcher rd = request.getRequestDispatcher("/views/home.jsp");
            rd.forward(request, response);
            return;
        }

        // Extrato (transações da conta)
        AccountTransactionalDAO transacaoDAO = new AccountTransactionalDAO();
        ArrayList<AccountTransactional> extrato = transacaoDAO.getAllByAccountId(conta.getId());
        if (extrato == null) {

        }

        // Envia dados para o JSP
        request.setAttribute("usuario", usuario);
        request.setAttribute("conta", conta);
        request.setAttribute("extrato", extrato);

        // Exibe tela
        RequestDispatcher rd = request.getRequestDispatcher("/views/home.jsp");
        rd.forward(request, response);
    }
}
