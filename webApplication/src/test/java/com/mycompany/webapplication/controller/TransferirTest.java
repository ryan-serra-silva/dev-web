package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.MockGenerator;
import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.UserDAO;
import com.mycompany.webapplication.model.AccountTransactionalDAO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;

import static com.mycompany.webapplication.usecases.TransferirUC.validateTransfer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyLong;


@ExtendWith(MockitoExtension.class)
public class TransferirTest {

    // ==== Mocks de infraestrutura ====

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    // ==== Mocks de dependências de negócio ====

    @Mock
    private AccountDAO accountDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private AccountTransactionalDAO transDAO;

    // ==== Objetos de domínio usados nos testes ====

    private Users remetente;
    private Users destinatario;
    private Account contaRemetente;
    private Account contaDestinatario;

    // Servlet a ser testado
    private Transferir servlet;

    @BeforeEach
    public void setUp() {
        remetente = MockGenerator.createUser();
        destinatario = MockGenerator.createUser();
        contaRemetente = MockGenerator.createAccount();
        contaDestinatario = MockGenerator.createAccount();

        // Garante e-mails diferentes e válidos
        remetente.setEmail("remetente@gmail.com");
        destinatario.setEmail("destino@gmail.com");

        // Saldo inicial “alto” para facilitar cenários
        contaRemetente.setBalance(new BigDecimal("1000.00"));
        contaDestinatario.setBalance(new BigDecimal("500.00"));

        // Usa o construtor com DAOs da classe Transferir
        servlet = new Transferir(accountDAO, userDAO, transDAO);
    }

    // =========================================================
    //  PARTE 1 – TESTES UNITÁRIOS DO MÉTODO validateTransfer
    // =========================================================

    @Test
    public void RemetenteNull() {
        String result = validateTransfer(null, destinatario.getEmail(), "100");
        String expected = "Sessão expirada. Faça login novamente.";
        assertEquals(expected, result);
    }

    @Test
    public void DestinatarioNull() {
        String result = validateTransfer(remetente, null, "100");
        String expected = "Informe o e-mail do destinatário.";
        assertEquals(expected, result);
    }

    @Test
    public void FormatoErradoEmail() {
        String result = validateTransfer(remetente, "emailsemarroba.com", "100");
        String expected = "E-mail do destinatário inválido.";
        assertEquals(expected, result);
    }

    @Test
    public void DominioBloqueado() {
        String result = validateTransfer(remetente, "teste@example.com", "100");
        String expected = "Transferências para este domínio estão bloqueadas.";
        assertEquals(expected, result);
    }

    @Test
    public void ValorNull() {
        String result = validateTransfer(remetente, destinatario.getEmail(), null);
        String expected = "Informe um valor numérico válido.";
        assertEquals(expected, result);
    }

    @Test
    public void ValorZero() {
        String result = validateTransfer(remetente, destinatario.getEmail(), "0");
        String expected = "Informe um valor maior que zero.";
        assertEquals(expected, result);
    }

    @Test
    public void ValorTresCasas() {
        String result = validateTransfer(remetente, destinatario.getEmail(), "10,2222");
        String expected = "Use no máximo três casas decimais.";
        assertEquals(expected, result);
    }

    @Test
    public void MesmaConta() {
        remetente.setEmail("abc@gmail.com");
        String result = validateTransfer(remetente, "abc@gmail.com", "100");
        String expected = "Não é possível transferir para a própria conta.";
        assertEquals(expected, result);
    }

    @Test
    public void AbaixoMinimo() {
        destinatario.setEmail("123@gmail.com");
        String result = validateTransfer(remetente, destinatario.getEmail(), "0,001");
        String expected = "Valor mínimo por transferência é R$ 0.01.";
        assertEquals(expected, result);
    }

    @Test
    public void AcimaMaximo() {
        destinatario.setEmail("123@gmail.com");
        String result = validateTransfer(remetente, destinatario.getEmail(), "1000000.00");
        String expected = "Valor máximo por transferência é R$100000.00.";
        assertEquals(expected, result);
    }

    // Do Post

    private void configurarRequestBasico(String valor, String emailDestino) throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(remetente);

        when(request.getParameter("destino")).thenReturn(emailDestino);
        when(request.getParameter("valor")).thenReturn(valor);

        when(request.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(dispatcher);
    }


    @Test
    public void doPost_ErroValidacaoInicial_EmailInvalido() throws Exception {
        configurarRequestBasico("100", "emailsemarroba.com");

        servlet.doPost(request, response);

        verify(request).setAttribute("mensagem", "E-mail do destinatário inválido.");
        verify(dispatcher).forward(request, response);
        verifyNoInteractions(accountDAO, userDAO, transDAO);
    }


