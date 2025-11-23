package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.AccountTransactional;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.AccountTransactionalDAO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class HomeTest {

    private Home servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private AccountDAO accountDAO;
    private AccountTransactionalDAO transactionDAO;

    @BeforeEach
    void setup() {
        servlet = new Home();

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        accountDAO = mock(AccountDAO.class);
        transactionDAO = mock(AccountTransactionalDAO.class);

        servlet.setAccountDAO(accountDAO);
        servlet.setTransactionDAO(transactionDAO);

        when(request.getSession()).thenReturn(session);
    }

    // ----------------------------------------------------------------------------------------

    @Test
    void testLogout() throws Exception {
        when(request.getParameter("action")).thenReturn("logout");

        servlet.doGet(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("views/login.jsp");
    }

    // ----------------------------------------------------------------------------------------

    @Test
    void testUsuarioNaoLogado() throws Exception {
        when(request.getParameter("action")).thenReturn(null);
        when(session.getAttribute("usuario")).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/Login");
    }

    // ----------------------------------------------------------------------------------------

    @Test
    void testContaNaoEncontrada() throws Exception {
        Users user = new Users();
        user.setId(1L);

        when(session.getAttribute("usuario")).thenReturn(user);
        when(accountDAO.getByUserId(1L)).thenReturn(null);
        when(request.getRequestDispatcher("/views/home.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("erro", "Conta bancária não encontrada para este usuário.");
        verify(dispatcher).forward(request, response);
    }

    // ----------------------------------------------------------------------------------------

    @Test
    void testExtratoNull() throws Exception {
        Users user = new Users();
        user.setId(1L);

        Account conta = new Account();
        conta.setId(10L);

        when(session.getAttribute("usuario")).thenReturn(user);
        when(accountDAO.getByUserId(1L)).thenReturn(conta);
        when(transactionDAO.getAllByAccountId(10L)).thenReturn(null);
        when(request.getRequestDispatcher("/views/home.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("extrato"), any(ArrayList.class));
        verify(dispatcher).forward(request, response);
    }

    // ----------------------------------------------------------------------------------------

    @Test
    void testFluxoCompleto() throws Exception {
        Users user = new Users();
        user.setId(1L);

        Account conta = new Account();
        conta.setId(10L);

        ArrayList<AccountTransactional> extrato = new ArrayList<>();
        extrato.add(new AccountTransactional());

        when(session.getAttribute("usuario")).thenReturn(user);
        when(accountDAO.getByUserId(1L)).thenReturn(conta);
        when(transactionDAO.getAllByAccountId(10L)).thenReturn(extrato);
        when(request.getRequestDispatcher("/views/home.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("usuario", user);
        verify(request).setAttribute("conta", conta);
        verify(request).setAttribute("extrato", extrato);
        verify(dispatcher).forward(request, response);
    }

}
