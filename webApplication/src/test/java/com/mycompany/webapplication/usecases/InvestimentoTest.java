package com.mycompany.webapplication.usecases;

import com.mycompany.webapplication.entity.*;
import com.mycompany.webapplication.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestimentoUCTest {

    @Mock
    AccountDAO accountDAO;

    @Mock
    InvestmentDAO investmentDAO;

    @Mock
    InvestmentProductDAO productDAO;

    @Mock
    JDBC jdbc;

    @Mock
    Connection connection;

    @Mock
    Users user;

    InvestimentoUC uc;

    InvestmentProduct product;

    @BeforeEach
    void setup() throws Exception {
        uc = new InvestimentoUC(accountDAO, investmentDAO, productDAO, jdbc);

        product = new InvestmentProduct();
        product.setTypeInvestment(InvestmentType.CDB);
    }


    @Test
    void validar_deveRetornarErroSeContaNula() {
        String msg = uc.validar(null, "CDB", BigDecimal.TEN, 12);
        assertEquals("Erro: Conta não encontrada.", msg);
    }

    @Test
    void validar_deveRetornarErroSeValorNegativo() {
        Account conta = new Account();
        String msg = uc.validar(conta, "CDB", BigDecimal.valueOf(-1), 10);
        assertEquals("Erro: Valor deve ser maior que zero.", msg);
    }

    @Test
    void validar_deveRetornarErroSeSaldoInsuficiente() {
        Account conta = new Account();
        conta.setBalance(BigDecimal.valueOf(50));

        String msg = uc.validar(conta, "CDB", BigDecimal.valueOf(100), 10);
        assertEquals("Erro: Saldo insuficiente para realizar o investimento. Saldo disponível: R$ 50", msg);
    }

    @Test
    void validar_deveRetornarErroSeTempoInvalido() {
        Account conta = new Account();
        conta.setBalance(BigDecimal.valueOf(500));

        String msg = uc.validar(conta, "CDB", BigDecimal.TEN, 0);
        assertEquals("Erro: O prazo do investimento deve ser maior que zero.", msg);
    }

    @Test
    void validar_deveRetornarNullQuandoValido() {
        Account conta = new Account();
        conta.setBalance(BigDecimal.valueOf(500));

        String msg = uc.validar(conta, "CDB", BigDecimal.valueOf(100), 12);
        assertNull(msg);
    }


    @Test
    void executar_deveExecutarComSucesso() throws Exception {

        when(user.getId()).thenReturn(1L);

        Account conta = new Account();
        conta.setId(10L);
        conta.setBalance(BigDecimal.valueOf(1000));

        when(accountDAO.getByUserId(1L)).thenReturn(conta);
        when(productDAO.getByType(InvestmentType.CDB)).thenReturn(product);

        when(jdbc.getConexao()).thenReturn(connection);

        doNothing().when(investmentDAO).insert(any(Investment.class), eq(connection));
        doNothing().when(accountDAO).update(any(Account.class), eq(connection));

        String resp = uc.executar(user, "CDB", BigDecimal.valueOf(200), 12);

        assertNull(resp);
        verify(connection).commit();
        verify(connection, never()).rollback();
    }

    @Test
    void executar_deveFazerRollbackSeFalharInsert() throws Exception {

        when(user.getId()).thenReturn(1L);

        Account conta = new Account();
        conta.setId(10L);
        conta.setBalance(BigDecimal.valueOf(1000));

        when(accountDAO.getByUserId(1L)).thenReturn(conta);
        when(productDAO.getByType(InvestmentType.CDB)).thenReturn(product);
        when(jdbc.getConexao()).thenReturn(connection);

        doThrow(new RuntimeException("Falha no insert"))
                .when(investmentDAO)
                .insert(any(), eq(connection));

        String msg = uc.executar(user, "CDB", BigDecimal.valueOf(200), 12);

        assertEquals("Erro ao processar o investimento.", msg);
        verify(connection).rollback();
    }

    @Test
    void executar_deveRetornarMensagemValidacao() throws Exception {

        when(user.getId()).thenReturn(1L);

        Account conta = new Account();
        conta.setId(10L);
        conta.setBalance(BigDecimal.valueOf(50));

        when(accountDAO.getByUserId(1L)).thenReturn(conta);

        String msg = uc.executar(user, "CDB", BigDecimal.valueOf(100), 12);

        assertEquals("Erro: Saldo insuficiente para realizar o investimento. Saldo disponível: R$ 50", msg);
    }
}
