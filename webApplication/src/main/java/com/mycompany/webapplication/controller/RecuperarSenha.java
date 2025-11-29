package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.JDBC;
import com.mycompany.webapplication.model.UserDAO;
import com.mycompany.webapplication.usecases.RecuperarSenhaUC;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "RecuperarSenha", urlPatterns = {"/RecuperarSenha"})
public class RecuperarSenha extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/recuperarSenha.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String novaSenha = request.getParameter("novaSenha");
        String confirmSenha = request.getParameter("confirmSenha");

        JDBC jdbc = new JDBC();
        UserDAO userDAO = new UserDAO(jdbc);

        Users usuario = userDAO.getByEmail(email);

        if (usuario == null) {
            request.setAttribute("msgError", "E-mail não encontrado em nosso sistema.");

        } else if (!novaSenha.equals(confirmSenha)) {
            request.setAttribute("msgError", "As senhas digitadas não coincidem.");


        } else if (!RecuperarSenhaUC.validarSenha(novaSenha, request)) {


        } else {

            userDAO.updatePasswordByEmail(email, novaSenha);
            request.setAttribute("msgSuccess", "Senha atualizada com sucesso! Faça login com a nova senha.");
        }

        request.getRequestDispatcher("/views/recuperarSenha.jsp").forward(request, response);
    }
}