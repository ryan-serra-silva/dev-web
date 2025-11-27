package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.*;
import com.mycompany.webapplication.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.regex.Pattern;

import static com.mycompany.webapplication.usecases.TransferirUC.validateTransfer;

@WebServlet(name = "Transferir", urlPatterns = {"/Transferir"})
public class Transferir extends HttpServlet {

    private void forwardMsg(HttpServletRequest request, HttpServletResponse response, String msg)
            throws ServletException, IOException {
        request.setAttribute("mensagem", msg);
        request.getRequestDispatcher("/views/transferencia.jsp").forward(request, response);
    }


    // Método auxiliar para parse de valor
    private BigDecimal parseValor(String valorParam) {
        if (valorParam == null) return null;
        String norm = valorParam.replace(".", "").replace(",", ".").trim();
        try {
            return new BigDecimal(norm).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Users remetente = (Users) session.getAttribute("usuario");

        String emailDestinoRaw = request.getParameter("destino");
        String valorParam = request.getParameter("valor");

        // Validação inicial
        String errorMsg = validateTransfer(remetente, emailDestinoRaw, valorParam);
        if (errorMsg != null) {
            forwardMsg(request, response, errorMsg);
            return;
        }

        // Parse seguro do valor
        BigDecimal valor = parseValor(valorParam);
        String emailDestino = emailDestinoRaw.trim().toLowerCase();

        // DAOs
        AccountDAO accountDAO = new AccountDAO();
        JDBC jdbc = new JDBC();
        UserDAO userDAO = new UserDAO(jdbc);
        AccountTransactionalDAO transDAO = new AccountTransactionalDAO(jdbc);

        // Carrega contas
        Account contaRemetente = accountDAO.getByUserId(remetente.getId());
        if (contaRemetente == null) {
            forwardMsg(request, response, "Conta do remetente não encontrada.");
            return;
        }

        Users destinatario = userDAO.getByEmail(emailDestino);
        if (destinatario == null) {
            forwardMsg(request, response, "Não há nenhuma conta vinculada a esse e-mail.");
            return;
        }

        Account contaDestinatario = accountDAO.getByUserId(destinatario.getId());
        if (contaDestinatario == null) {
            forwardMsg(request, response, "Conta do destinatário não encontrada.");
            return;
        }

        // [D14] Saldo insuficiente
        if (contaRemetente.getBalance().compareTo(valor) < 0) {
            forwardMsg(request, response, "Saldo insuficiente para transferência.");
            return;
        }

        // ===== Execução atômica =====
        BigDecimal saldoOrigemAntes = contaRemetente.getBalance();
        BigDecimal saldoDestinoAntes = contaDestinatario.getBalance();

        try {
            contaRemetente.setBalance(saldoOrigemAntes.subtract(valor));
            contaDestinatario.setBalance(saldoDestinoAntes.add(valor));

            accountDAO.update(contaRemetente);
            accountDAO.update(contaDestinatario);

            // Registra transações
            AccountTransactional out = new AccountTransactional(
                    TransactionType.TRANSFER_OUT,
                    valor,
                    LocalDateTime.now(),
                    "Transferência para: " + emailDestino,
                    contaRemetente
            );
            AccountTransactional in = new AccountTransactional(
                    TransactionType.TRANSFER_IN,
                    valor,
                    LocalDateTime.now(),
                    "Recebido de: " + remetente.getName(),
                    contaDestinatario
            );

            transDAO.insert(out);
            transDAO.insert(in);

            // Sucesso
            request.setAttribute("mensagem", "Transferência realizada com sucesso!");
            request.setAttribute("usuario", remetente);
            request.setAttribute("conta", accountDAO.getByUserId(remetente.getId()));
            request.getRequestDispatcher("/views/transferencia.jsp").forward(request, response);
        } catch (Exception ex) {
            // Rollback manual
            try {
                contaRemetente.setBalance(saldoOrigemAntes);
                contaDestinatario.setBalance(saldoDestinoAntes);
                accountDAO.update(contaRemetente);
                accountDAO.update(contaDestinatario);
            } catch (Exception ignore) {
            }
            forwardMsg(request, response, "Erro ao processar a transferência. Tente novamente.");
        }
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
