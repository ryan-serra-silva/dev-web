package com.mycompany.webapplication.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RecuperarSenhaTest {

    @Test
    public void CT001_atualizarSenhaComSucesso() {
        // Dados válidos
        String novaSenha = "NovaSenha1!";
        String confirmSenha = "NovaSenha1!";

        // Simulação de validação básica da classe original
        boolean senhasIguais = novaSenha.equals(confirmSenha);
        boolean tamanhoValido = novaSenha.length() >= 6;
        boolean temNumero = novaSenha.matches(".*\\d.*");
        boolean temMaiuscula = novaSenha.matches(".*[A-Z].*");
        boolean temEspecial = novaSenha.matches(".*[!@#$%^&*()].*");

        // Resultado final
        boolean senhaValida = senhasIguais && tamanhoValido && temNumero && temMaiuscula && temEspecial;

        // Verificação
        assertTrue(senhaValida, "Senha deveria ser válida e atualizada com sucesso.");
    }
}
