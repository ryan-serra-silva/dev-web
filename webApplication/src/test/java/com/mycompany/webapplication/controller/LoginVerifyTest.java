package com.mycompany.webapplication.controller;


import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.mockito.Mockito.*;

public class LoginVerifyTest {

    private LoginVerify servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;
    private UserDAO userDAO;
    private HttpSession session;

    @BeforeEach
    void setup() {
        servlet = new LoginVerify();

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        userDAO = mock(UserDAO.class);
        session = mock(HttpSession.class);

        servlet.setUserDAO(userDAO);
    }

    // --------------------------------------------------------
    // GET
    // --------------------------------------------------------

    @Test
    void testDoGetForwardLoginPage() throws Exception {
        when(request.getRequestDispatcher("/views/login.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    // --------------------------------------------------------
    // POST — CAMPOS VAZIOS
    // --------------------------------------------------------

    @Test
    void testDoPostCamposVaziosEmailNull() throws Exception {
        when(request.getParameter("email")).thenReturn(null);
        when(request.getParameter("senha")).thenReturn("");
        when(request.getRequestDispatcher("/views/login.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("msgError", "Preencha todos os campos");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPostCamposVaziosEmailBlank() throws Exception {
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("senha")).thenReturn("");
        when(request.getRequestDispatcher("/views/login.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("msgError", "Preencha todos os campos");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPostCamposVaziosSenhaNull() throws Exception {
        when(request.getParameter("email")).thenReturn("teste11@teste.com");
        when(request.getParameter("senha")).thenReturn(null);
        when(request.getRequestDispatcher("/views/login.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("msgError", "Preencha todos os campos");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPostCamposVaziosSenhaBlank() throws Exception {
        when(request.getParameter("email")).thenReturn("teste11@teste.com");
        when(request.getParameter("senha")).thenReturn("");
        when(request.getRequestDispatcher("/views/login.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("msgError", "Preencha todos os campos");
        verify(dispatcher).forward(request, response);
    }

    // --------------------------------------------------------
    // POST — USUÁRIO NÃO ENCONTRADO
    // --------------------------------------------------------

    @Test
    void testDoPostLoginInvalido() throws Exception {
        when(request.getParameter("email")).thenReturn("a@a.com");
        when(request.getParameter("senha")).thenReturn("123");
        when(userDAO.login("a@a.com", "123")).thenReturn(null);
        when(request.getRequestDispatcher("/views/login.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("msgError", "Credenciais inválidas");
        verify(dispatcher).forward(request, response);
    }

    // --------------------------------------------------------
    // POST — LOGIN VÁLIDO
    // --------------------------------------------------------

    @Test
    void testDoPostLoginValido() throws Exception {
        Users usuarioMock = new Users();
        usuarioMock.setId(1L);

        when(request.getParameter("email")).thenReturn("user@test.com");
        when(request.getParameter("senha")).thenReturn("123");
        when(userDAO.login("user@test.com", "123")).thenReturn(usuarioMock);

        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(session).setAttribute("usuario", usuarioMock);
        verify(response).sendRedirect("Home");
    }
}
