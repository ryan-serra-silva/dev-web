package com.mycompany.webapplication.usecases;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Investment;
import com.mycompany.webapplication.entity.InvestmentProduct;
import com.mycompany.webapplication.entity.InvestmentType;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.InvestmentDAO;
import com.mycompany.webapplication.model.InvestmentTransactionalDAO;
import com.mycompany.webapplication.model.JDBC;

@ExtendWith(MockitoExtension.class)
class InvestimentoTest {

    @Mock
    AccountDAO accountDAO;

    @Mock
    Investment investment;

    @Mock
    InvestmentDAO investmentDAO;

    @Mock
    Account conta;

    @Mock
    InvestmentTransactionalDAO productDAO;

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
        uc = new InvestimentoUC(accountDAO, investmentDAO, productDAO, investment, jdbc);

        product = new InvestmentProduct();
        product.setTypeInvestment(InvestmentType.CDB);
    }


    @Test
    void validar_deveRetornarErroSeContaNula() {
        String msg = uc.validar(null, "CDB", BigDecimal.TEN, 12);
        assertEquals("Erro: Conta não encontrada.", msg);
    }
    @Test
    void validar_deveRetornarErroSeValorNulo() {
        String msg = uc.validar(conta, "CDB", null, 12);
        assertEquals("Erro: Valor inválido.", msg);
    }
    @Test
    void validar_deveRetornarErroSeTipoNulo() {
        String msg = uc.validar(conta, null, BigDecimal.TEN, 12);
        assertEquals("Erro: Tipo de investimento deve ser selecionado.", msg);
    }

    @Test
    void validar_deveRetornarErroSeValorNegativo() {
        String msg = uc.validar(conta, "CDB", BigDecimal.valueOf(-1), 10);
        assertEquals("Erro: Valor deve ser maior que zero.", msg);
    }

    @Test
    void validar_deveRetornarErroSeSaldoInsuficiente() {
        when(conta.getBalance()).thenReturn(BigDecimal.valueOf(50));

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
        when(conta.getBalance()).thenReturn(BigDecimal.valueOf(1000));

        String msg = uc.validar(conta, "CDB", BigDecimal.valueOf(100), 12);
        assertNull(msg);
    }


    @Test
    void executar_deveExecutarComSucesso() throws Exception {

        when(user.getId()).thenReturn(1L);

        conta.setId(10L);
        conta.setBalance(BigDecimal.valueOf(1000));
        when(conta.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        doNothing().when(conta).setBalance(BigDecimal.valueOf(800));


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

        conta.setId(10L);
        conta.setBalance(BigDecimal.valueOf(1000));
        when(conta.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        doNothing().when(conta).setBalance(BigDecimal.valueOf(800));

        when(accountDAO.getByUserId(1L)).thenReturn(conta);
        when(productDAO.getByType(InvestmentType.CDB)).thenReturn(product);
        when(jdbc.getConexao()).thenReturn(connection);

        doThrow(new RuntimeException("Falha no insert"))
                .when(investmentDAO)
                .insert(any(), eq(connection));

        String msg = uc.executar(user, "CDB", BigDecimal.valueOf(200), 12);

        assertEquals("Erro ao processar o investimento.", msg);
        verify(conta, times(2)).setBalance(any(BigDecimal.class));
        verify(investment, times(1)).setAmount(any(BigDecimal.class));
        verify(investment, times(1)).setStartDate(any(LocalDate.class));
        verify(investment, times(1)).setEndDate(any(LocalDate.class));
        verify(investment, times(1)).setAccount(any(Account.class));
        verify(investment, times(1)).setInvestmentProduct(any(InvestmentProduct.class));
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
