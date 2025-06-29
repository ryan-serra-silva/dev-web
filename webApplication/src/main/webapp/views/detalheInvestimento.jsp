<%@ page contentType="text/html; charset=UTF-8" language="java" import="com.mycompany.webapplication.entity.Investment,com.mycompany.webapplication.entity.InvestmentProduct,java.math.BigDecimal" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <title>Resumo do Investimento</title>
  <style>
    body {
      background-color: #121212;
      color: #e0e0e0;
      font-family: 'Segoe UI', sans-serif;
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
      text-align: center;
      margin-bottom: 30px;
    }

    .info {
      font-size: 18px;
      margin-bottom: 15px;
    }

    .info strong {
      color: #27ae60;
    }

    .btn-voltar {
      display: block;
      margin: 30px auto 0;
      text-align: center;
      background-color: #3498db;
      color: white;
      padding: 12px 20px;
      border: none;
      border-radius: 6px;
      font-size: 16px;
      text-decoration: none;
    }

    .btn-voltar:hover {
      background-color: #2980b9;
    }
  </style>
</head>
<body>
<div class="container">
  <h1>Investimento Confirmado</h1>

  <div class="info">Tipo: <strong>${produto.typeInvestment}</strong></div>
  <div class="info">Valor investido: <strong>R$ ${investimento.amount}</strong></div>
  <div class="info">Taxa de retorno: <strong>${produto.returnRate}% ao mês</strong></div>
  <div class="info">Duração: <strong>${tempo} meses</strong></div>
  <div class="info">Valor total ao final: <strong>R$ ${valorFinal}</strong></div>
  <div class="info">Nova data de resgate: <strong>${investimento.endDate}</strong></div>

  <a class="btn-voltar" href="${pageContext.request.contextPath}/Home">Voltar para o Início</a>
</div>
</body>
</html>
