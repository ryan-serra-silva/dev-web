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
        // 1. Comprimento mínimo
        if (senha.length() < 8) {
            request.setAttribute("msgError", "Senha deve ter pelo menos 8 caracteres.");
            return false;
        }

        // 2. Comprimento máximo (evitar senhas excessivamente longas)
        if (senha.length() > 30) {
            request.setAttribute("msgError", "Senha não deve ultrapassar 30 caracteres.");
            return false;
        }

        // 3. Deve conter pelo menos um número
        if (!senha.matches(".*\\d.*")) {
            request.setAttribute("msgError", "Senha deve conter pelo menos um número.");
            return false;
        }

        // 4. Deve conter pelo menos uma letra maiúscula
        if (!senha.matches(".*[A-Z].*")) {
            request.setAttribute("msgError", "Senha deve conter pelo menos uma letra maiúscula.");
            return false;
        }

        // 5. Deve conter pelo menos uma letra minúscula
        if (!senha.matches(".*[a-z].*")) {
            request.setAttribute("msgError", "Senha deve conter pelo menos uma letra minúscula.");
            return false;
        }

        // 6. Deve conter pelo menos um caractere especial
        if (!senha.matches(".*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/].*")) {
            request.setAttribute("msgError", "Senha deve conter pelo menos um caractere especial.");
            return false;
        }

        // 7. Não pode conter espaços
        if (senha.contains(" ")) {
            request.setAttribute("msgError", "Senha não deve conter espaços.");
            return false;
        }

        // 8. Não pode ter o mesmo caractere repetido 3 vezes seguidas
        if (senha.matches(".*(.)\\1\\1.*")) {
            request.setAttribute("msgError", "Senha não deve conter três caracteres idênticos seguidos.");
            return false;
        }

        // 9. Não pode conter o nome 'senha' ou padrões previsíveis
        if (senha.toLowerCase().contains("senha") || senha.toLowerCase().contains("password")) {
            request.setAttribute("msgError", "Senha não deve conter palavras óbvias como 'senha' ou 'password'.");
            return false;
        }

        // 10. Deve conter pelo menos 4 tipos diferentes de caracteres (número, maiúscula, minúscula, especial)
        int tipos = 0;
        if (senha.matches(".*\\d.*")) tipos++;
        if (senha.matches(".*[A-Z].*")) tipos++;
        if (senha.matches(".*[a-z].*")) tipos++;
        if (senha.matches(".*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/].*")) tipos++;

        if (tipos < 3) {
            request.setAttribute("msgError", "Senha deve conter pelo menos três tipos diferentes de caracteres (número, maiúscula, minúscula, especial).");
            return false;
        }

        return true; // passou em todas as validações
    }
}