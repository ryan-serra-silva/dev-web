package com.mycompany.webapplication.controller;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.MockedConstruction;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Investment;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.InvestmentDAO;
import com.mycompany.webapplication.model.InvestmentTransactionalDAO;
import com.mycompany.webapplication.model.JDBC;
import com.mycompany.webapplication.usecases.InvestimentoUC;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class InvestimentoTest {

    private final Investimento servlet = new Investimento();

    @Test
    void doGet_usuarioNaoLogado_redirecionaParaLogin() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        servlet.doGet(req, resp);

        verify(resp).sendRedirect(anyString());
    }

    @Test
    void doGet_usuarioLogado_renderizaTelaComLista() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);

        Users usuario = new Users();
        usuario.setId(1L);

        when(session.getAttribute("usuario")).thenReturn(usuario);

        when(req.getRequestDispatcher("/views/investir.jsp"))
                .thenReturn(dispatcher);

        Account contaMock = new Account();
        contaMock.setId(10L);

        List<Investment> investimentosMock = List.of(
                new Investment(), new Investment()
        );

        try (MockedConstruction<AccountDAO> mockAcc =
                     mockConstruction(AccountDAO.class,
                             (mock, ctx) -> when(mock.getByUserId(1L)).thenReturn(contaMock));
             MockedConstruction<JDBC> mockJdbc =
                     mockConstruction(JDBC.class);
             MockedConstruction<InvestmentTransactionalDAO> mockTrans =
                     mockConstruction(InvestmentTransactionalDAO.class);
             MockedConstruction<InvestmentDAO> mockInvDao =
                     mockConstruction(InvestmentDAO.class,
                             (mock, ctx) -> when(mock.getAllByAccountId(10L))
                                     .thenReturn(investimentosMock))
        ) {

            servlet.doGet(req, resp);
            verify(req).setAttribute("usuario", usuario);
            verify(req).setAttribute("conta", contaMock);
            verify(req).setAttribute("listaInvestimentos", investimentosMock);
            verify(dispatcher).forward(req, resp);
        }
    }

    @Test
    void doPost_dadosInvalidos_retornaErro() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);

        Users usuario = new Users();
        usuario.setId(1L);

        when(session.getAttribute("usuario")).thenReturn(usuario);

        when(req.getParameter("valor")).thenReturn("");
        when(req.getParameter("tipo")).thenReturn("CDB");
        when(req.getParameter("tempo")).thenReturn("");

        when(req.getRequestDispatcher("/views/investir.jsp"))
                .thenReturn(dispatcher);

        Account contaMock = new Account();
        contaMock.setId(1L);
        contaMock.setBalance(BigDecimal.valueOf(1000));

        try (MockedConstruction<AccountDAO> accMock =
                     mockConstruction(AccountDAO.class,
                             (mock, ctx) -> when(mock.getByUserId(1L))
                                     .thenReturn(contaMock));
             MockedConstruction<JDBC> jdbcMock = mockConstruction(JDBC.class);
             MockedConstruction<InvestmentTransactionalDAO> transMock =
                     mockConstruction(InvestmentTransactionalDAO.class);
             MockedConstruction<InvestmentDAO> invMock =
                     mockConstruction(InvestmentDAO.class)
        ) {

            servlet.doPost(req, resp);

            verify(req).setAttribute(eq("mensagem"), contains("Dados inválidos"));
            verify(dispatcher).forward(req, resp);
        }
    }


    @Test
    void doPost_investimentoValido_redirecionaParaInvestir() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getSession()).thenReturn(session);

        Users usuario = new Users();
        usuario.setId(1L);

        when(session.getAttribute("usuario")).thenReturn(usuario);

        when(req.getParameter("tipo")).thenReturn("CDB");
        when(req.getParameter("valor")).thenReturn("500");
        when(req.getParameter("tempo")).thenReturn("12");

        Account contaMock = new Account();
        contaMock.setId(20L);
        contaMock.setBalance(new BigDecimal("2000"));

        try (MockedConstruction<AccountDAO> accMock =
                     mockConstruction(AccountDAO.class,
                             (mock, ctx) -> when(mock.getByUserId(1L))
                                     .thenReturn(contaMock));
             MockedConstruction<JDBC> jdbcMock =
                     mockConstruction(JDBC.class);
             MockedConstruction<InvestmentTransactionalDAO> transMock =
                     mockConstruction(InvestmentTransactionalDAO.class);
             MockedConstruction<InvestmentDAO> invMock =
                     mockConstruction(InvestmentDAO.class);
             MockedConstruction<InvestimentoUC> ucMock =
                     mockConstruction(InvestimentoUC.class,
                             (mock, ctx) -> when(
                                     mock.executar(
                                             eq(usuario),
                                             eq("CDB"),
                                             eq(new BigDecimal("500")),
                                             eq(12)
                                     )).thenReturn(null))
        ) {

            servlet.doPost(req, resp);

            // Verifica fluxo de sucesso
            verify(resp).sendRedirect("Investir");
        }
    }

    @Test
    void doPost_ucRetornaErro_renderizaTelaComMensagemDeErro() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);

        Users usuario = new Users();
        usuario.setId(1L);
        when(session.getAttribute("usuario")).thenReturn(usuario);

        when(req.getParameter("tipo")).thenReturn("CDB");
        when(req.getParameter("valor")).thenReturn("500");
        when(req.getParameter("tempo")).thenReturn("12");

        when(req.getRequestDispatcher("/views/investir.jsp"))
                .thenReturn(dispatcher);

        Account contaMock = new Account();
        contaMock.setId(20L);
        contaMock.setBalance(new BigDecimal("2000"));

        String erroExecucaoMock = "Saldo insuficiente para investir.";

        try (MockedConstruction<AccountDAO> accMock =
                     mockConstruction(AccountDAO.class,
                             (mock, ctx) -> when(mock.getByUserId(1L))
                                     .thenReturn(contaMock));
             MockedConstruction<JDBC> jdbcMock = mockConstruction(JDBC.class);
             MockedConstruction<InvestmentTransactionalDAO> transMock =
                     mockConstruction(InvestmentTransactionalDAO.class);
             MockedConstruction<InvestmentDAO> invMock =
                     mockConstruction(InvestmentDAO.class,
                             (mock, ctx) -> when(mock.getAllByAccountId(20L))
                                     .thenReturn(List.of()));
             MockedConstruction<InvestimentoUC> ucMock =
                     mockConstruction(InvestimentoUC.class,
                             (mock, ctx) -> when(
                                     mock.executar(
                                             eq(usuario),
                                             eq("CDB"),
                                             eq(new BigDecimal("500")),
                                             eq(12)
                                     )).thenReturn(erroExecucaoMock))
        ) {

            servlet.doPost(req, resp);

            verify(req).setAttribute(eq("mensagem"), eq(erroExecucaoMock));
            verify(dispatcher).forward(req, resp);
        }
    }

        @Test
        void doPost_carregarDadosTelaException_defineMensagemErro() throws Exception {

                HttpServletRequest req = mock(HttpServletRequest.class);
                HttpServletResponse resp = mock(HttpServletResponse.class);
                HttpSession session = mock(HttpSession.class);
                RequestDispatcher dispatcher = mock(RequestDispatcher.class);

                when(req.getSession()).thenReturn(session);

                Users usuario = new Users();
                usuario.setId(1L);

                when(session.getAttribute("usuario")).thenReturn(usuario);

                when(req.getParameter("valor")).thenReturn("");
                when(req.getParameter("tipo")).thenReturn("CDB");
                when(req.getParameter("tempo")).thenReturn("");

                when(req.getRequestDispatcher("/views/investir.jsp"))
                                .thenReturn(dispatcher);

                try (MockedConstruction<AccountDAO> accMock =
                                         mockConstruction(AccountDAO.class,
                                                         (mock, ctx) -> when(mock.getByUserId(1L)).thenThrow(new RuntimeException("DB fail")));
                         MockedConstruction<JDBC> jdbcMock = mockConstruction(JDBC.class);
                         MockedConstruction<InvestmentTransactionalDAO> transMock =
                                         mockConstruction(InvestmentTransactionalDAO.class);
                         MockedConstruction<InvestmentDAO> invMock =
                                         mockConstruction(InvestmentDAO.class)
                ) {

                    // Reference the MockedConstruction objects to avoid "variable not used" warnings
                    accMock.constructed();
                    jdbcMock.constructed();
                    transMock.constructed();
                    invMock.constructed();

                    servlet.doPost(req, resp);

                        verify(req).setAttribute(eq("mensagem"), eq("Erro ao carregar dados da conta."));
                        verify(dispatcher).forward(req, resp);
                }
        }


}
