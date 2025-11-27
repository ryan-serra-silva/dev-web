package com.mycompany.webapplication.model;
import com.mycompany.webapplication.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersTest {

    @Mock
    JDBC jdbc;

    @Mock
    Connection connection;

    @Mock
    PreparedStatement stmt;

    @Mock
    ResultSet rs;

    @InjectMocks
    UserDAO dao;

    @BeforeEach
    void setup() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
    }

    // --------------------------
    //      TEST get(int id)
    // --------------------------
    @Test
    void get_deveRetornarUsuarioQuandoExiste() throws Exception {
        when(connection.prepareStatement("SELECT * FROM Users WHERE id = ?")).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Ryan");
        when(rs.getString("email")).thenReturn("ryan@test.com");
        when(rs.getString("password_user")).thenReturn("123");

        Users u = dao.get(1);

        assertNotNull(u);
        assertEquals(1L, u.getId());
        assertEquals("Ryan", u.getName());
        assertEquals("ryan@test.com", u.getEmail());
    }

    @Test
    void get_deveRetornarNullQuandoNaoExiste() throws Exception {
        when(connection.prepareStatement("SELECT * FROM Users WHERE id = ?")).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Users u = dao.get(99);

        assertNull(u);
    }

    // --------------------------
    //       TEST getAll()
    // --------------------------
    @Test
    void getAll_deveRetornarListaDeUsuarios() throws Exception {
        when(connection.prepareStatement("SELECT * FROM Users")).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, false);
        when(rs.getLong("id")).thenReturn(1L, 2L);
        when(rs.getString("name")).thenReturn("A", "B");
        when(rs.getString("email")).thenReturn("a@test.com", "b@test.com");
        when(rs.getString("password_user")).thenReturn("p1", "p2");

        var lista = dao.getAll();

        assertEquals(2, lista.size());
        assertEquals("A", lista.get(0).getName());
        assertEquals("B", lista.get(1).getName());
    }

    // --------------------------
    //     TEST insert()
    // --------------------------
    @Test
    void insert_deveExecutarInsert() throws Exception {
        Users u = new Users(1L, "Ryan", "r@test.com", "123");

        when(connection.prepareStatement(
                "INSERT INTO Users (name, email, password_user) VALUES (?, ?, ?)")).thenReturn(stmt);

        dao.insert(u);

        verify(stmt).setString(1, "Ryan");
        verify(stmt).setString(2, "r@test.com");
        verify(stmt).setString(3, "123");
        verify(stmt).executeUpdate();
    }

    // --------------------------
    //      TEST update()
    // --------------------------
    @Test
    void update_deveExecutarUpdate() throws Exception {
        Users u = new Users(10L, "Novo", "novo@test.com", "abc");

        when(connection.prepareStatement(
                "UPDATE Users SET name = ?, email = ?, password_user = ? WHERE id = ?")).thenReturn(stmt);

        dao.update(u);

        verify(stmt).setString(1, "Novo");
        verify(stmt).setString(2, "novo@test.com");
        verify(stmt).setString(3, "abc");
        verify(stmt).setLong(4, 10L);
        verify(stmt).executeUpdate();
    }

    // --------------------------
    //     TEST delete()
    // --------------------------
    @Test
    void delete_deveExecutarDelete() throws Exception {
        when(connection.prepareStatement("DELETE FROM Users WHERE id = ?")).thenReturn(stmt);

        dao.delete(5);

        verify(stmt).setInt(1, 5);
        verify(stmt).executeUpdate();
    }

    // --------------------------
    //      TEST login()
    // --------------------------
    @Test
    void login_retornaUsuarioSeCredenciaisCorretas() throws Exception {

        when(connection.prepareStatement(
                "SELECT * FROM Users WHERE email = ? AND password_user = ? LIMIT 1"))
                .thenReturn(stmt);

        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Ryan");
        when(rs.getString("email")).thenReturn("r@test.com");
        when(rs.getString("password_user")).thenReturn("123");

        Users u = dao.login("r@test.com", "123");

        assertNotNull(u);
        assertEquals("Ryan", u.getName());
    }

    @Test
    void login_retornaNullSeNaoEncontrado() throws Exception {

        when(connection.prepareStatement(
                "SELECT * FROM Users WHERE email = ? AND password_user = ? LIMIT 1"))
                .thenReturn(stmt);

        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Users u = dao.login("x@test.com", "999");

        assertNull(u);
    }

    // --------------------------
    //   TEST getByEmail()
    // --------------------------
    @Test
    void getByEmail_deveRetornarUsuario() throws Exception {
        when(connection.prepareStatement("SELECT * FROM users WHERE email = ?"))
                .thenReturn(stmt);

        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getLong("id")).thenReturn(77L);
        when(rs.getString("name")).thenReturn("Teste");
        when(rs.getString("email")).thenReturn("a@test.com");
        when(rs.getString("password_user")).thenReturn("senha");

        Users u = dao.getByEmail("a@test.com");

        assertNotNull(u);
        assertEquals(77L, u.getId());
    }

    @Test
    void getByEmail_nullQuandoNaoExiste() throws Exception {
        when(connection.prepareStatement("SELECT * FROM users WHERE email = ?"))
                .thenReturn(stmt);

        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Users u = dao.getByEmail("naoexiste@test.com");

        assertNull(u);
    }

    // --------------------------
    //   TEST updatePasswordByEmail()
    // --------------------------
    @Test
    void updatePasswordByEmail_funciona() throws Exception {

        when(connection.prepareStatement(
                "UPDATE Users SET password_user = ? WHERE email = ?"))
                .thenReturn(stmt);

        dao.updatePasswordByEmail("a@test.com", "nova");

        verify(stmt).setString(1, "nova");
        verify(stmt).setString(2, "a@test.com");
        verify(stmt).executeUpdate();
    }
}
