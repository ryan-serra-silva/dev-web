package com.mycompany.webapplication.usecases;

import java.io.IOException;
import java.math.BigDecimal;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.JDBC;
import com.mycompany.webapplication.model.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CadastrarUsuarioUC {
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
            }
            if (senha == null || senha.isEmpty()) {
                return "Senha não pode estar vazia.";
            }
            if (senha.length() < 6) {
                return "Senha deve ter pelo menos 6 caracteres.";
            }
            if (!senha.matches(".*\\d.*") || !senha.matches(".*[!@#$%^&()].*")) {
                return "Senha deve conter pelo menos um número ou caractere especial.";
            }
            if (!senha.matches(".*[A-Z].*") || !senha.matches(".*[a-z].*")) {
                return "Senha deve conter letras maiúsculas e minúsculas.";
            }
            if (senha.toLowerCase().contains(nome.toLowerCase()) || senha.equalsIgnoreCase(email)) {
                return "Senha não pode conter o nome ou o e-mail.";
            }
            if (senha.matches("^(123456|abcdef|senha|password)$")) {
                return "Senha muito fraca. Escolha outra.";
            }
            if (senha.matches("^[!@#$%^&*()].*") || senha.matches(".*[!@#$%^&*()]$")) {
                return "Senha não pode começar ou terminar com caractere especial.";
            }

            if (senha.contains(" ")) {
                return "Senha não pode conter espaços.";
            }
            return null;
        }
    }
}    