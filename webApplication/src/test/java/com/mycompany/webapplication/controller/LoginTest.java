package com.mycompany.webapplication.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class LoginTest {

    private Login servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setup() {
        servlet = new Login();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        dispatcher = Mockito.mock(RequestDispatcher.class);
    }

    @Test
    void testDoGetForwardToLoginJsp() throws ServletException, IOException {
        // Arrange
        Mockito.when(request.getRequestDispatcher("/views/login.jsp"))
                .thenReturn(dispatcher);

        // Act
        servlet.doGet(request, response);

        // Assert
        Mockito.verify(request)
                .getRequestDispatcher("/views/login.jsp");

        Mockito.verify(dispatcher)
                .forward(request, response);
    }

    @Test
    void testDoPostForwardToAutenticacaoLoginJsp() throws ServletException, IOException {
        // Arrange
        Mockito.when(request.getRequestDispatcher("/views/autenticacao/login.jsp"))
                .thenReturn(dispatcher);

        // Act
        servlet.doPost(request, response);

        // Assert
        Mockito.verify(request)
                .getRequestDispatcher("/views/autenticacao/login.jsp");

        Mockito.verify(dispatcher)
                .forward(request, response);
    }
}
