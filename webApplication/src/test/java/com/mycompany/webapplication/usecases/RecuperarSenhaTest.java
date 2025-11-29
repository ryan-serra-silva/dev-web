package com.mycompany.webapplication.usecases;

import com.mycompany.webapplication.MockGenerator;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.usecases.RecuperarSenhaUC;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class RecuperarSenhaTest {
    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        Users users = MockGenerator.createUser();
    }

    @Test
    public void senhacurta(){
        boolean result = RecuperarSenhaUC.validarSenha("Chave1!",request);
        assertFalse(result);
    }
    @Test
    public void senhalonga(){
        boolean result = RecuperarSenhaUC.validarSenha("Senhasenhasenhasenhasenha1!",request);
        assertFalse(result);
    }
    @Test
    public void semnumero(){
        boolean result = RecuperarSenhaUC.validarSenha("Chaveaberta!",request);
        assertFalse(result);
    }
    @Test
    public void semmaiuscula(){
        boolean result = RecuperarSenhaUC.validarSenha("chaveaberta123!",request);
        assertFalse(result);
    }
    @Test
    public void semminuscula(){
        boolean result = RecuperarSenhaUC.validarSenha("CHAVEABERTA123!",request);
        assertFalse(result);
    }
    @Test
    public void semespecial(){
        boolean result = RecuperarSenhaUC.validarSenha("Chaveaberta123",request);
        assertFalse(result);
    }
    @Test
    public void comespaco(){
        boolean result = RecuperarSenhaUC.validarSenha("Chaveaberta 123!",request);
        assertFalse(result);
    }
    @Test
    public void caracterrepetido(){
        boolean result = RecuperarSenhaUC.validarSenha("Chaveabertaaaaa123!",request);
        assertFalse(result);
    }
    @Test
    public void senhafragil(){
        boolean result = RecuperarSenhaUC.validarSenha("Chavesenha123!",request);
        assertFalse(result);
    }
    @Test
    public void senhacorreta(){
        boolean result = RecuperarSenhaUC.validarSenha("Chaveaberta123!",request);
        assertTrue(result);
    }

}
