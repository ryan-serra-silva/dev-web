package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountTransactionalDAOTest {

    @Mock
    private JDBC jdbc;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement ps;

    @Mock
    private ResultSet rs;

    @InjectMocks
    private AccountTransactionalDAO dao;

    @BeforeEach
    void setup() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
    }

    @Test
    void get_deveRetornarTransacaoQuandoEncontrada() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getLong("id")).thenReturn(10L);
        when(rs.getString("type_transaction")).thenReturn("DEPOSIT");
        when(rs.getBigDecimal("amount")).thenReturn(BigDecimal.TEN);
        when(rs.getTimestamp("timestamp")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2024, 1, 10, 10, 0)));
        when(rs.getString("description")).thenReturn("Teste");
        when(rs.getLong("account_id")).thenReturn(5L);

        AccountTransactional t = dao.get(10);

        assertNotNull(t);
        assertEquals(10L, t.getId());
        assertEquals(TransactionType.DEPOSIT, t.getTypeTransaction());
        assertEquals(BigDecimal.TEN, t.getAmount());
        assertEquals("Teste", t.getDescription());
        assertEquals(5L, t.getAccount().getId());
    }
@Test
void update_deveChamarTodosOsSettersDoPreparedStatement() throws Exception {
    Account conta = new Account();
    conta.setId(50L);

    AccountTransactional t = new AccountTransactional();
    t.setId(99L);
    t.setAccount(conta);
    t.setAmount(BigDecimal.valueOf(777));
    t.setDescription("ABC");
    t.setTypeTransaction(TransactionType.WITHDRAW);
    t.setTimestamp(LocalDateTime.of(2024, 1, 10, 10, 30));

    when(connection.prepareStatement(anyString())).thenReturn(ps);
    when(ps.executeUpdate()).thenReturn(1);

    dao.update(t);

    verify(ps).setString(1, "WITHDRAW");
    verify(ps).setBigDecimal(2, BigDecimal.valueOf(777));
    verify(ps).setTimestamp(3, Timestamp.valueOf(t.getTimestamp()));
    verify(ps).setString(4, "ABC");
    verify(ps).setLong(5, 50L);
    verify(ps).setLong(6, 99L);
}

@Test
void update_deveUsarValoresCorretosNosParametros() throws Exception {
    Account conta = new Account();
    conta.setId(44L);

    AccountTransactional t = new AccountTransactional();
    t.setId(88L);
    t.setAccount(conta);
    t.setAmount(BigDecimal.TEN);
    t.setDescription("DescX");
    t.setTypeTransaction(TransactionType.DEPOSIT);
    t.setTimestamp(LocalDateTime.of(2024, 1, 1, 15, 0));

    when(connection.prepareStatement(anyString())).thenReturn(ps);
    when(ps.executeUpdate()).thenReturn(1);

    dao.update(t);

    ArgumentCaptor<String> capString = ArgumentCaptor.forClass(String.class);
    verify(ps).setString(eq(1), capString.capture());
    assertEquals("DEPOSIT", capString.getValue());
}


    @Test
    void get_deveRetornarNullQuandoNaoEncontrada() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        AccountTransactional t = dao.get(999);

        assertNull(t);
    }


    @Test
    void getAllByAccountId_deveRetornarListaDeTransacoes() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, false);

        when(rs.getLong("id")).thenReturn(1L, 2L);
        when(rs.getString("type_transaction")).thenReturn("WITHDRAW", "DEPOSIT");
        when(rs.getBigDecimal("amount")).thenReturn(BigDecimal.ONE, BigDecimal.TEN);
        when(rs.getTimestamp("timestamp")).thenReturn(
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now())
        );
        when(rs.getString("description")).thenReturn("A", "B");
        when(rs.getLong("account_id")).thenReturn(7L, 7L);

        ArrayList<AccountTransactional> lista = dao.getAllByAccountId(7L);

        assertEquals(2, lista.size());
        assertEquals(1L, lista.get(0).getId());
        assertEquals(2L, lista.get(1).getId());
    }

    // ------------------------------------------------------------------------------------
    // GET ALL
    // ------------------------------------------------------------------------------------
    @Test
    void getAll_deveRetornarTodasTransacoes() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("type_transaction")).thenReturn("DEPOSIT");
        when(rs.getBigDecimal("amount")).thenReturn(BigDecimal.ONE);
        when(rs.getTimestamp("timestamp")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getString("description")).thenReturn("teste");
        when(rs.getLong("account_id")).thenReturn(3L);

        ArrayList<AccountTransactional> lista = dao.getAll();

        assertEquals(1, lista.size());
        assertEquals(1L, lista.get(0).getId());
    }

    // ------------------------------------------------------------------------------------
    // INSERT
    // ------------------------------------------------------------------------------------
    @Test
    void insert_deveInserirTransacaoEPreencherIdGerado() throws Exception {
        Account conta = new Account();
        conta.setId(5L);

        AccountTransactional t = new AccountTransactional();
        t.setAccount(conta);
        t.setDescription("Teste");
        t.setTimestamp(LocalDateTime.now());
        t.setAmount(BigDecimal.TEN);
        t.setTypeTransaction(TransactionType.DEPOSIT);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(ps);

        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getLong(1)).thenReturn(123L);

        dao.insert(t);

        assertEquals(123L, t.getId());
        verify(ps).executeUpdate();
    }


    @Test
    void update_deveAtualizarQuandoEncontrarRegistro() throws Exception {
        Account conta = new Account();
        conta.setId(2L);

        AccountTransactional t = new AccountTransactional();
        t.setId(10L);
        t.setAccount(conta);
        t.setAmount(BigDecimal.ONE);
        t.setDescription("X");
        t.setTypeTransaction(TransactionType.DEPOSIT);
        t.setTimestamp(LocalDateTime.now());

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> dao.update(t));
    }

    @Test
    void update_deveLancarErroSeRegistroNaoForEncontrado() throws Exception {
        Account conta = new Account();
        conta.setId(2L);

        AccountTransactional t = new AccountTransactional();
        t.setId(10L);
        t.setAccount(conta);
        t.setAmount(BigDecimal.ONE);
        t.setDescription("X");
        t.setTypeTransaction(TransactionType.DEPOSIT);
        t.setTimestamp(LocalDateTime.now());

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0); // registro inexistente

        assertThrows(RuntimeException.class, () -> dao.update(t));
    }

    @Test
    void delete_deveChamarExecuteUpdate() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);

        assertDoesNotThrow(() -> dao.delete(5));

        verify(ps).executeUpdate();
    }
}
