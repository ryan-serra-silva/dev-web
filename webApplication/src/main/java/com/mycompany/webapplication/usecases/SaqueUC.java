package com.mycompany.webapplication.usecases;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.model.AccountDAO;

import java.math.BigDecimal;
import java.time.LocalTime;

public class SaqueUC {
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
