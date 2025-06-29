<%-- 
    Document   : transferencia
    Created on : 28 de jun. de 2025, 11:10:29
    Author     : ryan
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" import="com.mycompany.webapplication.entity.Users,com.mycompany.webapplication.entity.Account" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <form id="formTransferencia" action="${pageContext.request.contextPath}/Transferir" method="post">
      <label for="destino">E-mail do destinatário:</label>
      <input type="text" name="destino" id="destino" required />

      <label for="valor">Valor da transferência (R$):</label>
      <input type="number" step="0.01" min="0.01" name="valor" id="valor" required />

      <button type="submit">Confirmar Transferência</button>
    </form>

    <div class="message" id="mensagem"></div>
  </div>

  <script>
    const form = document.getElementById("formTransferencia");
    const mensagem = document.getElementById("mensagem");

    form.addEventListener("submit", function (e) {
      const valor = parseFloat(document.getElementById("valor").value);
      const destino = document.getElementById("destino").value.trim();

      if (!destino || isNaN(valor) || valor <= 0) {
        e.preventDefault();
        mensagem.textContent = "Preencha os dados corretamente.";
        mensagem.style.color = "#e74c3c";
      } else {
        mensagem.textContent = "Transferência em andamento...";
        mensagem.style.color = "#2ecc71";
      }
    });
  </script>
</body>
</html>