    @Test
    public void doPost_ContaRemetenteNull() throws Exception {
        configurarRequestBasico("100,00", destinatario.getEmail());

        when(accountDAO.getByUserId(anyLong())).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute("mensagem", "Conta do remetente não encontrada.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void doPost_DestinatarioNaoEncontrado() throws Exception {
        configurarRequestBasico("100,00", destinatario.getEmail());

        when(accountDAO.getByUserId(remetente.getId())).thenReturn(contaRemetente);
        when(userDAO.getByEmail(destinatario.getEmail())).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute("mensagem", "Não há nenhuma conta vinculada a esse e-mail.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void doPost_ContaDestinatarioNull() throws Exception {
        // configura o básico: sessão, parâmetros e dispatcher
        configurarRequestBasico("100,00", destinatario.getEmail());

        // 1ª chamada de getByUserId (remetente) -> conta válida
        // 2ª chamada de getByUserId (destinatário) -> null
        when(accountDAO.getByUserId(anyLong()))
                .thenReturn(contaRemetente)  // remetente OK
                .thenReturn(null);           // destinatário sem conta

        // destinatário existe como usuário, mas a conta dele não
        when(userDAO.getByEmail(destinatario.getEmail())).thenReturn(destinatario);

        // dispatcher para a tela de transferência
        when(request.getRequestDispatcher("/views/transferencia.jsp"))
                .thenReturn(dispatcher);

        servlet.doPost(request, response);

        // aqui é o ponto principal do teste:
        verify(request).setAttribute("mensagem", "Conta do destinatário não encontrada.");
        verify(dispatcher).forward(request, response);
    }




    @Test
    public void doPost_SaldoInsuficiente() throws Exception {
        configurarRequestBasico("1.000,00", destinatario.getEmail());

        // Saldo menor que o valor da transferência
        contaRemetente.setBalance(new BigDecimal("100.00"));

        when(accountDAO.getByUserId(remetente.getId())).thenReturn(contaRemetente);
        when(userDAO.getByEmail(destinatario.getEmail())).thenReturn(destinatario);
        when(accountDAO.getByUserId(destinatario.getId())).thenReturn(contaDestinatario);

        servlet.doPost(request, response);

        verify(request).setAttribute("mensagem", "Saldo insuficiente para transferência.");
        verify(dispatcher).forward(request, response);
    }


    @Test
    public void doPost_Sucesso() throws Exception {
        configurarRequestBasico("100,00", destinatario.getEmail());

        when(accountDAO.getByUserId(remetente.getId())).thenReturn(contaRemetente);
        when(userDAO.getByEmail(destinatario.getEmail())).thenReturn(destinatario);
        when(accountDAO.getByUserId(destinatario.getId())).thenReturn(contaDestinatario);

        // getByUserId chamado novamente para carregar conta atualizada na tela
        when(accountDAO.getByUserId(remetente.getId())).thenReturn(contaRemetente);

        servlet.doPost(request, response);

        verify(accountDAO, atLeastOnce()).update(any(Account.class));
        verify(transDAO, times(2)).insert(any()); // OUT e IN

        verify(request).setAttribute("mensagem", "Transferência realizada com sucesso!");
        verify(request).setAttribute("usuario", remetente);
        verify(request, atLeastOnce()).setAttribute(eq("conta"), any(Account.class));
        verify(dispatcher).forward(request, response);
    }


    @Test
    public void doPost_ErroNaTransacao() throws Exception {
        configurarRequestBasico("100,00", destinatario.getEmail());

        when(accountDAO.getByUserId(remetente.getId())).thenReturn(contaRemetente);
        when(userDAO.getByEmail(destinatario.getEmail())).thenReturn(destinatario);
        when(accountDAO.getByUserId(destinatario.getId())).thenReturn(contaDestinatario);

        // Primeira chamada ao insert lança exceção
        doThrow(new RuntimeException("DB ERROR"))
                .when(transDAO).insert(any());

        servlet.doPost(request, response);

        verify(request).setAttribute("mensagem",
                "Erro ao processar a transferência. Tente novamente.");
        verify(dispatcher).forward(request, response);
    }


    @Test
    public void doGet_SemUsuarioNaSessao() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
        verifyNoMoreInteractions(response);
        verifyNoInteractions(accountDAO, userDAO, transDAO);
    }


    @Test
    public void doGet_ComUsuarioNaSessao() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(remetente);
        when(accountDAO.getByUserId(remetente.getId())).thenReturn(contaRemetente);
        when(request.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("usuario", remetente);
        verify(request).setAttribute("conta", contaRemetente);
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }
}
