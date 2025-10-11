package com.mycompany.webapplication.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.AccountTransactional;
import com.mycompany.webapplication.entity.TransactionType;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.AccountTransactionalDAO;

import jakarta.servlet.http.HttpServletRequest;

public class AccountService {

    public static String processarDeposito(Long userId, BigDecimal valor, AccountDAO accountDAO, HttpServletRequest request, LocalTime agora) {
        Account conta = accountDAO.getByUserId(userId);

        if (conta == null) return "Erro: conta inválida ou inativa.";

        BigDecimal depositoMinimo = new BigDecimal("10");
        BigDecimal depositoMaximo = new BigDecimal("5000");


        LocalTime[] bloqueioInicios = { LocalTime.of(12, 0), LocalTime.of(18, 0) };
        LocalTime[] bloqueioFins    = { LocalTime.of(12, 30), LocalTime.of(18, 30) };

        // Checagem de horários bloqueados
        for (int i = 0; i < bloqueioInicios.length; i++) {
            if (!agora.isBefore(bloqueioInicios[i]) && !agora.isAfter(bloqueioFins[i])) {
                return "Depósitos não permitidos entre "
                        + bloqueioInicios[i] + " e " + bloqueioFins[i];
            }
        }

        // Validações de valor
        if (valor.compareTo(depositoMinimo) < 0) return "Erro: valor menor que o depósito mínimo de R$10.";
        if (valor.compareTo(depositoMaximo) > 0) return "Erro: valor maior que o depósito máximo de R$5000.";

        // Validação de valores especiais
        long valorInt = valor.longValue();
        if (valorInt % 2 == 1) return "Erro: valor não pode ser impar!";
        if (valorInt % 7 == 0) return "Erro: valor não pode ser múltiplo de 7!";
        if (valorInt % 11 == 0) return "Erro: valor não pode ser múltiplo de 11!";

        // Processamento do depósito
        BigDecimal novoSaldo = conta.getBalance().add(valor);
        conta.setBalance(novoSaldo);
        accountDAO.update(conta);

        AccountTransactional transacao = new AccountTransactional();
        transacao.setTypeTransaction(TransactionType.DEPOSIT);
        transacao.setAmount(valor);
        transacao.setTimestamp(LocalDateTime.now());
        transacao.setDescription("Depósito realizado");
        transacao.setAccount(conta);

        AccountTransactionalDAO transacaoDAO = new AccountTransactionalDAO();
        transacaoDAO.insert(transacao);

        // Alertas baseados no saldo
        if (novoSaldo.compareTo(new BigDecimal("10000")) > 0) {
            request.setAttribute("alerta", "Atenção: saldo acima de R$10.000!");
        }

        return "Depósito realizado com sucesso!";
    }

    public static String validarSaque(Long userId, BigDecimal valor, LocalTime agora, AccountDAO accountDAO) {
        LocalTime bloqueioInicio1 = LocalTime.of(12, 0);
        LocalTime bloqueioFim1    = LocalTime.of(12, 30);
        LocalTime bloqueioInicio2 = LocalTime.of(18, 0);
        LocalTime bloqueioFim2    = LocalTime.of(18, 30);
        Account conta = accountDAO.getByUserId(userId);

        if (conta == null) {
            return "Erro: conta inválida ou inativa.";
        }

        if (!agora.isBefore(bloqueioInicio1) && !agora.isAfter(bloqueioFim1)) {
            return "Saque não permitido entre 12:00 e 12:30.";
        }

        if (!agora.isBefore(bloqueioInicio2) && !agora.isAfter(bloqueioFim2)) {
            return "Saque não permitido entre 18:00 e 18:30.";
        }

        if (valor.compareTo(new BigDecimal("10")) < 0) {
            return "Erro: valor menor que o saque mínimo.";
        }

        if (valor.compareTo(new BigDecimal("2000")) > 0) {
            return "Erro: valor maior que o saque máximo.";
        }

        if (valor.remainder(new BigDecimal("10")).compareTo(BigDecimal.ZERO) != 0) {
            return "Erro: valor deve ser múltiplo de 10.";
        }

        if (conta.getBalance().compareTo(valor) < 0) {
            return "Erro: saldo insuficiente.";
        }

        return null;
    }
}
