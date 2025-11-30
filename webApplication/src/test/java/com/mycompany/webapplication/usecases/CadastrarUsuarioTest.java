package com.mycompany.webapplication.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.mycompany.webapplication.MockGenerator;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.UserDAO;
import com.mycompany.webapplication.usecases.CadastrarUsuarioUC;

public class CadastrarUsuarioTest {

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = Mockito.mock(UserDAO.class);
    }

    @Test
    public void testNomeVazio() {
        String msg = CadastrarUsuarioUC.validarUsuario("", "teste@email.com", "Senha123", userDAO);
        assertEquals("Nome não pode estar vazio.", msg);
    }

    @Test
    public void testEmailVazio() {
        String msg = CadastrarUsuarioUC.validarUsuario("Gabriel", "", "Senha123", userDAO);
        assertEquals("E-mail não pode estar vazio.", msg);
    }

    @Test
    public void testEmailInvalidoSemArroba() {
        String msg = CadastrarUsuarioUC.validarUsuario("Gabriel", "testeemail.com", "Senha123", userDAO);
        assertEquals("E-mail inválido. Deve conter '@' e '.'", msg);
    }

    @Test
    public void testEmailJaExistente() {
        Users u = MockGenerator.createUser();
        Mockito.when(userDAO.getByEmail("existente@email.com")).thenReturn(u);
        String msg = CadastrarUsuarioUC.validarUsuario("João", "existente@email.com", "Senha123", userDAO);
        assertEquals("E-mail já está em uso. Tente outro.", msg);
    }

    @Test
    public void testSenhaVazia() {
        String msg = CadastrarUsuarioUC.validarUsuario("Maria", "maria@email.com", "", userDAO);
        assertEquals("Senha não pode estar vazia.", msg);
    }

    @Test
    public void testSenhaCurta() {
        String msg = CadastrarUsuarioUC.validarUsuario("Maria", "maria@email.com", "Ab1", userDAO);
        assertEquals("Senha deve ter pelo menos 6 caracteres.", msg);
    }

    @Test
    public void testSenhaSemNumeroNemEspecial() {
        String msg = CadastrarUsuarioUC.validarUsuario("Carlos", "carlos@email.com", "Senhaa", userDAO);
        assertEquals("Senha deve conter pelo menos um número ou caractere especial.", msg);
    }

    @Test
    public void testSenhaSemMaiusculaEMinuscula() {
        String msg = CadastrarUsuarioUC.validarUsuario("Lucas", "lucas@email.com", "1234567", userDAO);
        assertEquals("Senha deve conter pelo menos um número ou caractere especial.", msg);
    }

    @Test
    public void testSenhaContemNome() {
        String msg = CadastrarUsuarioUC.validarUsuario("Lucas", "lucas@email.com", "Lucas12#3", userDAO);
        assertEquals("Senha não pode conter o nome ou o e-mail.", msg);
    }

    @Test
    public void testSenhaComEspaco() {
        String msg = CadastrarUsuarioUC.validarUsuario("Pedro", "pedro@email.com", "Senha 12#3", userDAO);
        assertEquals("Senha não pode conter espaços.", msg);
    }

    @Test
    public void testSenhaValida() {
        String msg = CadastrarUsuarioUC.validarUsuario("Pedro", "pedro@email.com", "Senha123!", userDAO);
        assertEquals("Senha não pode começar ou terminar com caractere especial.",msg);
    }
}
