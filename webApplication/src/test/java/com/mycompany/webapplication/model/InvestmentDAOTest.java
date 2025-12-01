package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Investment;
import com.mycompany.webapplication.entity.InvestmentProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvestmentDAOTest {

    @Mock
    private JDBC jdbc;

    @Mock
    private AccountDAO accountDAO;

    @Mock
    private InvestmentTransactionalDAO investmentProductDAO;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private InvestmentDAO investmentDAO;

    private Investment inv;
    private Account acc;
    private InvestmentProduct prod;

    @BeforeEach
    void setup() {
        acc = new Account();
        acc.setId(10L);

        prod = new InvestmentProduct();
        prod.setId(20L);

        inv = new Investment();
        inv.setId(1L);
        inv.setAmount(BigDecimal.valueOf(500));
        inv.setStartDate(LocalDate.of(2024, 1, 1));
        inv.setEndDate(LocalDate.of(2024, 12, 31));
        inv.setAccount(acc);
        inv.setInvestmentProduct(prod);
    }


    @Test
    void get_deveRetornarInvestmentQuandoEncontrado() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true);

        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(BigDecimal.valueOf(500));
        when(resultSet.getDate("start_date")).thenReturn(Date.valueOf(inv.getStartDate()));
        when(resultSet.getDate("end_date")).thenReturn(Date.valueOf(inv.getEndDate()));
        when(resultSet.getLong("account_id")).thenReturn(10L);
        when(resultSet.getLong("investment_product_id")).thenReturn(20L);

        when(accountDAO.get(10)).thenReturn(acc);
        when(investmentProductDAO.get(20)).thenReturn(prod);

        Investment result = investmentDAO.get(1);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BigDecimal.valueOf(500), result.getAmount());
        verify(preparedStatement).setInt(1, 1);
    }

    @Test
    void getAll_deveRetornarListaDeInvestments() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true, false);

        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(BigDecimal.valueOf(500));
        when(resultSet.getDate("start_date")).thenReturn(Date.valueOf(inv.getStartDate()));
        when(resultSet.getDate("end_date")).thenReturn(Date.valueOf(inv.getEndDate()));
        when(resultSet.getLong("account_id")).thenReturn(10L);
        when(resultSet.getLong("investment_product_id")).thenReturn(20L);

        when(accountDAO.get(10)).thenReturn(acc);
        when(investmentProductDAO.get(20)).thenReturn(prod);

        List<Investment> list = investmentDAO.getAll();

        assertEquals(1, list.size());
    }
    @Test
    void get_deveMapearDatasCorretamente() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true);

        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(BigDecimal.valueOf(500));

        Date sd = Date.valueOf(LocalDate.of(2024,1,1));
        Date ed = Date.valueOf(LocalDate.of(2024,12,31));

        when(resultSet.getDate("start_date")).thenReturn(sd);
        when(resultSet.getDate("end_date")).thenReturn(ed);

        when(resultSet.getLong("account_id")).thenReturn(10L);
        when(resultSet.getLong("investment_product_id")).thenReturn(20L);

        when(accountDAO.get(10)).thenReturn(acc);
        when(investmentProductDAO.get(20)).thenReturn(prod);

        Investment result = investmentDAO.get(1);

        assertNotNull(result);
        assertEquals(LocalDate.of(2024,1,1), result.getStartDate());
        assertEquals(LocalDate.of(2024,12,31), result.getEndDate());
    }
    @Test
    void get_deveIgnorarDatasQuandoNulas() throws Exception {
    when(jdbc.getConexao()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);

    when(resultSet.next()).thenReturn(true);

    when(resultSet.getLong("id")).thenReturn(1L);
    when(resultSet.getBigDecimal("amount")).thenReturn(BigDecimal.valueOf(500));

    when(resultSet.getDate("start_date")).thenReturn(null);
    when(resultSet.getDate("end_date")).thenReturn(null);

    when(resultSet.getLong("account_id")).thenReturn(10L);
    when(resultSet.getLong("investment_product_id")).thenReturn(20L);

    when(accountDAO.get(10)).thenReturn(acc);
    when(investmentProductDAO.get(20)).thenReturn(prod);

    Investment result = investmentDAO.get(1);

    assertNull(result.getStartDate());
    assertNull(result.getEndDate());
}
    @Test
void get_deveMapearAccountEProductCorretamente() throws Exception {
    when(jdbc.getConexao()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);

    when(resultSet.next()).thenReturn(true);

    when(resultSet.getLong("id")).thenReturn(1L);
    when(resultSet.getBigDecimal("amount")).thenReturn(BigDecimal.valueOf(500));
    when(resultSet.getDate("start_date")).thenReturn(Date.valueOf(inv.getStartDate()));
    when(resultSet.getDate("end_date")).thenReturn(Date.valueOf(inv.getEndDate()));

    when(resultSet.getLong("account_id")).thenReturn(10L);
    when(resultSet.getLong("investment_product_id")).thenReturn(20L);

    when(accountDAO.get(10)).thenReturn(acc);
    when(investmentProductDAO.get(20)).thenReturn(prod);

    Investment result = investmentDAO.get(1);

    assertSame(acc, result.getAccount());
    assertSame(prod, result.getInvestmentProduct());
}


    @Test
    void getAllByAccountId_deveRetornarListaFiltrada() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);

        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(BigDecimal.valueOf(500));
        when(resultSet.getDate("start_date")).thenReturn(Date.valueOf(inv.getStartDate()));
        when(resultSet.getDate("end_date")).thenReturn(Date.valueOf(inv.getEndDate()));
        when(resultSet.getLong("account_id")).thenReturn(10L);
        when(resultSet.getLong("investment_product_id")).thenReturn(20L);

        when(accountDAO.get(10)).thenReturn(acc);
        when(investmentProductDAO.get(20)).thenReturn(prod);

        List<Investment> list = investmentDAO.getAllByAccountId(10L);

        assertEquals(1, list.size());
        verify(preparedStatement).setLong(1, 10L);
    }


    @Test
    void insert_deveExecutarInsertComSucesso() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        investmentDAO.insert(inv);

        verify(preparedStatement).setBigDecimal(1, inv.getAmount());
        verify(preparedStatement).setDate(2, Date.valueOf(inv.getStartDate()));
        verify(preparedStatement).setDate(3, Date.valueOf(inv.getEndDate()));
        verify(preparedStatement).setLong(4, 10L);
        verify(preparedStatement).setLong(5, 20L);

        verify(preparedStatement).executeUpdate();
    }


    @Test
    void update_deveAtualizarDados() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        investmentDAO.update(inv);

        verify(preparedStatement).setBigDecimal(1, inv.getAmount());
        verify(preparedStatement).setDate(2, Date.valueOf(inv.getStartDate()));
        verify(preparedStatement).setDate(3, Date.valueOf(inv.getEndDate()));
        verify(preparedStatement).setLong(4, 10L);
        verify(preparedStatement).setLong(5, 20L);
        verify(preparedStatement).setLong(6, inv.getId());

        verify(preparedStatement).executeUpdate();
    }


    @Test
    void delete_deveRemoverPorId() throws Exception {
        when(jdbc.getConexao()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        investmentDAO.delete(1);

        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).executeUpdate();
    }
}
