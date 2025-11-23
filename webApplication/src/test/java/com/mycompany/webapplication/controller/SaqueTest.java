package com.mycompany.webapplication.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.AccountTransactional;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.AccountTransactionalDAO;
import com.mycompany.webapplication.usecases.SaqueUC;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class SaqueTest {

    private Saque servlet;
    private AccountDAO mockAccountDAO;
    private AccountTransactionalDAO mockTransDAO;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    private Users usuario;
    private Account conta;

    @BeforeEach
    void setup() {

        servlet = new Saque();

        // DAOs mockados
        mockAccountDAO = mock(AccountDAO.class);
        mockTransDAO = mock(AccountTransactionalDAO.class);

        servlet.setAccountDAO(mockAccountDAO);
        servlet.setTransactionalDAO(mockTransDAO);

        // Objetos do Servlet mockados
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher("/views/saque.jsp")).thenReturn(dispatcher);

        // Usuário
        usuario = new Users();
        usuario.setId(1L);

        conta = new Account();
        conta.setBalance(new BigDecimal("500"));
    }

    // ===============================================================
    // TESTE 1 – Saque com sucesso
    // ===============================================================
    @Test
    void testDoPost_SaqueComSucesso() throws Exception {

        when(request.getParameter("valor")).thenReturn("100");
        when(session.getAttribute("usuario")).thenReturn(usuario);
        when(mockAccountDAO.getByUserId(1L)).thenReturn(conta);

        try (MockedStatic<SaqueUC> mocked = mockStatic(SaqueUC.class)) {

            mocked.when(() -> SaqueUC.validarSaque(eq(1L), any(), any(), eq(mockAccountDAO)))
                    .thenReturn(null);

            servlet.doPost(request, response);

            // Verifica persistência
            verify(mockAccountDAO, times(1)).update(any(Account.class));
            verify(mockTransDAO, times(1)).insert(any(AccountTransactional.class));

            verify(request).setAttribute("mensagem", "Saque realizado com sucesso!");

            verify(dispatcher).forward(request, response);
        }
    }

    // ===============================================================
    // TESTE 2 – Saque com saldo crítico (< 100)
    // ===============================================================
    @Test
    void testDoPost_SaldoCritico() throws Exception {

        when(request.getParameter("valor")).thenReturn("450");
        when(session.getAttribute("usuario")).thenReturn(usuario);
        when(mockAccountDAO.getByUserId(1L))
                .thenReturn(conta);

        try (MockedStatic<SaqueUC> mocked = mockStatic(SaqueUC.class)) {

            mocked.when(() -> SaqueUC.validarSaque(eq(1L), any(), any(), eq(mockAccountDAO)))
                    .thenReturn(null);

            servlet.doPost(request, response);

            verify(request).setAttribute("alerta", "Atenção: saldo baixo!");
        }
    }

    // ===============================================================
    // TESTE 3 – Erro de validação (SaqueUC retorna mensagem)
    // ===============================================================
    @Test
    void testDoPost_ErroValidacao() throws Exception {

        when(request.getParameter("valor")).thenReturn("100");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        try (MockedStatic<SaqueUC> mocked = mockStatic(SaqueUC.class)) {

            mocked.when(() -> SaqueUC.validarSaque(eq(1L), any(), any(), eq(mockAccountDAO)))
                    .thenReturn("Valor inválido!");

            servlet.doPost(request, response);

            verify(request).setAttribute("mensagem", "Valor inválido!");
            verify(mockAccountDAO, never()).update(any());
            verify(mockTransDAO, never()).insert(any());
        }
    }

    // ===============================================================
    // TESTE 4 – Exceção inesperada → mensagem de erro genérica
    // ===============================================================
    @Test
    void testDoPost_ExcecaoInterna() throws Exception {

        when(request.getParameter("valor")).thenReturn("100");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        when(mockAccountDAO.getByUserId(1L))
                .thenThrow(new RuntimeException("DB ERROR"));

        servlet.doPost(request, response);

        verify(request).setAttribute("mensagem", "Erro no processamento do saque.");
    }

    // ===============================================================
    // TESTE 5 – GET sem usuário → redirect para login
    // ===============================================================
    @Test
    void testDoGet_UsuarioNaoLogado() throws Exception {

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    // ===============================================================
    // TESTE 6 – GET com usuário → carrega conta
    // ===============================================================
    @Test
    void testDoGet_UsuarioLogado() throws Exception {

        when(session.getAttribute("usuario")).thenReturn(usuario);
        when(mockAccountDAO.getByUserId(1L)).thenReturn(conta);

        servlet.doGet(request, response);

        verify(request).setAttribute("usuario", usuario);
        verify(request).setAttribute("conta", conta);

        verify(dispatcher).forward(request, response);
    }
}
