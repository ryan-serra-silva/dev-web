package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.usecases.DepositoUC;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositarTest {

    @InjectMocks
    private Depositar depositarServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private AccountDAO accountDAO;

    private Users usuario;

    @BeforeEach
    void setUp() {
        usuario = new Users();
        usuario.setId(1L);
    }


    @Test
    void doPost_redirecionaParaLogin_quandoUsuarioNulo() throws IOException, jakarta.servlet.ServletException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        depositarServlet.doPost(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void doPost_valorInvalido_exibeMensagemErro() throws IOException, jakarta.servlet.ServletException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);
        when(request.getParameter("valor")).thenReturn("abc");
        when(request.getRequestDispatcher("/views/deposito.jsp")).thenReturn(dispatcher);

        depositarServlet.doPost(request, response);

        verify(request).setAttribute("mensagem", "Valor inválido!");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doPost_depositoValido_exibeMensagemSucesso() throws Exception {

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);
        when(request.getParameter("valor")).thenReturn("100");
        when(request.getRequestDispatcher("/views/deposito.jsp")).thenReturn(dispatcher);

        Account contaMock = new Account();
        contaMock.setId(1L);

        try (MockedStatic<DepositoUC> mockedStatic = mockStatic(DepositoUC.class);
             MockedConstruction<AccountDAO> mockedConstruct = mockConstruction(AccountDAO.class,
                     (mock, context) -> when(mock.getByUserId(usuario.getId())).thenReturn(contaMock))) {

            mockedStatic.when(() ->
                    DepositoUC.processarDeposito(eq(1L), eq(new BigDecimal("100")), any(), eq(request), any())
            ).thenReturn("Depósito realizado com sucesso!");

            depositarServlet.doPost(request, response);
        }

        verify(request).setAttribute("mensagem", "Depósito realizado com sucesso!");
        verify(request).setAttribute("usuario", usuario);
        verify(request).setAttribute("conta", contaMock);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doGet_redirecionaParaLogin_quandoUsuarioNulo() throws IOException, jakarta.servlet.ServletException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        depositarServlet.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }
    @Test
    void doGet_usuarioValido_exibeDeposito() throws Exception {

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);
        when(request.getRequestDispatcher("/views/deposito.jsp")).thenReturn(dispatcher);

        Account contaMock = new Account();
        contaMock.setId(1L);

        try (MockedConstruction<AccountDAO> mocked = mockConstruction(AccountDAO.class,
                (mock, context) -> when(mock.getByUserId(usuario.getId())).thenReturn(contaMock))) {

            depositarServlet.doGet(request, response);
        }

        verify(request).setAttribute("usuario", usuario);
        verify(request).setAttribute("conta", contaMock);
        verify(dispatcher).forward(request, response);
    }
}
