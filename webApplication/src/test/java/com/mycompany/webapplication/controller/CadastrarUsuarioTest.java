package com.mycompany.webapplication.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CadastrarUsuarioTest {

    private CadastrarUsuario cadastrarUsuario;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        cadastrarUsuario = new CadastrarUsuario();
        userDAO = Mockito.mock(UserDAO.class); // Mock do DAO
    }

    @Test
    public void testNomeVazio() {
        String msg = chamarValidacao("", "teste@email.com", "Senha123");
        assertEquals("Nome não pode estar vazio.", msg);
    }

    @Test
    public void testEmailVazio() {
        String msg = chamarValidacao("Gabriel", "", "Senha123");
        assertEquals("E-mail não pode estar vazio.", msg);
    }

    @Test
    public void testEmailInvalidoSemArroba() {
        String msg = chamarValidacao("Gabriel", "testeemail.com", "Senha123");
        assertEquals("E-mail inválido. Deve conter '@' e '.'", msg);
    }

    @Test
    public void testEmailJaExistente() {
        Users u = MockGenerator.createUser();
        Mockito.when(userDAO.getByEmail("existente@email.com")).thenReturn(u);
        String msg = chamarValidacao("João", "existente@email.com", "Senha123");
        assertEquals("E-mail já está em uso. Tente outro.", msg);
    }

    @Test
    public void testSenhaVazia() {
        String msg = chamarValidacao("Maria", "maria@email.com", "");
        assertEquals("Senha não pode estar vazia.", msg);
    }

    @Test
    public void testSenhaCurta() {
        String msg = chamarValidacao("Maria", "maria@email.com", "Ab1");
        assertEquals("Senha deve ter pelo menos 6 caracteres.", msg);
    }

    @Test
    public void testSenhaSemNumeroNemEspecial() {
        String msg = chamarValidacao("Carlos", "carlos@email.com", "Senhaa");
        assertEquals("Senha deve conter pelo menos um número ou caractere especial.", msg);
    }

    @Test
    public void testSenhaSemMaiusculaEMinuscula() {
        String msg = chamarValidacao("Lucas", "lucas@email.com", "1234567");
        assertEquals("Senha deve conter letras maiúsculas e minúsculas.", msg);
    }

    @Test
    public void testSenhaContemNome() {
        String msg = chamarValidacao("Lucas", "lucas@email.com", "Lucas123");
        assertEquals("Senha não pode conter o nome ou o e-mail.", msg);
    }

    @Test
    public void testSenhaComEspaco() {
        String msg = chamarValidacao("Pedro", "pedro@email.com", "Senha 123");
        assertEquals("Senha não pode conter espaços.", msg);
    }

    @Test
    public void testSenhaValida() {
        String msg = chamarValidacao("Pedro", "pedro@email.com", "Senha123!");
        assertNull(msg, "Esperava que a senha válida não retornasse erro");
    }

    // Método auxiliar para chamar a validação por reflexão
    private String chamarValidacao(String nome, String email, String senha) {
        try {
            var metodo = CadastrarUsuario.class.getDeclaredMethod("validarUsuario", String.class, String.class, String.class, UserDAO.class);
            metodo.setAccessible(true);
            return (String) metodo.invoke(cadastrarUsuario, nome, email, senha, userDAO);
        } catch (Exception e) {
            fail("Erro ao invocar método validarUsuario: " + e.getMessage());
            return null;
        }
    }
}