package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.*;
import com.mycompany.webapplication.model.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class TransferirTest {


    private HttpServletRequest req() { return mock(HttpServletRequest.class, RETURNS_DEEP_STUBS); }
    private HttpServletResponse resp() { return mock(HttpServletResponse.class); }
    private HttpSession sessaoCom(Users u) {
        HttpSession s = mock(HttpSession.class);
        when(s.getAttribute("usuario")).thenReturn(u);
        return s;
    }
    private RequestDispatcher rdOK() {
        RequestDispatcher rd = mock(RequestDispatcher.class);
        // forward -> só pra não explodir
        doAnswer(inv -> null).when(rd).forward(any(), any());
        return rd;
    }
    private Users usuario(int id, String nome, String email) {
        Users u = mock(Users.class);
        when(u.getId()).thenReturn(id);
        when(u.getName()).thenReturn(nome);
        when(u.getEmail()).thenReturn(email);
        return u;
    }
    private Account conta(BigDecimal saldo) {
        Account a = mock(Account.class);
        when(a.getBalance()).thenReturn(saldo);
        // quando setar balance, atualiza o getter
        doAnswer(inv -> { when(a.getBalance()).thenReturn(inv.getArgument(0)); return null; })
                .when(a).setBalance(any(BigDecimal.class));
        return a;
    }

    // D1: sessão expirada 
    @Test @DisplayName("D1 - deve avisar quando não está logado")
    void naoLogado() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        when(req.getSession()).thenReturn(mock(HttpSession.class)); 
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("mensagem"), contains("Sessão expirada"));
        verify(rd).forward(eq(req), eq(resp));
    }

    // D2: email vazio 
    @Test @DisplayName("D2 - deve pedir e-mail do destinatário")
    void emailVazio() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        when(req.getSession()).thenReturn(sessaoCom(usuario(1,"A","a@b.com")));
        when(req.getParameter("destino")).thenReturn("   ");
        when(req.getParameter("valor")).thenReturn("10.00");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("mensagem"), contains("Informe o e-mail"));
        verify(rd).forward(eq(req), eq(resp));
    }

    // D3: email inválido 
    @Test @DisplayName("D3 - deve rejeitar e-mail inválido")
    void emailInvalido() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        when(req.getSession()).thenReturn(sessaoCom(usuario(1,"A","a@b.com")));
        when(req.getParameter("destino")).thenReturn("bob@invalid");
        when(req.getParameter("valor")).thenReturn("10.00");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("mensagem"), contains("inválido"));
        verify(rd).forward(eq(req), eq(resp));
    }

    // D4: domínio bloqueado
    @Test @DisplayName("D4 - deve bloquear domínio proibido")
    void dominioBloqueado() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        when(req.getSession()).thenReturn(sessaoCom(usuario(1,"A","a@b.com")));
        when(req.getParameter("destino")).thenReturn("joe@example.com");
        when(req.getParameter("valor")).thenReturn("10.00");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("mensagem"), contains("bloquead"));
        verify(rd).forward(eq(req), eq(resp));
    }

    // D5: valor ausente
    @Test @DisplayName("D5 - deve pedir valor numérico")
    void valorAusente() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        when(req.getSession()).thenReturn(sessaoCom(usuario(1,"A","a@b.com")));
        when(req.getParameter("destino")).thenReturn("b@ok.com");
        when(req.getParameter("valor")).thenReturn(null);
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("mensagem"), contains("numérico válido"));
        verify(rd).forward(eq(req), eq(resp));
    }

    // D6: valor <= 0
    @Test @DisplayName("D6 - não aceita valor <= 0")
    void valorMenorOuIgualZero() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        when(req.getSession()).thenReturn(sessaoCom(usuario(1,"A","a@b.com")));
        when(req.getParameter("destino")).thenReturn("b@ok.com");
        when(req.getParameter("valor")).thenReturn("0");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("mensagem"), contains("maior que zero"));
        verify(rd).forward(eq(req), eq(resp));
    }

    // D7: > 2 casas
    @Test @DisplayName("D7 - deve recusar quando tem mais de duas casas")
    void maisDeDuasCasas() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        when(req.getSession()).thenReturn(sessaoCom(usuario(1,"A","a@b.com")));
        when(req.getParameter("destino")).thenReturn("b@ok.com");
        when(req.getParameter("valor")).thenReturn("10.375");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("mensagem"), contains("duas casas"));
        verify(rd).forward(eq(req), eq(resp));
    }

    // D8: mesma conta
    @Test @DisplayName("D8 - não pode transferir para si mesmo")
    void mesmaConta() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        Users remetente = usuario(1,"A","a@b.com");
        when(req.getSession()).thenReturn(sessaoCom(remetente));
        when(req.getParameter("destino")).thenReturn("a@b.com");
        when(req.getParameter("valor")).thenReturn("10.00");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("mensagem"), contains("própria conta"));
        verify(rd).forward(eq(req), eq(resp));
    }

    // D9: mínimo
    @Test @DisplayName("D9 - deve recusar abaixo do mínimo")
    void abaixoDoMinimo() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        when(req.getSession()).thenReturn(sessaoCom(usuario(1,"A","a@b.com")));
        when(req.getParameter("destino")).thenReturn("b@ok.com");
        when(req.getParameter("valor")).thenReturn("0.001");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("mensagem"), contains("mínimo"));
        verify(rd).forward(eq(req), eq(resp));
    }

    // D10: máximo
    @Test @DisplayName("D10 - deve recusar acima do máximo")
    void acimaDoMaximo() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        when(req.getSession()).thenReturn(sessaoCom(usuario(1,"A","a@b.com")));
        when(req.getParameter("destino")).thenReturn("b@ok.com");
        when(req.getParameter("valor")).thenReturn("1000000.00");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("mensagem"), contains("máximo"));
        verify(rd).forward(eq(req), eq(resp));
    }

    // D11 - D14 
    @Test @DisplayName("D11 - deve avisar se conta do remetente não existe")
    void contaRemetenteNula() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        Users remetente = usuario(1,"A","a@b.com");
        when(req.getSession()).thenReturn(sessaoCom(remetente));
        when(req.getParameter("destino")).thenReturn("b@ok.com");
        when(req.getParameter("valor")).thenReturn("10.00");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        try (MockedConstruction<AccountDAO> mcAcc =
                     Mockito.mockConstruction(AccountDAO.class, (mock, ctx) -> when(mock.getByUserId(1)).thenReturn(null));
             MockedConstruction<UserDAO> mcUser =
                     Mockito.mockConstruction(UserDAO.class, (mock, ctx) -> when(mock.getByEmail("b@ok.com")).thenReturn(usuario(2,"B","b@ok.com")))) {

            servlet.doPost(req, resp);

            verify(req).setAttribute(eq("mensagem"), contains("remetente não encontrada"));
            verify(rd).forward(eq(req), eq(resp));
        }
    }

    @Test @DisplayName("D12 - deve avisar se usuário destino não existe")
    void destinatarioInexistente() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        when(req.getSession()).thenReturn(sessaoCom(usuario(1,"A","a@b.com")));
        when(req.getParameter("destino")).thenReturn("x@y.com");
        when(req.getParameter("valor")).thenReturn("10.00");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        try (MockedConstruction<UserDAO> mcUser =
                     Mockito.mockConstruction(UserDAO.class, (mock, ctx) -> when(mock.getByEmail("x@y.com")).thenReturn(null))) {

            servlet.doPost(req, resp);

            verify(req).setAttribute(eq("mensagem"), contains("nenhuma conta vinculada"));
            verify(rd).forward(eq(req), eq(resp));
        }
    }

    @Test @DisplayName("D13 - deve avisar se conta do destinatário não existe")
    void contaDestinatarioNula() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        Users remetente = usuario(1,"A","a@b.com");
        Users destinatario = usuario(2,"B","b@ok.com");

        when(req.getSession()).thenReturn(sessaoCom(remetente));
        when(req.getParameter("destino")).thenReturn("b@ok.com");
        when(req.getParameter("valor")).thenReturn("10.00");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        try (MockedConstruction<AccountDAO> mcAcc =
                     Mockito.mockConstruction(AccountDAO.class, (mock, ctx) -> when(mock.getByUserId(1)).thenReturn(conta(new BigDecimal("100.00"))));
             MockedConstruction<UserDAO> mcUser =
                     Mockito.mockConstruction(UserDAO.class, (mock, ctx) -> when(mock.getByEmail("b@ok.com")).thenReturn(destinatario));
             MockedConstruction<AccountTransactionalDAO> mcTx =
                     Mockito.mockConstruction(AccountTransactionalDAO.class)) {

            // a conta do destinatário retorna null
            mcAcc.constructed().get(0); // só para garantir 
            when(mcAcc.constructed().get(0).getByUserId(2)).thenReturn(null);

            servlet.doPost(req, resp);

            verify(req).setAttribute(eq("mensagem"), contains("destinatário não encontrada"));
            verify(rd).forward(eq(req), eq(resp));
        }
    }

    @Test @DisplayName("D14 - deve recusar quando saldo é insuficiente")
    void saldoInsuficiente() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();
        Users remetente = usuario(1,"A","a@b.com");
        Users destinatario = usuario(2,"B","b@ok.com");

        when(req.getSession()).thenReturn(sessaoCom(remetente));
        when(req.getParameter("destino")).thenReturn("b@ok.com");
        when(req.getParameter("valor")).thenReturn("9999.99");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        Account origem = conta(new BigDecimal("100.00"));
        Account destino = conta(new BigDecimal("50.00"));

        try (MockedConstruction<AccountDAO> mcAcc =
                     Mockito.mockConstruction(AccountDAO.class,
                             (mock, ctx) -> {
                                 when(mock.getByUserId(1)).thenReturn(origem);
                                 when(mock.getByUserId(2)).thenReturn(destino);
                             });
             MockedConstruction<UserDAO> mcUser =
                     Mockito.mockConstruction(UserDAO.class,
                             (mock, ctx) -> when(mock.getByEmail("b@ok.com")).thenReturn(destinatario))) {

            servlet.doPost(req, resp);

            // não atualiza nada e mostra mensagem
            AccountDAO accDao = mcAcc.constructed().get(0);
            verify(accDao, never()).update(any(Account.class));
            verify(req).setAttribute(eq("mensagem"), contains("Saldo insuficiente"));
            verify(rd).forward(eq(req), eq(resp));
        }
    }

    // Caminho feliz
    @Test @DisplayName("OK - deve transferir e registrar transações")
    void caminhoFeliz() throws Exception {
        var servlet = new Transferir();
        var req = req(); var resp = resp(); var rd = rdOK();

        Users remetente = usuario(1,"Alice","alice@bank.com");
        Users destinatario = usuario(2,"Bob","bob@bank.com");
        when(req.getSession()).thenReturn(sessaoCom(remetente));
        when(req.getParameter("destino")).thenReturn("bob@bank.com");
        when(req.getParameter("valor")).thenReturn("250.00");
        when(req.getRequestDispatcher("/views/transferencia.jsp")).thenReturn(rd);

        Account origem = conta(new BigDecimal("1000.00"));
        Account destino = conta(new BigDecimal("100.00"));

        try (MockedConstruction<AccountDAO> mcAcc =
                     Mockito.mockConstruction(AccountDAO.class,
                             (mock, ctx) -> {
                                 when(mock.getByUserId(1)).thenReturn(origem);
                                 when(mock.getByUserId(2)).thenReturn(destino);
                             });
             MockedConstruction<UserDAO> mcUser =
                     Mockito.mockConstruction(UserDAO.class,
                             (mock, ctx) -> when(mock.getByEmail("bob@bank.com")).thenReturn(destinatario));
             MockedConstruction<AccountTransactionalDAO> mcTx =
                     Mockito.mockConstruction(AccountTransactionalDAO.class,
                             (mock, ctx) -> doNothing().when(mock).insert(any(AccountTransactional.class)))) {

            servlet.doPost(req, resp);

            assertEquals(new BigDecimal("750.00"), origem.getBalance());
            assertEquals(new BigDecimal("350.00"), destino.getBalance());

            AccountDAO accDao = mcAcc.constructed().get(0);
            verify(accDao, times(1)).update(origem);
            verify(accDao, times(1)).update(destino);

            AccountTransactionalDAO txDao = mcTx.constructed().get(0);
            verify(txDao, times(2)).insert(any(AccountTransactional.class));

            verify(req).setAttribute(eq("mensagem"), contains("sucesso"));
            verify(rd).forward(eq(req), eq(resp));
        }
    }
}
