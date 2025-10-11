package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransferirTest {

    private Method validarEntrada; 
    private Users usuarioLogado;   

    @BeforeEach
    void setup() throws Exception {
        validarEntrada = Transferir.class.getDeclaredMethod(
                "validarEntrada", Users.class, String.class, String.class);
        validarEntrada.setAccessible(true);

        usuarioLogado = mock(Users.class);
        when(usuarioLogado.getEmail()).thenReturn("alice@bank.com");
        when(usuarioLogado.getId()).thenReturn(1);
        when(usuarioLogado.getName()).thenReturn("Alice");
    }

    private String chama(Users u, String destino, String valor) {
        try {
            return (String) validarEntrada.invoke(null, u, destino, valor);
        } catch (Exception e) {
            fail("Erro ao invocar validarEntrada: " + e.getMessage());
            return null;
        }
    }

    @Test @DisplayName("D1 - sessão expirada")
    void d1_sessaoExpirada() {
        String msg = chama(null, "bob@bank.com", "10.00");
        assertEquals("Sessão expirada. Faça login novamente.", msg);
    }

    @Test @DisplayName("D2 - e-mail vazio")
    void d2_emailVazio() {
        String msg = chama(usuarioLogado, "   ", "10.00");
        assertEquals("Informe o e-mail do destinatário.", msg);
    }

    @Test @DisplayName("D3 - e-mail inválido")
    void d3_emailInvalido() {
        String msg = chama(usuarioLogado, "bob@invalid", "10.00");
        assertEquals("E-mail do destinatário inválido.", msg);
    }

    @Test @DisplayName("D4 - domínio bloqueado")
    void d4_dominioBloqueado() {
        String msg = chama(usuarioLogado, "joe@example.com", "10.00");
        assertTrue(msg.startsWith("Transferências para o domínio \"example.com\""));
    }

    @Test @DisplayName("D5 - valor ausente")
    void d5_valorAusente() {
        String msg = chama(usuarioLogado, "bob@bank.com", null);
        assertEquals("Informe um valor numérico válido.", msg);
    }

    @Test @DisplayName("D6 - valor <= 0")
    void d6_valorMenorOuIgualZero() {
        String msg = chama(usuarioLogado, "bob@bank.com", "0");
        assertEquals("Informe um valor maior que zero.", msg);
    }

    @Test @DisplayName("D7 - mais de 2 casas")
    void d7_maisDeDuasCasas() {
        String msg = chama(usuarioLogado, "bob@bank.com", "10.375");
        assertEquals("Use no máximo duas casas decimais.", msg);
    }

    @Test @DisplayName("D8 - mesma conta")
    void d8_mesmaConta() {
        String msg = chama(usuarioLogado, "alice@bank.com", "10.00");
        assertEquals("Não é possível transferir para a própria conta.", msg);
    }

    @Test @DisplayName("D9 - abaixo do mínimo")
    void d9_abaixoMinimo() {
        String msg = chama(usuarioLogado, "bob@bank.com", "0.001");
        assertTrue(msg.contains("Valor mínimo por transferência é R$"));
    }

    @Test @DisplayName("D10 - acima do máximo")
    void d10_acimaMaximo() {
        String msg = chama(usuarioLogado, "bob@bank.com", "1000000.00");
        assertTrue(msg.contains("Valor máximo por transferência é R$"));
    }

    @Test @DisplayName("OK - entradas válidas retornam null (segue fluxo)")
    void ok_caminnhoFelizDeEntrada() {
        String msg = chama(usuarioLogado, "bob@bank.com", "250.00");
        assertNull(msg, "Quando a validação de entrada passa, retorna null");
    }
}
