<%-- 
    Document   : saque
    Created on : 28 de jun. de 2025, 11:10:00
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
  <title>Saque - Banco Digital</title>
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
      background-color: #e67e22;
      color: white;
      border: none;
      padding: 12px;
      font-size: 16px;
      border-radius: 6px;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    button:hover {
      background-color: #d35400;
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
    <h1>Saque</h1>

    <div class="user-info">Olá, ${usuario.name} | Saldo atual: R$ ${conta.balance}</div>
    
    <button type="button" onclick="voltarParaHome()">Voltar para Home</button>

    <form id="formSaque" action="${pageContext.request.contextPath}/Sacar" method="post">
        
      <label for="valor">Valor a sacar (R$):</label>
      <input type="number" step="0.01" min="0.01" name="valor" id="valor" required />

      <button type="submit">Confirmar Saque</button>
    </form>

    <div class="message" id="mensagem"></div>
  </div>

  <script>
    const form = document.getElementById("formSaque");
    const mensagem = document.getElementById("mensagem");
    
    const saldoAtual = parseFloat('${conta.balance}');

    form.addEventListener("submit", function (e) {
      const valor = parseFloat(document.getElementById("valor").value);
      if (isNaN(valor) || valor <= 0) {
        e.preventDefault();
        mensagem.textContent = "Digite um valor válido para o saque.";
        mensagem.style.color = "#e74c3c";
      } 
      
      if (valor > saldoAtual) {
          e.preventDefault();
          mensagem.textContent = 'Erro: saldo insuficiente para o saque.';
          return;
      }
        else {
        mensagem.textContent = "Solicitando saque...";
        mensagem.style.color = "#2ecc71";
      }
    });
    
    function voltarParaHome() {
  window.location.href = '${pageContext.request.contextPath}/Home';
    }
  </script>
</body>
</html>
