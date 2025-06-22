<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sistema Bancário</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <style>
        body {
            background-color: #121212;
            color: #fff;
            margin: 0;
            font-family: sans-serif;
        }

        .bank-header {
            background-color: #1f1f1f;
            padding: 15px 0;
            border-bottom: 1px solid #333;
        }

        .logo {
            font-size: 1.5rem;
            font-weight: bold;
            color: #007bff;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 0.9rem;
        }

        .user-avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background-color: #333;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .main-container {
            max-width: 800px;
            margin: 20px auto;
            padding: 0 15px;
        }

        .balance-card {
            background-color: #1f1f1f;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
        }

        .balance-amount {
            font-size: 2rem;
            font-weight: bold;
            margin: 10px 0;
        }

        .operations-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 15px;
        }

        .operation-btn {
            background-color: #1f1f1f;
            border: 1px solid #333;
            border-radius: 8px;
            padding: 20px 10px;
            color: #fff;
            font-weight: 500;
            text-align: center;
            cursor: pointer;
            transition: background 0.2s ease;
        }

        .operation-btn:hover {
            background-color: #2a2a2a;
        }

        .operation-btn i {
            font-size: 1.8rem;
            display: block;
            margin-bottom: 10px;
        }

        .logout-btn {
            background: none;
            border: 1px solid #dc3545;
            color: #dc3545;
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 0.85rem;
        }

        .logout-btn:hover {
            background-color: #dc3545;
            color: white;
        }

        .footer {
            text-align: center;
            padding: 20px 0;
            font-size: 13px;
            color: #888;
            border-top: 1px solid #333;
            margin-top: 30px;
        }
    </style>
</head>
<body>
    <header class="bank-header">
        <div class="container d-flex justify-content-between align-items-center">
            <div class="logo"><i class="fas fa-university"></i> Banco Digital</div>
            <div class="user-info">
                <div class="user-avatar">JS</div>
                <div>
                    <div>João Silva</div>
                    <div style="color: #aaa;">Conta: 12345-6</div>
                </div>
                <button class="logout-btn" onclick="logout()">Sair</button>
            </div>
        </div>
    </header>

    <div class="main-container">
        <div class="balance-card">
            <h4>Saldo Disponível</h4>
            <div class="balance-amount">R$ 5.250,75</div>
            <div class="d-flex justify-content-between" style="font-size: 0.9rem;">
                <div>
                    <small>Limite</small><br>R$ 2.500,00
                </div>
                <div>
                    <small>Atualizado em</small><br>22/06/2025
                </div>
            </div>
        </div>

        <div class="operations-grid">
            <div class="operation-btn" onclick="operacao('saldo')">
                <i class="fas fa-coins" style="color:#007bff;"></i> Saldo
            </div>
            <div class="operation-btn" onclick="operacao('extrato')">
                <i class="fas fa-file-invoice" style="color:#17a2b8;"></i> Extrato
            </div>
            <div class="operation-btn" onclick="operacao('deposito')">
                <i class="fas fa-money-bill-wave" style="color:#28a745;"></i> Depósito
            </div>
            <div class="operation-btn" onclick="operacao('saque')">
                <i class="fas fa-hand-holding-usd" style="color:#dc3545;"></i> Saque
            </div>
            <div class="operation-btn" onclick="operacao('transferencia')">
                <i class="fas fa-exchange-alt" style="color:#ffc107;"></i> Transferência
            </div>
            <div class="operation-btn" onclick="operacao('investimento')">
                <i class="fas fa-chart-line" style="color:#6610f2;"></i> Investimento
            </div>
        </div>
    </div>

    <footer class="footer">
        Sistema Bancário - Trabalho de Grupo | &copy; 2025
    </footer>

    <script>
        function operacao(tipo) {
            alert("Operação: " + tipo);
            // window.location.href = tipo + ".jsp";
        }

        function logout() {
            if (confirm("Deseja sair?")) {
                window.location.href = "login.jsp";
            }
        }
    </script>
</body>
</html>
