package com.mycompany.webapplication.controller;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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


}
