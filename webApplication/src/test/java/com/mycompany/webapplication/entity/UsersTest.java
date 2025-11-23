package com.mycompany.webapplication.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsersTest {
    @Test
    void testEmptyConstructorAndSetters() {
        Users user = new Users();

        user.setId(1L);
        user.setName("Daniel");
        user.setEmail("daniel@test.com");
        user.setPassword("123456");

        assertEquals(1L, user.getId());
        assertEquals("Daniel", user.getName());
        assertEquals("daniel@test.com", user.getEmail());
        assertEquals("123456", user.getPassword());
    }

    @Test
    void testFullConstructor() {
        Users user = new Users(
                10L,
                "Maria",
                "maria@test.com",
                "senha123"
        );

        assertEquals(10L, user.getId());
        assertEquals("Maria", user.getName());
        assertEquals("maria@test.com", user.getEmail());
        assertEquals("senha123", user.getPassword());
    }

    @Test
    void testConstructorEmailAndPassword() {
        Users user = new Users(
                "user@test.com",
                "pass123"
        );

        assertNull(user.getId());
        assertNull(user.getName());
        assertEquals("user@test.com", user.getEmail());
        assertEquals("pass123", user.getPassword());
    }

    @Test
    void testConstructorNameEmailPassword() {
        Users user = new Users(
                "João",
                "joao@test.com",
                "abc123"
        );

        assertNull(user.getId());
        assertEquals("João", user.getName());
        assertEquals("joao@test.com", user.getEmail());
        assertEquals("abc123", user.getPassword());
    }

    @Test
    void testToString() {
        Users user = new Users(
                5L,
                "Ana",
                "ana@test.com",
                "pwd456"
        );

        String result = user.toString();

        assertTrue(result.contains("id=5"));
        assertTrue(result.contains("name=Ana"));
        assertTrue(result.contains("email=ana@test.com"));
        assertTrue(result.contains("password=pwd456"));
    }
}
