package com.mycompany.webapplication.usecases;

import com.mycompany.webapplication.MockGenerator; 
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

        Users users = MockGenerator.createUser();
    }

 
    @Test
    public void senhacorreta(){
    
        boolean result = RecuperarSenhaUC.validarSenha("Chaveaberta123!", request);
        assertTrue(result);
        verifyNoInteractions(request);
    }

    @Test
    public void senhaExatamente8Caracteres(){

        boolean result = RecuperarSenhaUC.validarSenha("Aa1@bcde", request);
        assertTrue(result, "Deve aceitar senha com exatamente 8 caracteres");
    }

    @Test
    public void senhaExatamente30Caracteres(){
  
        String senha30 = "Aa1@bcdeAa1@bcdeAa1@bcdeAa1@bc"; 
        boolean result = RecuperarSenhaUC.validarSenha(senha30, request);
        assertTrue(result, "Deve aceitar senha com exatamente 30 caracteres");
    }

    @Test
    public void senhacurta(){
      
        boolean result = RecuperarSenhaUC.validarSenha("Chave1!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha deve ter pelo menos 8 caracteres."));
    }

    @Test
    public void senhalonga(){
      
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
        
        boolean result = RecuperarSenhaUC.validarSenha("Chaveabertaaaaa123!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha não deve conter três caracteres idênticos seguidos."));
    }

    @Test
    public void senhafragil_senha(){
       
        boolean result = RecuperarSenhaUC.validarSenha("Chavesenha123!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha não deve conter palavras óbvias como 'senha' ou 'password'."));
    }

    @Test
    public void senhafragil_password(){
        boolean result = RecuperarSenhaUC.validarSenha("Mypassword123!", request);
        assertFalse(result);
        verify(request).setAttribute(eq(MSG_ERROR), eq("Senha não deve conter palavras óbvias como 'senha' ou 'password'."));
    }
}
