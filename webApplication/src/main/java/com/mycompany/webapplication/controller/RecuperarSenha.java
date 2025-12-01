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

    private UserDAO userDAO;

    // Inicialização padrão para quando o servidor rodar a aplicação
    @Override
    public void init() throws ServletException {
        super.init();
        // Em produção, ele cria a conexão real
        this.userDAO = new UserDAO(new JDBC());
    }

    // Setter protegido/público para permitir que o TESTE injete um MOCK
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

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

        // Garantia de segurança caso o init não tenha rodado (ex: teste unitário mal configurado)
        if (this.userDAO == null) {
            this.init();
        }

        processarAlteracaoSenha(request, email, novaSenha, confirmSenha);

        request.getRequestDispatcher("/views/recuperarSenha.jsp").forward(request, response);
    }

    // Método extraído para facilitar testes unitários isolados se necessário
    private void processarAlteracaoSenha(HttpServletRequest request, String email, String novaSenha, String confirmSenha) {

        Users usuario = userDAO.getByEmail(email);

        if (usuario == null) {
            request.setAttribute("msgError", "E-mail não encontrado em nosso sistema.");
            return; // Encerra o fluxo aqui
        }

        if (!novaSenha.equals(confirmSenha)) {
            request.setAttribute("msgError", "As senhas digitadas não coincidem.");
            return; // Encerra o fluxo aqui
        }

        // A chamada estática ainda é um ponto de dificuldade para testes,
        // mas assumindo que validarSenha já popula o request com erros se falhar:
        boolean senhaValida = RecuperarSenhaUC.validarSenha(novaSenha, request);

        if (!senhaValida) {
            // Se a validação falhar, não fazemos nada e deixamos o request com os erros do UC
            // O return garante que não caia no updatePassword
            return;
        }

        // Se chegou aqui, é sucesso
        userDAO.updatePasswordByEmail(email, novaSenha);
        request.setAttribute("msgSuccess", "Senha atualizada com sucesso! Faça login com a nova senha.");
    }
}
