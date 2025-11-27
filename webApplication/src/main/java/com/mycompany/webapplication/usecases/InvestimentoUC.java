package com.mycompany.webapplication.usecases;

import com.mycompany.webapplication.entity.*;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.InvestmentDAO;
import com.mycompany.webapplication.model.InvestmentProductDAO;
import com.mycompany.webapplication.model.JDBC;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;

public class InvestimentoUC {

    private final AccountDAO accountDAO;
    private final InvestmentDAO investmentDAO;
    private final InvestmentProductDAO productDAO;
    private final JDBC jdbc;

    public InvestimentoUC(AccountDAO accountDAO,
                          InvestmentDAO investmentDAO,
                          InvestmentProductDAO productDAO,
                          JDBC jdbc) {

        this.accountDAO = accountDAO;
        this.investmentDAO = investmentDAO;
        this.productDAO = productDAO;
        this.jdbc = jdbc;
    }

    public static String validar(Account conta, String tipo, BigDecimal valor, int tempoMeses) {

        if (conta == null) {
            return "Erro: Conta não encontrada.";
        }
        if (valor == null) {
            return "Erro: Valor inválido.";
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            return "Erro: Valor deve ser maior que zero.";
        }
        if (tipo == null || tipo.isBlank()) {
            return "Erro: Tipo de investimento deve ser selecionado.";
        }
        if (conta.getBalance().compareTo(valor) < 0) {
            return "Erro: Saldo insuficiente para realizar o investimento. " +
                    "Saldo disponível: R$ " + conta.getBalance();
        }
        if (tempoMeses <= 0) {
            return "Erro: O prazo do investimento deve ser maior que zero.";
        }

        return null;
    }

    // ------------------------------------------------------------------------

    public String executar(Users usuario, String tipo, BigDecimal valor, int tempoMeses) {

        try {
            Account conta = accountDAO.getByUserId(usuario.getId());

            String erro = validar(conta, tipo, valor, tempoMeses);
            if (erro != null) return erro;

            Connection conn = jdbc.getConexao();

            try {
                conn.setAutoCommit(false);

                // Atualiza saldo
                conta.setBalance(conta.getBalance().subtract(valor));
                accountDAO.update(conta, conn);

                // Busca produto
                InvestmentType tipoEnum = InvestmentType.valueOf(tipo.toUpperCase());
                InvestmentProduct produto = productDAO.getByType(tipoEnum);

                if (produto == null) {
                    throw new Exception("Produto de investimento não encontrado.");
                }

                // Cria investimento
                Investment inv = new Investment();
                inv.setAmount(valor);
                inv.setStartDate(LocalDate.now());
                inv.setEndDate(LocalDate.now().plusMonths(tempoMeses));
                inv.setAccount(conta);
                inv.setInvestmentProduct(produto);

                investmentDAO.insert(inv, conn);

                conn.commit();
                return null;

            } catch (Exception e) {
                conn.rollback();
                return "Erro ao processar o investimento.";
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro interno no caso de uso.";
        }
    }
}
