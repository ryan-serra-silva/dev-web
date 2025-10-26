package com.mycompany.webapplication.controller;

import java.time.LocalTime;
import java.math.BigDecimal;

import com.mycompany.webapplication.entity.*;
import com.mycompany.webapplication.model.*;

import com.mycompany.webapplication.usecases.AccountService;
import com.mycompany.webapplication.usecases.DepositoUC;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "Depositar", urlPatterns = {"/Depositar"})
public class Depositar extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Users usuario = (Users) session.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        BigDecimal valor;
        try {
            valor = new BigDecimal(request.getParameter("valor"));
        } catch (NumberFormatException e) {
            request.setAttribute("mensagem", "Valor inválido!");
            request.getRequestDispatcher("/views/deposito.jsp").forward(request, response);
            return;
        }
        LocalTime agora = LocalTime.now();
        AccountDAO accountDAO = new AccountDAO();
        String resultado = DepositoUC.processarDeposito(usuario.getId(), valor,accountDAO ,request, agora);

        request.setAttribute("mensagem", resultado);
        request.setAttribute("usuario", usuario);

        request.setAttribute("conta", accountDAO.getByUserId(usuario.getId()));

        request.getRequestDispatcher("/views/deposito.jsp").forward(request, response);
    }

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

        request.setAttribute("usuario", usuario);
        request.setAttribute("conta", conta);

        request.getRequestDispatcher("/views/deposito.jsp").forward(request, response);
    }
}
