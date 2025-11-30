package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.JDBC;
import com.mycompany.webapplication.model.UserDAO;
import com.mycompany.webapplication.usecases.CadastrarUsuarioUC;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarTest {

    @InjectMocks
    private CadastrarUsuario cadastrarUsuarioServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Test
    void doGet_encaminhaParaJspCadastro() throws IOException, jakarta.servlet.ServletException {
        when(request.getRequestDispatcher("/views/cadastro.jsp")).thenReturn(dispatcher);

        cadastrarUsuarioServlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    void doPost_erroValidacao_exibeMensagemErro() throws IOException, jakarta.servlet.ServletException {
        // Arrange
        when(request.getParameter("nome")).thenReturn("João");
        when(request.getParameter("email")).thenReturn("joao@teste.com");
        when(request.getParameter("senha")).thenReturn("123");
        when(request.getRequestDispatcher("/views/cadastro.jsp")).thenReturn(dispatcher);

        try (MockedConstruction<JDBC> mockedJdbc = mockConstruction(JDBC.class);
             MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class);
             MockedConstruction<AccountDAO> mockedAccountDAO = mockConstruction(AccountDAO.class);
             MockedStatic<CadastrarUsuarioUC> mockedStaticUC = mockStatic(CadastrarUsuarioUC.class)) {

            mockedStaticUC.when(() -> 
                CadastrarUsuarioUC.validarUsuario(anyString(), anyString(), anyString(), any(UserDAO.class))
            ).thenReturn("Erro de validação simulado");

            cadastrarUsuarioServlet.doPost(request, response);
        }

        verify(request).setAttribute("msgError", "Erro de validação simulado");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doPost_cadastroSucesso_criaUsuarioEConta() throws IOException, jakarta.servlet.ServletException {
        String emailTeste = "sucesso@teste.com";
        when(request.getParameter("nome")).thenReturn("Maria");
        when(request.getParameter("email")).thenReturn(emailTeste);
        when(request.getParameter("senha")).thenReturn("senhaForte");
        when(request.getRequestDispatcher("/views/cadastro.jsp")).thenReturn(dispatcher);

        Users usuarioSimulado = new Users("Maria", emailTeste, "senhaForte");
        usuarioSimulado.setId(10L);

        try (MockedConstruction<JDBC> mockedJdbc = mockConstruction(JDBC.class);
             MockedConstruction<AccountDAO> mockedAccountDAO = mockConstruction(AccountDAO.class);
             MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class,
                     (mock, context) -> {
                         when(mock.getByEmail(emailTeste)).thenReturn(usuarioSimulado);
                     });
             MockedStatic<CadastrarUsuarioUC> mockedStaticUC = mockStatic(CadastrarUsuarioUC.class)) {

            mockedStaticUC.when(() -> 
                CadastrarUsuarioUC.validarUsuario(anyString(), anyString(), anyString(), any(UserDAO.class))
            ).thenReturn(null);

            cadastrarUsuarioServlet.doPost(request, response);

            UserDAO userDAO = mockedUserDAO.constructed().get(0);
            verify(userDAO).insert(any(Users.class));

            AccountDAO accountDAO = mockedAccountDAO.constructed().get(0);
            verify(accountDAO).insert(any(Account.class));
        }

        verify(request).setAttribute(eq("msgSuccess"), contains("Cadastro realizado com sucesso"));
        verify(request).setAttribute("redirecionarLogin", true);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doPost_erroAoBuscarUsuarioAposInserir_exibeMensagemErro() throws IOException, jakarta.servlet.ServletException {
        String emailTeste = "falha@teste.com";
        when(request.getParameter("nome")).thenReturn("Teste");
        when(request.getParameter("email")).thenReturn(emailTeste);
        when(request.getParameter("senha")).thenReturn("123");
        when(request.getRequestDispatcher("/views/cadastro.jsp")).thenReturn(dispatcher);

        try (MockedConstruction<JDBC> mockedJdbc = mockConstruction(JDBC.class);
             MockedConstruction<AccountDAO> mockedAccountDAO = mockConstruction(AccountDAO.class);
             MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class,
                     (mock, context) -> {
                         when(mock.getByEmail(emailTeste)).thenReturn(null);
                     });
             MockedStatic<CadastrarUsuarioUC> mockedStaticUC = mockStatic(CadastrarUsuarioUC.class)) {

            mockedStaticUC.when(() -> 
                CadastrarUsuarioUC.validarUsuario(anyString(), anyString(), anyString(), any(UserDAO.class))
            ).thenReturn(null);

            cadastrarUsuarioServlet.doPost(request, response);
            
            AccountDAO accountDAO = mockedAccountDAO.constructed().get(0);
            verify(accountDAO, never()).insert(any(Account.class));
        }

        verify(request).setAttribute("msgError", "Erro ao buscar usuário após cadastro.");
        verify(dispatcher).forward(request, response);
    }
}