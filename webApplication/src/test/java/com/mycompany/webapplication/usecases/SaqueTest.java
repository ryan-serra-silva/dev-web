package com.mycompany.webapplication.usecases;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.model.AccountDAO;
import static com.mycompany.webapplication.usecases.SaqueUC.validarSaque;

@ExtendWith(MockitoExtension.class)
public class SaqueTest {

    @Mock
    AccountDAO accountDAO;

    @Test
    void deveRetornarErroQuandoContaForNula() {
        when(accountDAO.getByUserId(1L)).thenReturn(null);

        String resultado = validarSaque(1L, BigDecimal.TEN, LocalTime.of(10, 0), accountDAO);

        assertEquals("Erro: conta inválida ou inativa.", resultado);
    }

    @Test
    void deveBloquearSaqueEntre12he12h30m() {
        Account conta = new Account();
        conta.setBalance(new BigDecimal("1000"));
        when(accountDAO.getByUserId(1L)).thenReturn(conta);

        String resultado = validarSaque(1L, BigDecimal.TEN, LocalTime.of(12, 15), accountDAO);

        assertEquals("Saque não permitido entre 12:00 e 12:30.", resultado);
    }

    @Test
    void deveBloquearSaqueEntre18he18h30m() {
        Account conta = new Account();
        conta.setBalance(new BigDecimal("1000"));
        when(accountDAO.getByUserId(1L)).thenReturn(conta);

        String resultado = validarSaque(1L, BigDecimal.TEN, LocalTime.of(18, 15), accountDAO);

        assertEquals("Saque não permitido entre 18:00 e 18:30.", resultado);
    }

    @Test
    void deveRetornarErroQuandoValorMenorQueMinimo() {
        Account conta = new Account();
        conta.setBalance(new BigDecimal("1000"));
        when(accountDAO.getByUserId(1L)).thenReturn(conta);

        String resultado = validarSaque(1L, new BigDecimal("5"), LocalTime.of(10, 0), accountDAO);

        assertEquals("Erro: valor menor que o saque mínimo.", resultado);
    }

    // Cenário 5: Valor maior que o máximo
    @Test
    void deveRetornarErroQuandoValorMaiorQueMaximo() {
        Account conta = new Account();
        conta.setBalance(new BigDecimal("5000"));
        when(accountDAO.getByUserId(1L)).thenReturn(conta);

        String resultado = validarSaque(1L, new BigDecimal("3000"), LocalTime.of(10, 0), accountDAO);

        assertEquals("Erro: valor maior que o saque máximo.", resultado);
    }

    @Test
    void deveRetornarErroQuandoValorNaoMultiploDe10() {
        Account conta = new Account();
        conta.setBalance(new BigDecimal("1000"));
        when(accountDAO.getByUserId(1L)).thenReturn(conta);

        String resultado = validarSaque(1L, new BigDecimal("25"), LocalTime.of(10, 0), accountDAO);

        assertEquals("Erro: valor deve ser múltiplo de 10.", resultado);
    }

    @Test
    void deveRetornarErroQuandoSaldoInsuficiente() {
        Account conta = new Account();
        conta.setBalance(new BigDecimal("50"));
        when(accountDAO.getByUserId(1L)).thenReturn(conta);

        String resultado = validarSaque(1L, new BigDecimal("100"), LocalTime.of(10, 0), accountDAO);

        assertEquals("Erro: saldo insuficiente.", resultado);
    }

    @Test
    void devePermitirSaqueValido() {
        Account conta = new Account();
        conta.setBalance(new BigDecimal("1000"));
        when(accountDAO.getByUserId(1L)).thenReturn(conta);

        String resultado = validarSaque(1L, new BigDecimal("100"), LocalTime.of(9, 0), accountDAO);

        assertNull(resultado);
    }
}
