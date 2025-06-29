package com.mycompany.webapplication.controller;

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

            if (conta != null && valor.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal novoSaldo = conta.getBalance().add(valor);
                conta.setBalance(novoSaldo);
                accountDAO.update(conta);

                AccountTransactional transacao = new AccountTransactional();
                transacao.setTypeTransaction(TransactionType.DEPOSIT);
                transacao.setAmount(valor);
                transacao.setTimestamp(LocalDateTime.now());
                transacao.setDescription("Depósito realizado via web");
                transacao.setAccount(conta);

                AccountTransactionalDAO transacaoDAO = new AccountTransactionalDAO();
                transacaoDAO.insert(transacao);

                conta = accountDAO.getByUserId(userId); // Atualiza novamente para refletir saldo atualizado

                request.setAttribute("mensagem", "Depósito realizado com sucesso!");
                request.setAttribute("usuario", usuario);
                request.setAttribute("conta", conta);
            } else {
                request.setAttribute("mensagem", "Erro: valor inválido ou conta não encontrada.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensagem", "Erro no processamento do depósito.");
        }

        request.getRequestDispatcher("/views/deposito.jsp").forward(request, response);
    }
}
