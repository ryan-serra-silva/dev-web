<%-- 
    Document   : transferencia
    Created on : 28 de jun. de 2025, 11:10:29
    Author     : ryan
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" import="com.mycompany.webapplication.entity.Users,com.mycompany.webapplication.entity.Account" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Proteção: verifica se o usuário está logado
    Users usuario = (Users) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/Login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <title>Transferência - Banco Digital</title>
  <style>
    body {
      background-color: #121212;
      color: #e0e0e0;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      padding: 20px;
    }

    .container {
      max-width: 600px;
      margin: auto;
      background-color: #1e1e1e;
      padding: 30px;
      border-radius: 10px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.6);
    }

    h1 {
      font-size: 24px;
      text-align: center;
      margin-bottom: 30px;
    }

    label {
      display: block;
      margin-bottom: 8px;
      font-size: 16px;
    }

    input[type="number"], input[type="text"] {
      width: 100%;
      padding: 10px;
      border-radius: 6px;
      border: 1px solid #555;
      background-color: #2a2a2a;
      color: #fff;
      margin-bottom: 20px;
      font-size: 16px;
    }

    button {
      width: 100%;
      background-color: #9b59b6;
      color: white;
      border: none;
      padding: 12px;
      font-size: 16px;
      border-radius: 6px;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    button:hover {
      background-color: #8e44ad;
    }

    .user-info {
      text-align: center;
      margin-bottom: 20px;
      font-size: 18px;
      color: #bbbbbb;
    }

    .message {
      margin-top: 15px;
      text-align: center;
      font-size: 16px;
    }
  </style>
</head>
<body>
  <div class="container">
    <h1>Transferência</h1>

    <div class="user-info">Olá, ${usuario.name} | Saldo atual: R$ ${conta.balance}</div>
    
    <button type="button" onclick="voltarParaHome()">Voltar para Home</button>

    <form id="formTransferencia" action="${pageContext.request.contextPath}/Transferir" method="post">
      <label for="destino">E-mail do destinatário:</label>
      <input type="text" name="destino" id="destino" required />

      <label for="valor">Valor da transferência (R$):</label>
      <input type="number" step="0.01" min="0.01" name="valor" id="valor" required />

      <button type="submit">Confirmar Transferência</button>
    </form>

    <c:if test="${not empty mensagem}">
        <div class="message" style="color: #e74c3c; margin-top: 15px; text-align: center;">${mensagem}</div>
    </c:if>

    <div class="message" id="mensagem"></div>
  </div>

<script>
(function () {
  const form = document.getElementById("formTransferencia");
  const mensagem = document.getElementById("mensagem");
  const btnSubmit = form?.querySelector('button[type="submit"]');

  // Variáveis vindas do JSP (com fallback seguro)
  const saldoAtual = Number(String('${conta.balance}').replace(',', '.')) || 0;
  const emailUsuario = String('${usuario.email}').trim().toLowerCase();
  const serverMessage = String('${mensagem}').trim();

  // Limites de negócio (ajuste livre para seus testes)
  const MIN_TRANSFER = 0.01;
  const MAX_TRANSFER = 100000.00;
  const DAILY_LIMIT  = 5000.00;

  // Se existir, some diário acumulado; senão, 0 (para testar o limite diário)
  const dailyTransferred =
    Number(String('${empty usuario.dailyTransferred ? 0 : usuario.dailyTransferred}')
      .replace(',', '.')) || 0;

  // Lista de domínios bloqueados (ajuste para criar cenários de erro)
  const blockedDomains = new Set(['example.com', 'test.com']);

  // Helpers
  function showMsg(text, ok = false) {
    if (!mensagem) return;
    mensagem.textContent = text;
    mensagem.style.color = ok ? "#2ecc71" : "#e74c3c";
  }

  function emailValido(e) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i;
    return re.test(e);
  }

  function parseValor(input) {
    if (!input) return NaN;
    const normalizado = String(input).replace(/\./g, '').replace(',', '.').trim();
    const num = parseFloat(normalizado);
    return Number.isFinite(num) ? num : NaN;
  }

  function casasDecimais(str) {
    const s = String(str).trim();
    const p = s.includes(',') ? s.split(',')[1] : s.split('.')[1];
    return p ? p.length : 0;
  }

  // [D1] Se veio mensagem do servidor (pós-backend), apenas exibe e não bloqueia nova tentativa
  if (serverMessage) {
    showMsg(serverMessage, /sucesso|ok|realizada/i.test(serverMessage));
  }

  form?.addEventListener("submit", function (e) {
    const valorInput = document.getElementById("valor");
    const destinoInput = document.getElementById("destino");

    const destinoRaw = String(destinoInput?.value || "");
    const destino = destinoRaw.trim().toLowerCase();
    const valor = parseValor(valorInput?.value);
    const decs = casasDecimais(valorInput?.value || "");

    // Limpa mensagem anterior
    showMsg("");

    // [D2] Evitar envio duplicado/duplo clique
    if (btnSubmit && btnSubmit.disabled) {
      e.preventDefault();
      showMsg("Operação já em andamento. Aguarde…");
      return;
    }

    // [D3] Destinatário vazio
    if (!destino) {
      e.preventDefault();
      showMsg("Informe o e-mail do destinatário.");
      return;
    }

    // [D4] Formato de e-mail inválido
    if (!emailValido(destino)) {
      e.preventDefault();
      showMsg("E-mail do destinatário inválido.");
      return;
    }

    // [D5] Domínio bloqueado (cria cenário de erro adicional)
    const domain = destino.split('@')[1] || '';
    if (blockedDomains.has(domain)) {
      e.preventDefault();
      showMsg(`Transferências para o domínio "${domain}" estão bloqueadas.`);
      return;
    }

    // [D6] Valor ausente ou NaN
    if (!Number.isFinite(valor)) {
      e.preventDefault();
      showMsg("Informe um valor numérico válido (ex.: 10,50).");
      return;
    }

    // [D7] Valor <= 0
    if (valor <= 0) {
      e.preventDefault();
      showMsg("Informe um valor maior que zero.");
      return;
    }

    // [D8] Mais de 2 casas decimais
    if (decs > 2) {
      e.preventDefault();
      showMsg("Use no máximo duas casas decimais.");
      return;
    }

    // [D9] Mesma conta (destino = e-mail do usuário)
    if (emailUsuario && destino === emailUsuario) {
      e.preventDefault();
      showMsg("Não é possível transferir para a própria conta.");
      return;
    }

    // [D10] Saldo insuficiente
    if (valor > saldoAtual) {
      e.preventDefault();
      showMsg("Saldo insuficiente para a transferência.");
      return;
    }

    // [D11] Abaixo do mínimo por transação
    if (valor < MIN_TRANSFER) {
      e.preventDefault();
      showMsg(`Valor mínimo por transferência: R$ ${MIN_TRANSFER.toFixed(2)}.`);
      return;
    }

    // [D12] Acima do máximo por transação
    if (valor > MAX_TRANSFER) {
      e.preventDefault();
      showMsg(`Valor máximo por transferência: R$ ${MAX_TRANSFER.toFixed(2)}.`);
      return;
    }

    // [D13] Estouro do limite diário
    if ((dailyTransferred + valor) > DAILY_LIMIT) {
      e.preventDefault();
      const restante = Math.max(0, DAILY_LIMIT - dailyTransferred);
      showMsg(`Limite diário excedido. Restante disponível hoje: R$ ${restante.toFixed(2)}.`);
      return;
    }

    // Se passou por tudo, feedback e trava duplo clique
    if (btnSubmit) btnSubmit.disabled = true;
    showMsg("Transferência em andamento...", true);
  });

  // Botão voltar
  window.voltarParaHome = function () {
    window.location.href = '${pageContext.request.contextPath}/Home';
  };
})();
</script>

</body>
</html>
