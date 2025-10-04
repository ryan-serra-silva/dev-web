package com.mycompany.webapplication.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;

import com.mycompany.webapplication.entity.*;
import com.mycompany.webapplication.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "Depositar", urlPatterns = {"/Depositar"})
public class Depositar extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Users usuario = (Users) session.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        BigDecimal valor;
        try {
            valor = new BigDecimal(request.getParameter("valor"));
        } catch (NumberFormatException e) {
            request.setAttribute("mensagem", "Valor inválido!");
            request.getRequestDispatcher("/views/deposito.jsp").forward(request, response);
            return;
        }

        String resultado = processarDeposito(usuario.getId(), valor, request);

        request.setAttribute("mensagem", resultado);
        request.setAttribute("usuario", usuario);

        AccountDAO accountDAO = new AccountDAO();
        request.setAttribute("conta", accountDAO.getByUserId(usuario.getId()));

        request.getRequestDispatcher("/views/deposito.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Users usuario = (Users) session.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        AccountDAO accountDAO = new AccountDAO();
        Account conta = accountDAO.getByUserId(usuario.getId());

        request.setAttribute("usuario", usuario);
        request.setAttribute("conta", conta);

        request.getRequestDispatcher("/views/deposito.jsp").forward(request, response);
    }

    public String processarDeposito(Long userId, BigDecimal valor, HttpServletRequest request) {
        AccountDAO accountDAO = new AccountDAO();
        Account conta = accountDAO.getByUserId(userId);

        if (conta == null) return "Erro: conta inválida ou inativa.";

        BigDecimal depositoMinimo = new BigDecimal("10");
        BigDecimal depositoMaximo = new BigDecimal("5000");

        LocalTime agora = LocalTime.now();
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

}
