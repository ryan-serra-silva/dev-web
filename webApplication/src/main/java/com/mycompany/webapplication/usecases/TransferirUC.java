package com.mycompany.webapplication.usecases;

import com.mycompany.webapplication.entity.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.regex.Pattern;

public class TransferirUC {

    // Limites de negócio
    private static final BigDecimal MIN_TRANSFER = new BigDecimal("0.01");
    private static final BigDecimal MAX_TRANSFER = new BigDecimal("100000.00");
    private static final Pattern EMAIL_RE = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final Set<String> BLOCKED_DOMAINS = Set.of("example.com", "test.com");

    // Método de validação
    public static String validateTransfer(Users remetente, String emailDestinoRaw, String valorParam) {
        // Normalizações
        String emailDestino = emailDestinoRaw == null ? null : emailDestinoRaw.trim().toLowerCase();

        // [D1] Usuário não autenticado
        if (remetente == null) {
            return "Sessão expirada. Faça login novamente.";
        }

        // [D2] Email destino vazio/nulo
        if (emailDestino == null || emailDestino.isEmpty()) {
            return "Informe o e-mail do destinatário.";
        }

        // [D3] Formato de e-mail inválido
        if (!EMAIL_RE.matcher(emailDestino).matches()) {
            return "E-mail do destinatário inválido.";
        }

        // [D4] Domínio bloqueado
        String[] parts = emailDestino.split("@", 2);
        String domain = parts.length == 2 ? parts[1] : "";
        if (BLOCKED_DOMAINS.contains(domain)) {
            return "Transferências para este domínio estão bloqueadas.";
        }
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
        // [D5] Valor ausente ou inválido
        if (valor == null) {
            return "Informe um valor numérico válido.";
        }

        // [D6] Valor <= 0
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            return "Informe um valor maior que zero.";
        }

        // [D7] Mais de 3 casas decimais
        if (valor.scale() > 3) {
            return "Use no máximo três casas decimais.";
        }

        // Ajusta escala/rounding para consistência
        valor = valor.setScale(3, RoundingMode.HALF_UP);

        // [D8] Mesma conta
        if (remetente.getEmail() != null
                && emailDestino.equalsIgnoreCase(remetente.getEmail().trim().toLowerCase())) {
            return "Não é possível transferir para a própria conta.";
        }

        // [D9] Abaixo do mínimo
        if (valor.compareTo(MIN_TRANSFER) < 0) {
            return "Valor mínimo por transferência é R$ 0.01.";
        }

        // [D10] Acima do máximo
        if (valor.compareTo(MAX_TRANSFER) > 0) {
            return "Valor máximo por transferência é R$100000.00.";
        }

        return null; // sem erros
    }
}
