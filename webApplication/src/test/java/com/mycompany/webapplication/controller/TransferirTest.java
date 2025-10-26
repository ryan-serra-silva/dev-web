package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.UserDAO;
import com.mycompany.webapplication.model.AccountTransactionalDAO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferirTest {

    @InjectMocks
    private Transferir transferirServlet;

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

    @Mock
    private UserDAO userDAO;

    @Mock
    private AccountTransactionalDAO transDAO;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    private Users remetente;
    private Users destinatario;
    private Account contaRemetente;
    private Account contaDestinatario;

    @BeforeEach
    public void setUp() {
        remetente = MockGenerator.createUser();
        remetente.setId(1L);
        remetente.setEmail("remetente@gmail.com");

        destinatario = MockGenerator.createUser();
        destinatario.setId(2L);
        destinatario.setEmail("destinatario@gmail.com");

        contaRemetente = MockGenerator.createAccount();
        contaRemetente.setId(1L);
        contaRemetente.setUserId(remetente.getId());
        contaRemetente.setBalance(new BigDecimal("1000.00"));

        contaDestinatario = MockGenerator.createAccount();
        contaDestinatario.setId(2L);
        contaDestinatario.setUserId(destinatario.getId());
        contaDestinatario.setBalance(new BigDecimal("500.00"));
    }

    @Test
    public void testDoPost_SaldoInsuficiente() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(remetente);
        when(request.getParameter("destino")).thenReturn(destinatario.getEmail());
        when(request.getParameter("valor")).thenReturn("2000"); // maior que saldo

        when(accountDAO.getByUserId(remetente.getId())).thenReturn(contaRemetente);
        when(userDAO.getByEmail(destinatario.getEmail())).thenReturn(destinatario);
        when(accountDAO.getByUserId(destinatario.getId())).thenReturn(contaDestinatario);

        when(request.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(dispatcher);

        transferirServlet.doPost(request, response);

        verify(request).setAttribute(eq("mensagem"), stringCaptor.capture());
        assertTrue(stringCaptor.getValue().contains("Saldo insuficiente"));
    }

    @Test
    public void testDoPost_TransferenciaSucesso() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(remetente);
        when(request.getParameter("destino")).thenReturn(destinatario.getEmail());
        when(request.getParameter("valor")).thenReturn("200");

        when(accountDAO.getByUserId(remetente.getId())).thenReturn(contaRemetente);
        when(userDAO.getByEmail(destinatario.getEmail())).thenReturn(destinatario);
        when(accountDAO.getByUserId(destinatario.getId())).thenReturn(contaDestinatario);

        when(request.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(dispatcher);

        transferirServlet.doPost(request, response);

        // Verifica se saldo foi atualizado
        assertEquals(new BigDecimal("1000.00"), contaRemetente.getBalance());
        assertEquals(new BigDecimal("500.00"), contaDestinatario.getBalance());

        verify(accountDAO).update(contaRemetente);
        verify(accountDAO).update(contaDestinatario);


        verify(request).setAttribute(eq("mensagem"), stringCaptor.capture());
        assertTrue(stringCaptor.getValue().contains("TransferÃªncia realizada com sucesso"));
    }

    @Test
    public void testDoGet_SessionNull() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        transferirServlet.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    public void testDoGet_Success() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(remetente);
        when(accountDAO.getByUserId(remetente.getId())).thenReturn(contaRemetente);
        when(request.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(dispatcher);

        transferirServlet.doGet(request, response);

        verify(request).setAttribute("usuario", remetente);
        verify(request).setAttribute("conta", contaRemetente);
        verify(dispatcher).forward(request, response);
    }
}
