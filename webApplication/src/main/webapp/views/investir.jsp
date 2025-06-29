<%@ page contentType="text/html" pageEncoding="UTF-8" import="com.mycompany.webapplication.entity.Users,com.mycompany.webapplication.entity.Account" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <title>Investimento - Banco Digital</title>
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

    input[type="number"], select {
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
      background-color: #27ae60;
      color: white;
      border: none;
      padding: 12px;
      font-size: 16px;
      border-radius: 6px;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    button:hover {
      background-color: #1abc9c;
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

    .info {
      font-size: 14px;
      text-align: center;
      color: #aaaaaa;
      margin-bottom: 20px;
    }
  </style>
</head>
<body>
  <div class="container">
    <h1>Investimento</h1>

    <div class="user-info">Olá, ${usuario.name} | Saldo atual: R$ ${conta.balance}</div>
    
    <button type="button" onclick="voltarParaHome()">Voltar para Home</button>

    <form id="formInvestimento" action="${pageContext.request.contextPath}/Investir" method="post">
      <label for="tipo">Tipo de Investimento:</label>
      <select id="tipo" name="tipo" required>
        <option value="">Selecione</option>
        <option value="CDB">CDB</option>
        <option value="TESOURO">Tesouro Direto</option>
        <option value="POUPANÇA">Poupança</option>
      </select>

      <label for="valor">Valor do Investimento (R$):</label>
      <input type="number" step="0.01" min="0.01" name="valor" id="valor" required />

      <label for="tempo">Tempo do Investimento (em meses):</label>
      <input type="number" min="1" name="tempo" id="tempo" required />

      <div class="info">O valor final será calculado com base no tipo de investimento, tempo e taxa de retorno.</div>

      <button type="submit">Confirmar Investimento</button>
    </form>

    <div class="message" id="mensagem"></div>
  </div>

  <script>
    const form = document.getElementById("formInvestimento");
    const mensagem = document.getElementById("mensagem");

    form.addEventListener("submit", function (e) {
      const tipo = document.getElementById("tipo").value;
      const valor = parseFloat(document.getElementById("valor").value);
      const tempo = parseInt(document.getElementById("tempo").value);

      if (!tipo || isNaN(valor) || valor <= 0 || isNaN(tempo) || tempo < 1) {
        e.preventDefault();
        mensagem.textContent = "Preencha todos os campos corretamente.";
        mensagem.style.color = "#e74c3c";
      } else {
        mensagem.textContent = "Enviando solicitação de investimento...";
        mensagem.style.color = "#2ecc71";
      }
    });
    
function voltarParaHome() {
  window.location.href = '${pageContext.request.contextPath}/Home';
}

  </script>
</body>
</html>
