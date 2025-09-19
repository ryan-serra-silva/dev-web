package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.UserDAO;
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

        UserDAO userDAO = new UserDAO();
        Users usuario = userDAO.getByEmail(email);

        if (usuario == null) {
            request.setAttribute("msgError", "E-mail não encontrado em nosso sistema.");
        } else if (!novaSenha.equals(confirmSenha)) {
            request.setAttribute("msgError", "As senhas digitadas não coincidem.");
        } else if (!validarSenha(novaSenha, request)) {
            // A validação detalhada já define a mensagem de erro
        } else {
            // Atualiza a senha no banco
            userDAO.updatePasswordByEmail(email, novaSenha);
            request.setAttribute("msgSuccess", "Senha atualizada com sucesso! Faça login com a nova senha.");
        }

        request.getRequestDispatcher("/views/recuperarSenha.jsp").forward(request, response);
    }

    /**
     * Valida a senha segundo os critérios:
     * - pelo menos 6 caracteres
     * - pelo menos um número
     * - pelo menos uma letra maiúscula
     */
    private boolean validarSenha(String senha, HttpServletRequest request) {
        if (senha.length() < 6) {
            request.setAttribute("msgError", "Senha deve ter pelo menos 6 caracteres.");
            return false;
        }
        if (!senha.matches(".*\\d.*")) {
            request.setAttribute("msgError", "Senha deve conter pelo menos um número.");
            return false;
        }
        if (!senha.matches(".*[A-Z].*")) {
            request.setAttribute("msgError", "Senha deve conter pelo menos uma letra maiúscula.");
            return false;
        }
        if (!senha.matches(".*[!@#$%^&*()].*")) {
            request.setAttribute("msgError", "Senha deve conter um caractere especial.");
            return false;
    }

        return true;
    }
}
