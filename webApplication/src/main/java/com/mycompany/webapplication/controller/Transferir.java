package com.mycompany.webapplication.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.AccountTransactional;
import com.mycompany.webapplication.entity.TransactionType;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.AccountTransactionalDAO;
import com.mycompany.webapplication.model.JDBC;
import com.mycompany.webapplication.model.UserDAO;
import static com.mycompany.webapplication.usecases.TransferirUC.validateTransfer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "Transferir", urlPatterns = {"/Transferir"})
public class Transferir extends HttpServlet {

    // DAOs como dependências (para poder mockar nos testes)
    AccountDAO accountDAO;
    UserDAO userDAO;
    AccountTransactionalDAO transDAO;

    // Construtor padrão usado pelo container
    public Transferir() {
        JDBC jdbc = new JDBC();
        this.accountDAO = new AccountDAO();
        this.userDAO = new UserDAO(jdbc);
        this.transDAO = new AccountTransactionalDAO(jdbc);
    }

    // Construtor de teste (package-private) – permite injetar mocks
    Transferir(AccountDAO accountDAO, UserDAO userDAO, AccountTransactionalDAO transDAO) {
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
        this.transDAO = transDAO;
    }

    private void forwardMsg(HttpServletRequest request, HttpServletResponse response, String msg)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Users usuario = (Users) session.getAttribute("usuario");

        Account conta = null;
        if (usuario != null) {
            conta = accountDAO.getByUserId(usuario.getId());
        }

        request.setAttribute("usuario", usuario);
        request.setAttribute("conta", conta);
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

        String emailDestinoRaw = request.getParameter("email");

        String valorParam = request.getParameter("valor");

        // Validação inicial (casos da TransferirUC)
        String errorMsg = validateTransfer(remetente, emailDestinoRaw, valorParam);
        if (errorMsg != null) {
            forwardMsg(request, response, errorMsg);
            return;
        }

        // Parse seguro do valor
        BigDecimal valor = parseValor(valorParam);
        String emailDestino = emailDestinoRaw.trim().toLowerCase();

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

        // Saldo insuficiente
        if (contaRemetente.getBalance().compareTo(valor) < 0) {
            forwardMsg(request, response, "Saldo insuficiente para transferência.");
            return;
        }

        // ===== Execução “atômica” (com rollback manual) =====
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
                // ignora falha no rollback
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

        Account conta = accountDAO.getByUserId(usuario.getId());

        request.setAttribute("usuario", usuario);
        request.setAttribute("conta", conta);
        request.getRequestDispatcher("/views/transferencia.jsp").forward(request, response);
    }
}
