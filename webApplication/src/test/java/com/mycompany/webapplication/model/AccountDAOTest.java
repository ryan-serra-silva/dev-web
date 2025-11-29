package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountDAOTest {

    @Mock JDBC jdbc;
    @Mock Connection conn;
    @Mock PreparedStatement stmt;
    @Mock ResultSet rs;

    AccountDAO dao;
    @BeforeEach
    void setUp() {
        dao = new AccountDAO(jdbc);
    }

    @Test
    void deveRetornarAccountPorId() throws Exception {

        when(jdbc.getConexao()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("account_number")).thenReturn("12345-6");
        when(rs.getString("agency")).thenReturn("0001");
        when(rs.getBigDecimal("balance")).thenReturn(BigDecimal.TEN);
        when(rs.getLong("user_id")).thenReturn(99L);

        Account acc = dao.get(1);

        assertNotNull(acc);
        assertEquals(1L, acc.getId());
        assertEquals("12345-6", acc.getAccountNumber());
    }

    @Test
    void getDeveRetornarNullQuandoNaoEncontrar() throws Exception {
        when(jdbc.getConexao()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(false);

        Account acc = dao.get(1);

        assertNull(acc);
    }

    @Test
    void deveRetornarListaDeAccounts() throws Exception {
        when(jdbc.getConexao()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, false);
        when(rs.getLong("id")).thenReturn(1L, 2L);
        when(rs.getString("account_number")).thenReturn("11111-1", "22222-2");
        when(rs.getString("agency")).thenReturn("0001", "0002");
        when(rs.getBigDecimal("balance")).thenReturn(BigDecimal.ONE, BigDecimal.TEN);
        when(rs.getLong("user_id")).thenReturn(10L, 20L);

        ArrayList<Account> lista = dao.getAll();

        assertEquals(2, lista.size());
        assertEquals(1L, lista.get(0).getId());
        assertEquals(2L, lista.get(1).getId());
    }

    @Test
    void getByUserIdComConnectionDeveRetornarConta() throws Exception {
        Long userId = 99L;

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getLong("id")).thenReturn(500L);
        when(rs.getString("account_number")).thenReturn("55555-5");
        when(rs.getString("agency")).thenReturn("0099");
        when(rs.getBigDecimal("balance")).thenReturn(BigDecimal.valueOf(150));
        when(rs.getLong("user_id")).thenReturn(userId);

        Account acc = dao.getByUserId(userId, conn);

        assertNotNull(acc);
        assertEquals(500L, acc.getId());
        verify(stmt).setLong(1, userId);
    }

    @Test
    void getByUserIdComConnectionDeveRetornarNullQuandoNaoExiste() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Account acc = dao.getByUserId(55L, conn);

        assertNull(acc);
    }

    @Test
    void getByUserIdDeveChamarInternamenteOutraVersao() throws Exception {
        when(jdbc.getConexao()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getLong("id")).thenReturn(80L);
        when(rs.getString("account_number")).thenReturn("88000-1");
        when(rs.getString("agency")).thenReturn("0008");
        when(rs.getBigDecimal("balance")).thenReturn(BigDecimal.valueOf(123));
        when(rs.getLong("user_id")).thenReturn(700L);

        Account acc = dao.getByUserId(700L);

        assertNotNull(acc);
        assertEquals(80L, acc.getId());
    }

    @Test
    void deveInserirConta() throws Exception {
        Account a = new Account(1L, "123", "001", BigDecimal.TEN, 55L);

        when(jdbc.getConexao()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);

        dao.insert(a);

        verify(stmt).setString(1, "123");
        verify(stmt).setString(2, "001");
        verify(stmt).setBigDecimal(3, BigDecimal.TEN);
        verify(stmt).setLong(4, 55L);
        verify(stmt).executeUpdate();
    }

    @Test
    void updateComConnectionDeveAtualizarConta() throws Exception {
        Account a = new Account(10L, "99999-9", "0099", BigDecimal.ONE, 44L);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);

        dao.update(a, conn);

        verify(stmt).setString(1, "99999-9");
        verify(stmt).setString(2, "0099");
        verify(stmt).setBigDecimal(3, BigDecimal.ONE);
        verify(stmt).setLong(4, 44L);
        verify(stmt).setLong(5, 10L);
        verify(stmt).executeUpdate();
    }
}


