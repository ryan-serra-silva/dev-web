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

    .transactions, .actions {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    .transaction, .action {
      padding: 15px;
      background-color: #2a2a2a;
      border-radius: 6px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .action button, .toggle-extrato-btn {
      background-color: #3498db;
      color: white;
      border: none;
      padding: 10px 20px;
      font-size: 16px;
      border-radius: 6px;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    .action button:hover, .toggle-extrato-btn:hover {
      background-color: #2980b9;
    }

    /* Extrato oculto inicialmente */
    #extrato {
      display: none;
    }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>Banco Digital</h1>
      <div class="user-info">Bem-vindo, João Silva</div>
    </div>

    <div class="balance">Saldo disponível: R$ 8.200,00</div>

    <div class="section">
      <h2>Extrato</h2>
      <button class="toggle-extrato-btn" onclick="toggleExtrato()">Visualizar Extrato</button>

      <div id="extrato" class="transactions" style="margin-top: 15px;">
        <div class="transaction">
          <span>Depósito</span><span>+ R$ 2.000,00</span>
        </div>
        <div class="transaction">
          <span>Transferência enviada</span><span>- R$ 500,00</span>
        </div>
        <div class="transaction">
          <span>Investimento aplicado</span><span>- R$ 1.000,00</span>
        </div>
      </div>
    </div>

    <div class="section">
      <h2>Ações</h2>
      <div class="actions">
        <div class="action">
          <span>Depositar</span>
          <button>Fazer Depósito</button>
        </div>
        <div class="action">
          <span>Sacar</span>
          <button>Fazer Saque</button>
        </div>
        <div class="action">
          <span>Transferir</span>
          <button>Fazer Transferência</button>
        </div>
        <div class="action">
          <span>Investir</span>
          <button>Fazer Investimento</button>
        </div>
      </div>
    </div>
  </div>

  <script>
    function toggleExtrato() {
      const extrato = document.getElementById("extrato");
      extrato.style.display = extrato.style.display === "none" ? "flex" : "none";
    }
  </script>
</body>
</html>
