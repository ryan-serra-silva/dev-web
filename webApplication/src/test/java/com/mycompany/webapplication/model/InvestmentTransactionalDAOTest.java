package com.mycompany.webapplication.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mycompany.webapplication.entity.InvestmentProduct;
import com.mycompany.webapplication.entity.InvestmentType;

@ExtendWith(MockitoExtension.class)
public class InvestmentTransactionalDAOTest {

    @Mock
    private JDBC jdbc;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement stmt;

    @Mock
    private ResultSet rs;

    @InjectMocks
    private InvestmentTransactionalDAO dao;

    private InvestmentProduct product;

    @BeforeEach
    void setup() {
        product = new InvestmentProduct();
        product.setId(1L);
        product.setTypeInvestment(InvestmentType.CDB);
        product.setReturnRate(BigDecimal.valueOf(1.5));
    }

    @Test
    void get_deveRetornarProdutoQuandoEncontrado() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("type_investment")).thenReturn("CDB");
        when(rs.getBigDecimal("return_rate")).thenReturn(BigDecimal.valueOf(1.5));

        InvestmentProduct result = dao.get(1);

        assertNotNull(result);   
        assertEquals(1L, result.getId());
        assertEquals(InvestmentType.CDB, result.getTypeInvestment());
        assertEquals(BigDecimal.valueOf(1.5), result.getReturnRate());

    }

    @Test
    void getByType_deveRetornarProdutoDoTipo() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("type_investment")).thenReturn("CDB");
        when(rs.getBigDecimal("return_rate")).thenReturn(BigDecimal.valueOf(1.5));

        InvestmentProduct result = dao.getByType(InvestmentType.CDB);

        assertNotNull(result);
        verify(stmt).setString(1, "CDB");

        assertEquals(1L, result.getId());
        assertEquals(InvestmentType.CDB, result.getTypeInvestment());
        assertEquals(BigDecimal.valueOf(1.5), result.getReturnRate());
    }


   @Test
void getAll_deveRetornarListaCompleta() throws Exception {
    when(jdbc.getConexao()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(stmt);
    when(stmt.executeQuery()).thenReturn(rs);

    when(rs.next()).thenReturn(true, false);
    when(rs.getLong("id")).thenReturn(1L);
    when(rs.getString("type_investment")).thenReturn("CDB");
    when(rs.getBigDecimal("return_rate")).thenReturn(BigDecimal.valueOf(1.5));

    ArrayList<InvestmentProduct> list = dao.getAll();

    assertEquals(1, list.size());
    assertEquals(1L, list.get(0).getId());
    assertEquals(InvestmentType.CDB, list.get(0).getTypeInvestment());
    assertEquals(BigDecimal.valueOf(1.5), list.get(0).getReturnRate());
}

    @Test
    void insert_deveInserirRegistro() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);

        dao.insert(product);

        verify(stmt).setString(1, "CDB");
        verify(stmt).setBigDecimal(2, BigDecimal.valueOf(1.5));
        verify(stmt).executeUpdate();
    }

    @Test
    void update_deveAtualizarRegistro() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);

        dao.update(product);

        verify(stmt).setString(1, "CDB");
        verify(stmt).setBigDecimal(2, BigDecimal.valueOf(1.5));
        verify(stmt).setLong(3, 1L);
        verify(stmt).executeUpdate();
    }

    @Test
    void delete_deveRemoverPorId() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);

        dao.delete(1);

        verify(stmt).setInt(1, 1);
        verify(stmt).executeUpdate();
    }
}
