package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Servlet responsável pelo cadastro de novos usuários
 * 
 * @author ryan
 */
@WebServlet(name = "CadastroUsuario", urlPatterns = { "/CadastroUsuario" })
public class CadastroUsuario extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/cadastro.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        UserDAO userDAO = new UserDAO();
        AccountDAO accountDAO = new AccountDAO();

        String msgErro = validarUsuario(nome, email, senha, userDAO);

        if (msgErro != null) {
            request.setAttribute("msgError", msgErro);
        } else {
            Users novo = new Users(nome, email, senha);
            userDAO.insert(novo);

            Users usuarioComId = userDAO.getByEmail(email);

            if (usuarioComId != null) {
                String agencia = "0001";
                String numeroConta = gerarNumeroContaAleatorio();
                BigDecimal saldoInicial = new BigDecimal("0.00");

                Account novaConta = new Account(numeroConta, agencia, saldoInicial, usuarioComId.getId());
                accountDAO.insert(novaConta);

                request.setAttribute("msgSuccess",
                        "Cadastro realizado com sucesso! Conta criada. Redirecionando para login...");
                request.setAttribute("redirecionarLogin", true);
            } else {
                request.setAttribute("msgError", "Erro ao buscar usuário após cadastro.");
            }
        }

        request.getRequestDispatcher("/views/cadastro.jsp")
                .forward(request, response);
    }

    private String validarUsuario(String nome, String email, String senha, UserDAO userDAO) {
        if (nome == null || nome.trim().isEmpty()) {
            return "Nome não pode estar vazio.";
        } else if (email == null || email.trim().isEmpty()) {
            return "E-mail não pode estar vazio.";
        } else if (!email.contains("@") || !email.contains(".")) {
            return "E-mail inválido. Deve conter '@' e '.'";
        } else {
            Users existente = userDAO.getByEmail(email);
            if (existente != null) {
                return "E-mail já está em uso. Tente outro.";
            } else if (senha == null || senha.isEmpty()) {
                return "Senha não pode estar vazia.";
            } else if (senha.length() < 6) {
                return "Senha deve ter pelo menos 6 caracteres.";
            } else if (!senha.matches(".*\\d.*") && !senha.matches(".*[!@#$%^&*()].*")) {
                return "Senha deve conter pelo menos um número ou caractere especial.";
            } else if (!senha.matches(".*[A-Z].*") && !senha.matches(".*[a-z].*")) {
                return "Senha deve conter letras maiúsculas e minúsculas.";
            } else if (senha.toLowerCase().contains(nome.toLowerCase()) || senha.equalsIgnoreCase(email)) {
                return "Senha não pode conter o nome ou o e-mail.";
            } else if (senha.matches("^(123456|abcdef|senha|password)$")) {
                return "Senha muito fraca. Escolha outra.";
            } else if (senha.startsWith("!@#") || senha.endsWith("!@#")) {
                return "Senha não pode começar ou terminar com '!@#'.";
            } else if (senha.contains(" ")) {
                return "Senha não pode conter espaços.";
            } else {
                return null; 
            }
        }
    }

    private String gerarNumeroContaAleatorio() {
        int numero = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(numero);
    }
}