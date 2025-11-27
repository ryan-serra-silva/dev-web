package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.AccountTransactional;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.AccountTransactionalDAO;

import com.mycompany.webapplication.model.JDBC;
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

    // DAOs mockáveis para testes
    private AccountDAO accountDAO;
    private AccountTransactionalDAO transactionDAO;

    @Override
    public void init() throws ServletException {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new AccountTransactionalDAO(new JDBC());
    }

    // setters para mock
    public void setAccountDAO(AccountDAO dao) {
        this.accountDAO = dao;
    }

    public void setTransactionDAO(AccountTransactionalDAO dao) {
        this.transactionDAO = dao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // -------- LOGOUT --------
        if ("logout".equals(action)) {
            HttpSession session = request.getSession();
            session.invalidate();
            response.sendRedirect("views/login.jsp");
            return;
        }

        HttpSession session = request.getSession();
        Users usuario = (Users) session.getAttribute("usuario");

        // -------- NÃO LOGADO --------
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        // -------- BUSCA CONTA --------
        Account conta = accountDAO.getByUserId(usuario.getId());

        if (conta == null) {
            request.setAttribute("erro", "Conta bancária não encontrada para este usuário.");
            RequestDispatcher rd = request.getRequestDispatcher("/views/home.jsp");
            rd.forward(request, response);
            return;
        }

        // -------- BUSCA EXTRATO --------
        ArrayList<AccountTransactional> extrato =
                transactionDAO.getAllByAccountId(conta.getId());

        // Extrato pode ser null? (mantido só porque está no seu código)
        if (extrato == null) {
            extrato = new ArrayList<>();
        }

        // -------- ENVIA DADOS --------
        request.setAttribute("usuario", usuario);
        request.setAttribute("conta", conta);
        request.setAttribute("extrato", extrato);

        RequestDispatcher rd = request.getRequestDispatcher("/views/home.jsp");
        rd.forward(request, response);
    }
}
