package com.mycompany.webapplication.usecases;

import com.mycompany.webapplication.MockGenerator; // Pode manter se usa em outros lugares, mas não é estritamente necessário aqui
import com.mycompany.webapplication.entity.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecuperarSenhaTest {

    @Mock
    HttpServletRequest request;

    private static final String MSG_ERROR = "msgError";

    @BeforeEach
    public void setUp() {
        // O MockGenerator não é essencial para o teste estático do UC,
        // mas mantive para não quebrar seu padrão de setup.
        Users users = MockGenerator.createUser();
    }

    // --- TESTES DE SUCESSO E FRONTEIRA (MATADORES DE MUTAÇÃO RELACIONAL) ---

    @Test
    public void senhacorreta(){
        // Senha forte válida padrão
        boolean result = RecuperarSenhaUC.validarSenha("Chaveaberta123!", request);
        assertTrue(result);
        verifyNoInteractions(request); // Garante que não setou erro
    }

    @Test
    public void senhaExatamente8Caracteres(){
        // Fronteira Inferior: Mata mutantes que trocam < 8 por <= 8
        boolean result = RecuperarSenhaUC.validarSenha("Aa1@bcde", request);
        assertTrue(result, "Deve aceitar senha com exatamente 8 caracteres");
    }

    @Test
    public void senhaExatamente30Caracteres(){
        // Fronteira Superior: Mata mutantes que trocam > 30 por >= 30
        String senha30 = "Aa1@bcdeAa1@bcdeAa1@bcdeAa1@bc";
        boolean result = RecuperarSenhaUC.validarSenha(senha30, request);
        assertTrue(result, "Deve aceitar senha com exatamente 30 caracteres");
    }

    // --- TESTES DE FALHA (MATADORES DE MENSAGEM E LÓGICA) ---

    @Test
    public void senhacurta(){
        // 7 caracteres
        boolean result = RecuperarSenhaUC.validarSenha("Chave1!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha deve ter pelo menos 8 caracteres."));
    }

    @Test
    public void senhalonga(){
        // 31 caracteres
        String senhaLonga = "Senhasenhasenhasenhasenha12345!";
        boolean result = RecuperarSenhaUC.validarSenha(senhaLonga, request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha não deve ultrapassar 30 caracteres."));
    }

    @Test
    public void semnumero(){
        boolean result = RecuperarSenhaUC.validarSenha("Chaveaberta!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha deve conter pelo menos um número."));
    }

    @Test
    public void semmaiuscula(){
        boolean result = RecuperarSenhaUC.validarSenha("chaveaberta123!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha deve conter pelo menos uma letra maiúscula."));
    }

    @Test
    public void semminuscula(){
        boolean result = RecuperarSenhaUC.validarSenha("CHAVEABERTA123!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha deve conter pelo menos uma letra minúscula."));
    }

    @Test
    public void semespecial(){
        boolean result = RecuperarSenhaUC.validarSenha("Chaveaberta123", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha deve conter pelo menos um caractere especial."));
    }

    @Test
    public void comespaco(){
        boolean result = RecuperarSenhaUC.validarSenha("Chaveaberta 123!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha não deve conter espaços."));
    }

    @Test
    public void caracterrepetido(){
        // Testa repetição (aaa)
        boolean result = RecuperarSenhaUC.validarSenha("Chaveabertaaaaa123!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha não deve conter três caracteres idênticos seguidos."));
    }

    @Test
    public void senhafragil_senha(){
        // Testa palavra "senha"
        boolean result = RecuperarSenhaUC.validarSenha("Chavesenha123!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha não deve conter palavras óbvias como 'senha' ou 'password'."));
    }

    @Test
    public void senhafragil_password(){
        // Testa palavra "password" (para cobrir o OR || na condição)
        boolean result = RecuperarSenhaUC.validarSenha("Mypassword123!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha não deve conter palavras óbvias como 'senha' ou 'password'."));
    }
}
