package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.*;
import com.mycompany.webapplication.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebServlet(name = "Transferir", urlPatterns = {"/Transferir"})
public class Transferir extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String emailDestino = request.getParameter("destino");
        BigDecimal valor = new BigDecimal(request.getParameter("valor"));

        HttpSession session = request.getSession();
        Users remetente = (Users) session.getAttribute("usuario");

        if (remetente == null || emailDestino == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            request.setAttribute("mensagem", "Dados inválidos.");
            request.getRequestDispatcher("/views/transferencia.jsp").forward(request, response);
            return;
        }

        AccountDAO accountDAO = new AccountDAO();
        UserDAO userDAO = new UserDAO();
        AccountTransactionalDAO transDAO = new AccountTransactionalDAO();

        Account contaRemetente = accountDAO.getByUserId(remetente.getId());
        Users destinatario = userDAO.getByEmail(emailDestino);

        if (destinatario == null) {
            request.setAttribute("mensagem", "Usuário destinatário não encontrado.");
            request.getRequestDispatcher("/views/transferencia.jsp").forward(request, response);
            return;
        }

        Account contaDestinatario = accountDAO.getByUserId(destinatario.getId());

        if (contaRemetente.getBalance().compareTo(valor) < 0) {
            request.setAttribute("mensagem", "Saldo insuficiente para transferência.");
            request.getRequestDispatcher("/views/transferencia.jsp").forward(request, response);
            return;
        }

        // Atualiza saldos
        contaRemetente.setBalance(contaRemetente.getBalance().subtract(valor));
        contaDestinatario.setBalance(contaDestinatario.getBalance().add(valor));

        accountDAO.update(contaRemetente);
        accountDAO.update(contaDestinatario);

        // Registra transações
        AccountTransactional out = new AccountTransactional(TransactionType.TRAN    SFER_OUT, valor, LocalDateTime.now(),
                "Transferência para: " + emailDestino, contaRemetente);
        AccountTransactional in = new AccountTransactional(TransactionType.TRANSFER_IN, valor, LocalDateTime.now(),
                "Recebido de: " + remetente.getEmail(), contaDestinatario);

        transDAO.insert(out);
        transDAO.insert(in);

        request.setAttribute("mensagem", "Transferência realizada com sucesso!");
        request.setAttribute("usuario", remetente);
        request.setAttribute("conta", accountDAO.getByUserId(remetente.getId()));
        request.getRequestDispatcher("/views/transferencia.jsp").forward(request, response);
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

        request.getRequestDispatcher("/views/transferencia.jsp").forward(request, response);
    }
}
