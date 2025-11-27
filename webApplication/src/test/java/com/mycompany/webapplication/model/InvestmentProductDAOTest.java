package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.InvestmentProduct;
import com.mycompany.webapplication.entity.InvestmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvestmentProductDAOTest {

    @Mock
    private JDBC jdbc;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement stmt;

    @Mock
    private ResultSet rs;

    @InjectMocks
    private InvestmentProductDAO dao;

    private InvestmentProduct product;

    @BeforeEach
    void setup() {
        product = new InvestmentProduct();
        product.setId(1L);
        product.setTypeInvestment(InvestmentType.CDB);
        product.setReturnRate(BigDecimal.valueOf(1.5));
    }


    @Test
    void get_deveRetornarInvestmentProductQuandoEncontrado() throws Exception {
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
    }

    @Test
    void getByType_deveRetornarProdutoPorTipo() throws Exception {
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
    }

    @Test
    void getAll_deveRetornarTodosInvestmentProducts() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("type_investment")).thenReturn("CDB");
        when(rs.getBigDecimal("return_rate")).thenReturn(BigDecimal.valueOf(1.5));

        ArrayList<InvestmentProduct> list = dao.getAll();

        assertEquals(1, list.size());
    }


    @Test
    void insert_deveInserirComSucesso() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);

        dao.insert(product);

        verify(stmt).setString(1, product.getTypeInvestment().name());
        verify(stmt).setBigDecimal(2, product.getReturnRate());
        verify(stmt).executeUpdate();
    }


    @Test
    void update_deveAtualizarComSucesso() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);

        dao.update(product);

        verify(stmt).setString(1, product.getTypeInvestment().name());
        verify(stmt).setBigDecimal(2, product.getReturnRate());
        verify(stmt).setLong(3, product.getId());
        verify(stmt).executeUpdate();
    }


    @Test
    void delete_deveExcluirPorId() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);

        dao.delete(1);

        verify(stmt).setInt(1, 1);
        verify(stmt).executeUpdate();
    }
}
