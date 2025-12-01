package com.mycompany.webapplication.usecases;

import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.UserDAO;

public class CadastrarUsuarioUC {


    private CadastrarUsuarioUC() {
        throw new UnsupportedOperationException("Classe utilitária. Não pode ser instanciada.");
    }

    public static String validarUsuario(String nome, String email, String senha, UserDAO userDAO) {

        String erro;

        erro = validarNome(nome);
        if (erro != null) return erro;

        erro = validarEmail(email, userDAO);
        if (erro != null) return erro;

        erro = validarSenha(senha, nome, email);
        if (erro != null) return erro;

        return null;
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return "Nome não pode estar vazio.";
        }
        return null;
    }

    private static String validarEmail(String email, UserDAO userDAO) {
        if (email == null || email.trim().isEmpty()) {
            return "E-mail não pode estar vazio.";
        }
        if (!email.contains("@") || !email.contains(".")) {
            return "E-mail inválido. Deve conter '@' e '.'";
        }
        Users existente = userDAO.getByEmail(email);
        if (existente != null) {
            return "E-mail já está em uso. Tente outro.";
        }
        return null;
    }

    private static String validarSenha(String senha, String nome, String email) {
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

        if (senha.toLowerCase().contains(nome.toLowerCase()) ||
            senha.equalsIgnoreCase(email)) {
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
