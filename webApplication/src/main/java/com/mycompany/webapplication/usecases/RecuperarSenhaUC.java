package com.mycompany.webapplication.usecases;

import jakarta.servlet.http.HttpServletRequest;

public class RecuperarSenhaUC {


    private static final String MSG_ERROR = "msgError";


    private RecuperarSenhaUC() {
    }

    public static boolean validarSenha(String senha, HttpServletRequest request) {
        // 1. Comprimento mínimo
        if (senha.length() < 8) {
            request.setAttribute(MSG_ERROR, "Senha deve ter pelo menos 8 caracteres.");
            return false;
        }

        // 2. Comprimento máximo (evitar senhas excessivamente longas)
        if (senha.length() > 30) {
            request.setAttribute(MSG_ERROR, "Senha não deve ultrapassar 30 caracteres.");
            return false;
        }

        // 3. Deve conter pelo menos um número
        if (!senha.matches(".*\\d.*")) {
            request.setAttribute(MSG_ERROR, "Senha deve conter pelo menos um número.");
            return false;
        }

        // 4. Deve conter pelo menos uma letra maiúscula
        if (!senha.matches(".*[A-Z].*")) {
            request.setAttribute(MSG_ERROR, "Senha deve conter pelo menos uma letra maiúscula.");
            return false;
        }

        // 5. Deve conter pelo menos uma letra minúscula
        if (!senha.matches(".*[a-z].*")) {
            request.setAttribute(MSG_ERROR, "Senha deve conter pelo menos uma letra minúscula.");
            return false;
        }

        // 6. Deve conter pelo menos um caractere especial
        if (!senha.matches(".*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/].*")) {
            request.setAttribute(MSG_ERROR, "Senha deve conter pelo menos um caractere especial.");
            return false;
        }

        // 7. Não pode conter espaços
        if (senha.contains(" ")) {
            request.setAttribute(MSG_ERROR, "Senha não deve conter espaços.");
            return false;
        }

        // 8. Não pode ter o mesmo caractere repetido 3 vezes seguidas
        if (senha.matches(".*(.)\\1\\1.*")) {
            request.setAttribute(MSG_ERROR, "Senha não deve conter três caracteres idênticos seguidos.");
            return false;
        }

        // 9. Não pode conter o nome 'senha' ou padrões previsíveis
        if (senha.toLowerCase().contains("senha") || senha.toLowerCase().contains("password")) {
            request.setAttribute(MSG_ERROR, "Senha não deve conter palavras óbvias como 'senha' ou 'password'.");
            return false;
        }

        // 10. Deve conter pelo menos 4 tipos diferentes de caracteres (número, maiúscula, minúscula, especial)
        int tipos = 0;
        if (senha.matches(".*\\d.*")) tipos++;
        if (senha.matches(".*[A-Z].*")) tipos++;
        if (senha.matches(".*[a-z].*")) tipos++;
        if (senha.matches(".*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/].*")) tipos++;

        if (tipos < 3) {
            request.setAttribute(MSG_ERROR, "Senha deve conter pelo menos três tipos diferentes de caracteres (número, maiúscula, minúscula, especial).");
            return false;
        }

        return true; 
    }
}
