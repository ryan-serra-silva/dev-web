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
import static com.mycompany.webapplication.usecases.TransferirUC.validateTransfer;

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
        destinatario = MockGenerator.createUser();
        contaRemetente = MockGenerator.createAccount();
        contaDestinatario = MockGenerator.createAccount();

    }

    @Test
    public void RemetenteNull(){
        String result = validateTransfer(null, destinatario.getEmail(), "100");
        String expected = "Sessão expirada. Faça login novamente.";
        assertEquals(expected,result);
    }

    @Test
    public void DestinatarioNull(){
        String result = validateTransfer(remetente, null, "100");
        String expected = "Informe o e-mail do destinatário.";
        assertEquals(expected,result);
    }

    @Test
    public void FormatoErradoEmail(){
        String result = validateTransfer(remetente, "emailsemarroba.com", "100");
        String expected = "E-mail do destinatário inválido.";
        assertEquals(expected,result);
    }

    @Test
    public void DominioBloqueado(){
        String result = validateTransfer(remetente, "teste@example.com", "100");
        String expected = "Transferências para este domínio estão bloqueadas.";
        assertEquals(expected,result);
    }

    @Test
    public void ValorNull() {
        String result = validateTransfer(remetente, destinatario.getEmail(), null);
        String expected = "Informe um valor numérico válido.";
        assertEquals(expected,result);
    }

    @Test
    public void ValorZero() {
        String result = validateTransfer(remetente, destinatario.getEmail(), "0");
        String expected = "Informe um valor maior que zero.";
        assertEquals(expected,result);
    }

    @Test
    public void ValorTresCasas() {
        String result = validateTransfer(remetente, destinatario.getEmail(), "10,2222");
        String expected = "Use no máximo três casas decimais.";
        assertEquals(expected,result);
    }

    @Test
    public void MesmaConta() {
        String result = validateTransfer(remetente, remetente.getEmail(), "100");
        String expected = "Não é possível transferir para a própria conta.";
        assertEquals(expected,result);
    }

    @Test
    public void AbaixoMinimo() {
        destinatario.setEmail("123@gmail.com");
        String result = validateTransfer(remetente, destinatario.getEmail(), "0,001");
        String expected = "Valor mínimo por transferência é R$ 0.01.";
        assertEquals(expected,result);
    }

    @Test
    public void AcimaMaximo() {
        destinatario.setEmail("123@gmail.com");
        String result = validateTransfer(remetente, destinatario.getEmail(), "1000000.00");
        String expected = "Valor máximo por transferência é R$100000.00.";
        assertEquals(expected,result);
    }

}
