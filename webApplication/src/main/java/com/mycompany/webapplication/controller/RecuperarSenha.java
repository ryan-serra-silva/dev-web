package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.SecureRandom;

@WebServlet(name = "RecuperarSenha", urlPatterns = {"/RecuperarSenha"})
public class RecuperarSenha extends HttpServlet {

    // Apenas exibe a página JSP
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/recuperarSenha.jsp").forward(request, response);
    }

    // Processa a solicitação de nova senha
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        UserDAO userDAO = new UserDAO();
        Users usuario = userDAO.getByEmail(email);

        if (usuario == null) {
            // Se o email não existe no banco
            request.setAttribute("msgError", "E-mail não encontrado em nosso sistema.");
        } else {
            // Se o e-mail for encontrado, gera e atualiza a senha
            String novaSenha = gerarNovaSenhaAleatoria();
            userDAO.updatePasswordByEmail(email, novaSenha);

            // IMPORTANTE: Em um sistema real, a nova senha NUNCA deve ser exibida na tela.
            // Ela deveria ser enviada por e-mail.
            // Para este projeto, vamos exibi-la como mensagem de sucesso.
            request.setAttribute("msgSuccess", "Sua nova senha é: " + novaSenha + ". Anote-a e use-a para fazer o login.");
        }

        request.getRequestDispatcher("/views/recuperarSenha.jsp").forward(request, response);
    }

    // Método para gerar uma senha aleatória simples (ex: 6 dígitos)
    private String gerarNovaSenhaAleatoria() {
        SecureRandom random = new SecureRandom();
        int numero = 100000 + random.nextInt(900000); // Gera um número entre 100000 e 999999
        return String.valueOf(numero);
    }
}