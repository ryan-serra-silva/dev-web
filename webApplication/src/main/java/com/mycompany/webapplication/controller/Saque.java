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
        String mensagemValidacao = validarSaque(userId, valor, LocalTime.now(), accountDAO);

        if (mensagemValidacao != null) {
            request.setAttribute("mensagem", mensagemValidacao);
        } else {
            Account conta = accountDAO.getByUserId(userId);
            BigDecimal novoSaldo = conta.getBalance().subtract(valor);
            conta.setBalance(novoSaldo);
            accountDAO.update(conta);

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

    protected String validarSaque(Long userId, BigDecimal valor, LocalTime agora, AccountDAO accountDAO) {
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
