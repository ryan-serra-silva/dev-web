package com.mycompany.webapplication.usecases;

import java.math.BigDecimal;
import java.time.LocalTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.webapplication.MockGenerator;
import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.model.AccountDAO;
import static com.mycompany.webapplication.usecases.DepositoUC.processarDeposito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class DepositoTest {
    @Mock
    AccountDAO accountDAO;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;


    @Test
    void AccountDontNull() {
        Account conta = MockGenerator.createAccount();
        LocalTime agora = LocalTime.now();

        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        Assertions.assertNotEquals(
                "Erro: conta inválida ou inativa.",
                processarDeposito(14L, BigDecimal.valueOf(4000.0), accountDAO, request, agora)
        );

    }

    @Test
    void AccountNull() {
        HttpServletRequest request = null;
        LocalTime agora = LocalTime.now();
        when(accountDAO.getByUserId(14L)).thenReturn(null);

        Assertions.assertEquals(
                "Erro: conta inválida ou inativa.",
                processarDeposito(14L, BigDecimal.valueOf(4000.0), accountDAO, request, agora)
        );
    }

    @Test
    void testBloqueioInicio12h(){
        Account  conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);
        LocalTime horario = LocalTime.of(12, 0);
        String expected = "Depósitos não permitidos entre 12:00 e 12:30";
        String result = processarDeposito(14L,BigDecimal.valueOf(100), accountDAO,request,horario);
        Assertions.assertEquals(expected, result);

    }

    @Test
    void testBloqueioFim12h30() {
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(12, 30);

        String expected = "Depósitos não permitidos entre 12:00 e 12:30";
        String resultado = processarDeposito(14L, BigDecimal.valueOf(100), accountDAO, request, horario);
        Assertions.assertEquals(expected, resultado);
    }

    @Test
    void testBloqueioInicio18h() {
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(18, 0);

        String expected = "Depósitos não permitidos entre 18:00 e 18:30";
        String resultado = processarDeposito(14L, BigDecimal.valueOf(100), accountDAO, request, horario);
        Assertions.assertEquals(expected, resultado);
    }

    @Test
    void testBloqueioFim18h30() {
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(18, 30);

        String expected = "Depósitos não permitidos entre 18:00 e 18:30";
        String resultado = processarDeposito(14L, BigDecimal.valueOf(100), accountDAO, request, horario);
        Assertions.assertEquals(expected, resultado);
    }

    @Test
    void testForaDoSegundoHorarioDeBloqueio() {
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(19, 30);

        String expected = "Depósitos não permitidos entre 18:00 e 18:30";
        String resultado = processarDeposito(14L, BigDecimal.valueOf(100), accountDAO, request, horario);
        Assertions.assertNotEquals(expected, resultado);
    }

    @Test
    void testForaDoPrimeiroHorarioDeBloqueio() {
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(14, 30);

        String expected = "Depósitos não permitidos entre 12:00 e 13:30";
        String result = processarDeposito(14L, BigDecimal.valueOf(100), accountDAO, request, horario);
        Assertions.assertNotEquals(expected, result);
    }

    @Test
    void testValidaValorMinimo(){
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(14, 30);
        String expected = "Erro: valor menor que o depósito mínimo de R$10.";
        String result = processarDeposito(14L, BigDecimal.valueOf(9), accountDAO, request, horario);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testValidaValorMinimoAtingido(){
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(14, 30);
        String expected = "Erro: valor menor que o depósito mínimo de R$10";
        String result = processarDeposito(14L, BigDecimal.valueOf(12), accountDAO, request, horario);
        Assertions.assertNotEquals(expected, result);
    }

    @Test
    void testValidaValorMaximo(){
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(14, 30);
        String expected = "Erro: valor maior que o depósito máximo de R$5000.";
        String result = processarDeposito(14L, BigDecimal.valueOf(5002), accountDAO, request, horario);
        Assertions.assertEquals(expected, result);
    }
    @Test
    void testValidaValorMaximoRespeitado(){
    Account conta = MockGenerator.createAccount();
    when(accountDAO.getByUserId(14L)).thenReturn(conta);

    String result = processarDeposito(14L, BigDecimal.valueOf(5000), accountDAO, request, LocalTime.of(14, 30));

    Assertions.assertEquals("Depósito realizado com sucesso!", result);
    }


    @Test
    void validaSeValorImpar(){
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(14, 30);
        String expected = "Erro: valor não pode ser impar!";
        String result = processarDeposito(14L, BigDecimal.valueOf(4999), accountDAO, request, horario);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void validaSeValorMutiplode7(){
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(14, 30);
        String expected = "Erro: valor não pode ser múltiplo de 7!";
        String result = processarDeposito(14L, BigDecimal.valueOf(56), accountDAO, request, horario);
        Assertions.assertEquals(expected, result);
    }
    @Test
    void validaSeValorMutiplode11(){
        Account conta = MockGenerator.createAccount();
        when(accountDAO.getByUserId(14L)).thenReturn(conta);

        LocalTime horario = LocalTime.of(14, 30);
        String expected = "Erro: valor não pode ser múltiplo de 11!";
        String result = processarDeposito(14L, BigDecimal.valueOf(22), accountDAO, request, horario);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testAlertaSaldoAcimaDe10000() {
        Account conta = MockGenerator.createAccount();
        conta.setBalance(new BigDecimal("9500"));
        BigDecimal deposito = new BigDecimal("600");

        when(accountDAO.getByUserId(14L)).thenReturn(conta);
        String resultado = processarDeposito(14L, deposito, accountDAO, request, LocalTime.of(10, 0));

        verify(request).setAttribute("alerta", "Atenção: saldo acima de R$10.000!");
        Assertions.assertEquals("Depósito realizado com sucesso!", resultado);
    }
}