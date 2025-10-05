package com.mycompany.webapplication.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecuperarSenhaTest {

    private RecuperarSenha servlet;
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        servlet = new RecuperarSenha();
        request = mock(HttpServletRequest.class);
    }

    private boolean validarSenha(String senha) {
        try {
            var method = RecuperarSenha.class.getDeclaredMethod("validarSenha", String.class, HttpServletRequest.class);
            method.setAccessible(true);
            return (boolean) method.invoke(servlet, senha, request);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Test
    public void testSenhaMinima() {
        String senha = "Ab1!";
        boolean resultado = validarSenha(senha);
        assertFalse(resultado);
        verify(request).setAttribute(eq("msgError"), contains("8 caracteres"));
    }

    @Test
    public void testSenhaMaxima() {
        String senha = "A1!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        boolean resultado = validarSenha(senha);
        assertFalse(resultado);
        verify(request).setAttribute(eq("msgError"), contains("não deve ultrapassar 30"));
    }

    @Test
    public void testSenhaSemNumero() {
        String senha = "Abcdefgh!";
        boolean resultado = validarSenha(senha);
        assertFalse(resultado);
        verify(request).setAttribute(eq("msgError"), contains("um número"));
    }

    @Test
    public void testSenhaSemMaiuscula() {
        String senha = "abcdef1!";
        boolean resultado = validarSenha(senha);
        assertFalse(resultado);
        verify(request).setAttribute(eq("msgError"), contains("uma letra maiúscula"));
    }

    @Test
    public void testSenhaSemMinuscula() {
        String senha = "ABCDEF1!";
        boolean resultado = validarSenha(senha);
        assertFalse(resultado);
        verify(request).setAttribute(eq("msgError"), contains("uma letra minúscula"));
    }

    @Test
    public void testSenhaSemEspecial() {
        String senha = "Abcdef12";
        boolean resultado = validarSenha(senha);
        assertFalse(resultado);
        verify(request).setAttribute(eq("msgError"), contains("um caractere especial"));
    }

    @Test
    public void testSenhaComEspaco() {
        String senha = "Abc123! ";
        boolean resultado = validarSenha(senha);
        assertFalse(resultado);
        verify(request).setAttribute(eq("msgError"), contains("não deve conter espaços"));
    }

    @Test
    public void testSenhaTresCaracteresRepetidos() {
        String senha = "Abc111!d";
        boolean resultado = validarSenha(senha);
        assertFalse(resultado);
        verify(request).setAttribute(eq("msgError"), contains("três caracteres idênticos"));
    }

    @Test
    public void testSenhaContendoSenhaOuPassword() {
        String senha = "MinhaSenha1!";
        boolean resultado = validarSenha(senha);
        assertFalse(resultado);
        verify(request).setAttribute(eq("msgError"), contains("óbvias como 'senha' ou 'password'"));
    }

    @Test
    public void testSenhaValida() {
        String senha = "Abc1!def";
        boolean resultado = validarSenha(senha);
        assertTrue(resultado);
        verify(request, never()).setAttribute(eq("msgError"), any());
    }
}
