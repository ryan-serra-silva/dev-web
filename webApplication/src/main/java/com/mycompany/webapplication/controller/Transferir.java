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

@WebServlet(name = "Transferir", urlPatterns = {"/Transferir"})
public class Transferir extends HttpServlet {

    // Limites de negócio (ajuste se quiser)
    private static final BigDecimal MIN_TRANSFER = new BigDecimal("0.01");
    private static final BigDecimal MAX_TRANSFER = new BigDecimal("100000.00");
    private static final Pattern EMAIL_RE = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final Set<String> BLOCKED_DOMAINS = Set.of("example.com", "test.com");

    private void forwardMsg(HttpServletRequest request, HttpServletResponse response, String msg)
            throws ServletException, IOException {
        request.setAttribute("mensagem", msg);
        request.getRequestDispatcher("/views/transferencia.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Users remetente = (Users) session.getAttribute("usuario");

        // Parâmetros crus
        String emailDestinoRaw = request.getParameter("destino");
        String valorParam = request.getParameter("valor");

        // [D1] Usuário não autenticado
        if (remetente == null) {
            forwardMsg(request, response, "Sessão expirada. Faça login novamente.");
            return;
        }

        // Normalizações
        String emailDestino = emailDestinoRaw == null ? null : emailDestinoRaw.trim().toLowerCase();

        // Parse seguro do valor (suporta vírgula)
        BigDecimal valor = null;
        if (valorParam != null) {
            String norm = valorParam.replace(".", "").replace(",", ".").trim();
            try {
                valor = new BigDecimal(norm);
            } catch (NumberFormatException e) {
                // mantém null para cair na validação
            }
        }

        // [D2] Email destino vazio/nulo
        if (emailDestino == null || emailDestino.isEmpty()) {
            forwardMsg(request, response, "Informe o e-mail do destinatário.");
            return;
        }

        // [D3] Formato de e-mail inválido
        if (!EMAIL_RE.matcher(emailDestino).matches()) {
            forwardMsg(request, response, "E-mail do destinatário inválido.");
            return;
        }

        // [D4] Domínio bloqueado 
        String[] parts = emailDestino.split("@", 2);
        String domain = parts.length == 2 ? parts[1] : "";
        if (BLOCKED_DOMAINS.contains(domain)) {
            forwardMsg(request, response, "Transferências para o domínio \"" + domain + "\" estão bloqueadas.");
            return;
        }

        // [D5] Valor ausente ou inválido
        if (valor == null) {
            forwardMsg(request, response, "Informe um valor numérico válido.");
            return;
        }

        // [D6] Valor <= 0
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            forwardMsg(request, response, "Informe um valor maior que zero.");
            return;
        }

        // [D7] Mais de 2 casas decimais
        if (valor.scale() > 2) {
            forwardMsg(request, response, "Use no máximo duas casas decimais.");
            return;
        }

        // Ajusta escala/rounding para consistência
        valor = valor.setScale(2, RoundingMode.HALF_UP);

        // [D8] Mesma conta (destino = e-mail do remetente)
        if (remetente.getEmail() != null
                && emailDestino.equalsIgnoreCase(remetente.getEmail().trim().toLowerCase())) {
            forwardMsg(request, response, "Não é possível transferir para a própria conta.");
            return;
        }

        // [D9] Abaixo do mínimo
        if (valor.compareTo(MIN_TRANSFER) < 0) {
            forwardMsg(request, response, "Valor mínimo por transferência é R$ " + MIN_TRANSFER.toPlainString() + ".");
            return;
        }

        // [D10] Acima do máximo
        if (valor.compareTo(MAX_TRANSFER) > 0) {
            forwardMsg(request, response, "Valor máximo por transferência é R$ " + MAX_TRANSFER.toPlainString() + ".");
            return;
        }

        // DAOs
        AccountDAO accountDAO = new AccountDAO();
        UserDAO userDAO = new UserDAO();
        AccountTransactionalDAO transDAO = new AccountTransactionalDAO();

        // Carrega contas
        Account contaRemetente = accountDAO.getByUserId(remetente.getId());

        // [D11] Conta remetente inexistente (raro, mas cheque)
        if (contaRemetente == null) {
            forwardMsg(request, response, "Conta do remetente não encontrada.");
            return;
        }

        Users destinatario = userDAO.getByEmail(emailDestino);

        // [D12] Usuário/conta destino inexistente
        if (destinatario == null) {
            forwardMsg(request, response, "Não há nenhuma conta vinculada a esse e-mail.");
            return;
        }

        Account contaDestinatario = accountDAO.getByUserId(destinatario.getId());

        // [D13] Conta do destinatário inexistente (consistência)
        if (contaDestinatario == null) {
            forwardMsg(request, response, "Conta do destinatário não encontrada.");
            return;
        }

        // [D14] Saldo insuficiente
        if (contaRemetente.getBalance().compareTo(valor) < 0) {
            forwardMsg(request, response, "Saldo insuficiente para transferência.");
            return;
        }

        // ===== Execução atômica (tentativa) =====
        BigDecimal saldoOrigemAntes = contaRemetente.getBalance();
        BigDecimal saldoDestinoAntes = contaDestinatario.getBalance();

        try {
            // Atualiza saldos na memória
            contaRemetente.setBalance(saldoOrigemAntes.subtract(valor));
            contaDestinatario.setBalance(saldoDestinoAntes.add(valor));

            // Persiste
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
            // Rollback manual (como não temos transação declarada aqui)
            try {
                contaRemetente.setBalance(saldoOrigemAntes);
                contaDestinatario.setBalance(saldoDestinoAntes);
                accountDAO.update(contaRemetente);
                accountDAO.update(contaDestinatario);
            } catch (Exception ignore) {
                // se falhar rollback, apenas registra/mostra mensagem
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
