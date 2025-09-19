package com.mycompany.webapplication.controller;
import java.time.LocalTime;
import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.AccountTransactional;
import com.mycompany.webapplication.entity.TransactionType;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.AccountTransactionalDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebServlet(name = "Depositar", urlPatterns = {"/Depositar"})
public class Depositar extends HttpServlet {

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

        BigDecimal depositoMinimo = new BigDecimal("10");
        BigDecimal depositoMaximo = new BigDecimal("5000");

        // Horários bloqueados
        LocalTime agora = LocalTime.now();
        LocalTime bloqueioInicio1 = LocalTime.of(12, 0);  // 12:00
        LocalTime bloqueioFim1    = LocalTime.of(12, 30); // 12:30
        LocalTime bloqueioInicio2 = LocalTime.of(17, 0);  // 18:00
        LocalTime bloqueioFim2    = LocalTime.of(18, 30); // 18:30

        if (conta == null) {
            request.setAttribute("mensagem", "Erro: conta inválida ou inativa.");
        } 
        else if (!agora.isBefore(bloqueioInicio1) && !agora.isAfter(bloqueioFim1)) {
            request.setAttribute("mensagem", "Depósitos não permitidos entre 12:00 e 12:30.");
        }
        else if (!agora.isBefore(bloqueioInicio2) && !agora.isAfter(bloqueioFim2)) {
            request.setAttribute("mensagem", "Depósitos não permitidos entre 18:00 e 18:30.");
        }
        else if (valor.compareTo(depositoMinimo) < 0) {
            request.setAttribute("mensagem", "Erro: valor menor que o depósito mínimo de R$10.");
        }
        else if (valor.compareTo(depositoMaximo) > 0) {
            request.setAttribute("mensagem", "Erro: valor maior que o depósito máximo de R$5000.");
        }
        else {
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

            // Alerta saldo alto opcional
            if (novoSaldo.compareTo(new BigDecimal("10000")) > 0) {
                request.setAttribute("alerta", "Atenção: saldo acima de R$10.000!");
            }

            conta = accountDAO.getByUserId(userId); // atualiza saldo
            request.setAttribute("mensagem", "Depósito realizado com sucesso!");
            request.setAttribute("usuario", usuario);
            request.setAttribute("conta", conta);
        }

    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("mensagem", "Erro no processamento do depósito.");
    }

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
}
