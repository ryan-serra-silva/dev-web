<%@ page contentType="text/html" pageEncoding="UTF-8"
  import="com.mycompany.webapplication.entity.Account,com.mycompany.webapplication.entity.Users,com.mycompany.webapplication.entity.AccountTransactional,java.util.ArrayList"
  %>
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
      <title>Banco Digital - Tema Escuro</title>
      <style>
        * {
          margin: 0;
          padding: 0;
          box-sizing: border-box;
          font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        body {
          background-color: #121212;
          color: #e0e0e0;
          padding: 20px;
        }

        .container {
          max-width: 1000px;
          margin: auto;
          background-color: #1e1e1e;
          padding: 30px;
          border-radius: 10px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.6);
        }

        .header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 30px;
        }

        .header h1 {
          font-size: 24px;
          color: #ffffff;
        }

        .user-info {
          font-size: 18px;
          color: #bbbbbb;
        }

        .balance {
          font-size: 28px;
          color: #2ecc71;
          margin-bottom: 20px;
        }

        .section {
          margin-top: 30px;
        }

        .section h2 {
          font-size: 20px;
          color: #ffffff;
          margin-bottom: 15px;
          border-bottom: 2px solid #444;
          padding-bottom: 5px;
        }

        .transactions,
        .actions {
          display: flex;
          flex-direction: column;
          gap: 10px;
        }

        .transaction,
        .action {
          padding: 15px;
          background-color: #2a2a2a;
          border-radius: 6px;
          display: flex;
          justify-content: space-between;
          align-items: center;
        }

        .action button,
        .toggle-extrato-btn {
          background-color: #3498db;
          color: white;
          border: none;
          padding: 10px 20px;
          font-size: 16px;
          border-radius: 6px;
          cursor: pointer;
          transition: background-color 0.3s;
        }

        .action button:hover,
        .toggle-extrato-btn:hover {
          background-color: #2980b9;
        }

        #extrato {
          display: none;
          flex-direction: column;
        }
      </style>
    </head>

    <body>
      <div class="container">
        <div class="header">
          <h1>Banco Digital</h1>
          <div class="user-info">Bem-vindo, ${usuario.name}.</div>
          <a href="${pageContext.request.contextPath}/Home?action=logout">Sair</a>
        </div>

        <div class="balance">Saldo disponível: R$ ${conta.balance}</div>

        <div class="section">
          <h2>Extrato</h2>
          <button class="toggle-extrato-btn" onclick="toggleExtrato()">Visualizar Extrato</button>

          <div id="extrato" class="transactions" style="margin-top: 15px;">
            <c:choose>
              <c:when test="${not empty extrato}">
                <c:forEach var="t" items="${extrato}">
                  <div class="transaction">
                    <span>${t.description}</span>
                    <span>
                      <c:choose>
                        <c:when test="${t.typeTransaction == 'DEPOSIT'}">+ R$ ${t.amount}</c:when>
                        <c:when test="${t.typeTransaction == 'WITHDRAW'}">- R$ ${t.amount}</c:when>
                        <c:when test="${t.typeTransaction == 'TRANSFER_IN'}">+ R$ ${t.amount}</c:when>
                        <c:when test="${t.typeTransaction == 'TRANSFER_OUT'}">- R$ ${t.amount}</c:when>
                        <c:otherwise>R$ ${t.amount}</c:otherwise>
                      </c:choose>
                    </span>
                  </div>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <div class="transaction">
                  <span>Nenhuma transação encontrada.</span>
                </div>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <div class="section">
          <h2>Ações</h2>
          <div class="actions">
            <div class="action">
              <span>Depositar</span>
              <button onclick="irParaDeposito()">Fazer Depósito</button>
            </div>
            <div class="action">
              <span>Sacar</span>
              <button onclick="irParaSaque()">Fazer Saque</button>
            </div>
            <div class="action">
              <span>Transferir</span>
              <button onclick="irParaTransferencia()">Fazer Transferência</button>
            </div>
            <div class="action">
              <span>Investir</span>
              <button onclick="irParaInvestimento()">Fazer Investimento</button>
            </div>
          </div>
        </div>
      </div>

      <script>
        function toggleExtrato() {
          const extrato = document.getElementById("extrato");
          const btn = document.querySelector(".toggle-extrato-btn");

          if (extrato.style.display === "none" || extrato.style.display === "") {
            extrato.style.display = "flex";
            btn.textContent = "Ocultar Extrato";
          } else {
            extrato.style.display = "none";
            btn.textContent = "Visualizar Extrato";
          }
        }

        function irParaDeposito() {
          window.location.href = '${pageContext.request.contextPath}/Depositar';
        }

        function irParaSaque() {
          window.location.href = '${pageContext.request.contextPath}/Sacar';
        }

        function irParaTransferencia() {
          window.location.href = '${pageContext.request.contextPath}/Transferir';
        }

        function irParaInvestimento() {
          window.location.href = '${pageContext.request.contextPath}/Investir';
        }
      </script>
    </body>

    </html>