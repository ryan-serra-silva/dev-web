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
  <title>Depósito - Banco Digital</title>
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

    input[type="number"] {
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
      background-color: #3498db;
      color: white;
      border: none;
      padding: 12px;
      font-size: 16px;
      border-radius: 6px;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    button:hover {
      background-color: #2980b9;
    }

    .message {
      margin-top: 15px;
      font-size: 16px;
      text-align: center;
    }

    .user-info {
      text-align: center;
      margin-bottom: 20px;
      font-size: 18px;
      color: #bbbbbb;
    }
  </style>
</head>
<body>
  <div class="container">
    <h1>Depósito</h1>

    <div class="user-info">Olá, ${usuario.name} | Saldo atual: R$ ${conta.balance}</div>

    <button type="button" onclick="voltarParaHome()">Voltar para Home</button>

    <form id="formDeposito" action="${pageContext.request.contextPath}/Depositar" method="post">
      <label for="valor">Valor do Depósito (R$):</label>
      <input type="number" step="0.01" min="0.01" name="valor" id="valor" required />

      <button type="submit">Confirmar Depósito</button>
    </form>

    <!-- Exibe mensagens do servlet -->
    <div class="message"
         style="color: ${not empty alerta ? '#f39c12' : (mensagem != null && mensagem.toLowerCase().contains('erro') ? '#e74c3c' : '#2ecc71')}">
        ${mensagem}
        <c:if test="${not empty alerta}">
            <br/>${alerta}
        </c:if>
    </div>
  </div>

  <script>
    function voltarParaHome() {
      window.location.href = '${pageContext.request.contextPath}/Home';
    }

    // Validação inicial no frontend (opcional)
    const form = document.getElementById("formDeposito");
    form.addEventListener("submit", function (e) {
      const valor = parseFloat(document.getElementById("valor").value);
      if (isNaN(valor) || valor <= 0) {
        e.preventDefault();
        const mensagemDiv = document.querySelector(".message");
        mensagemDiv.textContent = "Por favor, insira um valor válido para depósito.";
        mensagemDiv.style.color = "#e74c3c";
      }
    });
  </script>
</body>
</html>
