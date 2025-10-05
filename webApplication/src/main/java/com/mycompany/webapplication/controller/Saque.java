package com.mycompany.webapplication.controller;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.AccountTransactional;
import com.mycompany.webapplication.entity.TransactionType;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.AccountTransactionalDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "Sacar", urlPatterns = {"/Sacar"})
public class Saque extends HttpServlet {


@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    try {
        BigDecimal valor = new BigDecimal(request.getParameter("valor"));

        HttpSession session = request.getSession();
        Users usuario = (Users) session.getAttribute("usuario");
        Long userId = usuario.getId();

        AccountDAO accountDAO = new AccountDAO();
        Account conta = accountDAO.getByUserId(userId);

        // Horários bloqueados
        LocalTime agora = LocalTime.now();
        LocalTime bloqueioInicio1 = LocalTime.of(12, 0);  // 12:00
        LocalTime bloqueioFim1    = LocalTime.of(12, 30); // 12:30
        LocalTime bloqueioInicio2 = LocalTime.of(18, 0);  // 18:00
        LocalTime bloqueioFim2    = LocalTime.of(18, 30); // 18:30

        if (conta == null) {
            request.setAttribute("mensagem", "Erro: conta inválida ou inativa.");
        }
        // Bloqueio horário 1
        else if (!agora.isBefore(bloqueioInicio1) && !agora.isAfter(bloqueioFim1)) {
            request.setAttribute("mensagem", "Saque não permitido entre 12:00 e 12:30.");
        }
        // Bloqueio horário 2
        else if (!agora.isBefore(bloqueioInicio2) && !agora.isAfter(bloqueioFim2)) {
            request.setAttribute("mensagem", "Saque não permitido entre 18:00 e 18:30.");
        }
        else if (valor.compareTo(new BigDecimal("10")) < 0) {
            request.setAttribute("mensagem", "Erro: valor menor que o saque mínimo.");
        }
        else if (valor.compareTo(new BigDecimal("2000")) > 0) {
            request.setAttribute("mensagem", "Erro: valor maior que o saque máximo.");
        }
        else if (valor.remainder(new BigDecimal("10")).compareTo(BigDecimal.ZERO) != 0) {
            request.setAttribute("mensagem", "Erro: valor deve ser múltiplo de 10.");
        }
        else if (conta.getBalance().compareTo(valor) >= 0) {
            BigDecimal novoSaldo = conta.getBalance().subtract(valor);
            conta.setBalance(novoSaldo);
            accountDAO.update(conta);

            // Registro da transação
            AccountTransactional transacao = new AccountTransactional();
            transacao.setTypeTransaction(TransactionType.WITHDRAW);
            transacao.setAmount(valor);
            transacao.setTimestamp(LocalDateTime.now());
            transacao.setDescription("Saque realizado");
            transacao.setAccount(conta);

            AccountTransactionalDAO transacaoDAO = new AccountTransactionalDAO();
            transacaoDAO.insert(transacao);

            // Alerta de saldo crítico
            if (novoSaldo.compareTo(new BigDecimal("100")) < 0) {
                request.setAttribute("alerta", "Atenção: saldo baixo!");
            }

            conta = accountDAO.getByUserId(userId); // novo saldo
            request.setAttribute("mensagem", "Saque realizado com sucesso!");
            request.setAttribute("usuario", usuario);
            request.setAttribute("conta", conta);
        } else {
            request.setAttribute("mensagem", "Erro: saldo insuficiente.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("mensagem", "Erro no processamento do saque.");
    }

    request.getRequestDispatcher("/views/saque.jsp").forward(request, response);
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

        request.getRequestDispatcher("/views/saque.jsp").forward(request, response);
    }
}
