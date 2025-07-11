<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
        <%
            // Proteção: verifica se o usuário está logado
            com.mycompany.webapplication.entity.Users usuario = (com.mycompany.webapplication.entity.Users) session.getAttribute("usuario");
            if (usuario == null) {
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }
        %>

            <!DOCTYPE html>
            <html lang="pt-br">

            <head>
                <meta charset="UTF-8">
                <title>Meus Investimentos - Banco Digital</title>
                <style>
                    /* CSS Base Fornecido e Adaptado */
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

                    /* --- HEADER --- */
                    .header {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        margin-bottom: 20px;
                    }

                    .header h1 {
                        font-size: 24px;
                        color: #ffffff;
                    }

                    .header .user-info {
                        font-size: 16px;
                        color: #bbbbbb;
                    }

                    .header .user-info a {
                        color: #3498db;
                        text-decoration: none;
                        margin-left: 15px;
                    }

                    /* --- SEÇÕES --- */
                    .section {
                        margin-top: 30px;
                    }

                    .section h2 {
                        font-size: 20px;
                        color: #ffffff;
                        margin-bottom: 15px;
                        border-bottom: 2px solid #444;
                        padding-bottom: 10px;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }

                    /* --- BOTÕES --- */
                    .btn {
                        background-color: #3498db;
                        color: white;
                        border: none;
                        padding: 10px 20px;
                        font-size: 16px;
                        border-radius: 6px;
                        cursor: pointer;
                        transition: background-color 0.3s;
                        text-decoration: none;
                    }

                    .btn:hover {
                        background-color: #2980b9;
                    }

                    .btn-secondary {
                        background-color: #4e5d6c;
                    }

                    .btn-secondary:hover {
                        background-color: #5a6b7d;
                    }

                    /* --- TABELA DE INVESTIMENTOS --- */
                    .investment-table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 20px;
                    }

                    .investment-table th,
                    .investment-table td {
                        text-align: left;
                        padding: 12px 15px;
                        border-bottom: 1px solid #3a3a3a;
                    }

                    .investment-table thead th {
                        background-color: #2a2a2a;
                        color: #ffffff;
                    }

                    .investment-table tbody tr:hover {
                        background-color: #2c2c2c;
                    }

                    .investment-table .text-center {
                        text-align: center;
                    }

                    .investment-table .text-danger {
                        color: #e74c3c;
                        font-weight: bold;
                    }

                    /* --- FORMULÁRIO --- */
                    .form-container {
                        margin-top: 30px;
                        background-color: #2a2a2a;
                        padding: 25px;
                        border-radius: 8px;
                    }

                    .form-container h4 {
                        font-size: 18px;
                        color: #ffffff;
                        margin-bottom: 20px;
                    }

                    .form-group {
                        margin-bottom: 20px;
                    }

                    .form-group label {
                        display: block;
                        margin-bottom: 8px;
                        color: #bbbbbb;
                    }

                    .form-control {
                        width: 100%;
                        padding: 12px;
                        background-color: #333;
                        border: 1px solid #555;
                        border-radius: 6px;
                        color: #e0e0e0;
                        font-size: 16px;
                    }

                    .form-control:focus {
                        outline: none;
                        border-color: #3498db;
                    }

                    .btn-container {
                        margin-top: 25px;
                    }
                </style>
            </head>

            <body>
                <div class="container">
                    <header class="header">
                        <h1>Banco Digital</h1>
                        <div class="user-info">
                            <span>Bem-vindo,
                                <c:out value="${usuario.name}" />.
                            </span>
                            <a href="${pageContext.request.contextPath}/Home?action=logout">Sair</a>
                        </div>
                    </header>

                    <!-- Botão Voltar para Home - SEMPRE VISÍVEL -->
                    <div style="margin-bottom: 20px; text-align: center;">
                        <button type="button" onclick="window.location.href='${pageContext.request.contextPath}/Home'"
                            class="btn btn-secondary" style="background-color: #e74c3c; border-color: #e74c3c;">
                            ← Voltar para Home
                        </button>
                    </div>

                    <section class="section">
                        <h2>
                            <span>Meus Investimentos</span>
                            <button id="btnNovoInvestimento" class="btn">Novo Investimento</button>
                        </h2>

                        <table class="investment-table">
                            <thead>
                                <tr>
                                    <th>Tipo</th>
                                    <th>Valor Aplicado</th>
                                    <th>Data de Início</th>
                                    <th>Data de Vencimento</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="investimento" items="${listaInvestimentos}">
                                    <tr>
                                        <td>
                                            <c:if test="${not empty investimento.investmentProduct}">
                                                <c:out value="${investimento.investmentProduct.typeInvestment}" />
                                            </c:if>
                                            <c:if test="${empty investimento.investmentProduct}">
                                                <span class="text-danger">Produto Inválido</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${investimento.amount}" type="currency"
                                                currencySymbol="R$ " />
                                        </td>
                                        <td>
                                            <c:if test="${not empty investimento.startDate}">
                                                ${String.format('%02d',
                                                investimento.startDate.dayOfMonth)}/${String.format('%02d',
                                                investimento.startDate.monthValue)}/${investimento.startDate.year}
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${not empty investimento.endDate}">
                                                ${String.format('%02d',
                                                investimento.endDate.dayOfMonth)}/${String.format('%02d',
                                                investimento.endDate.monthValue)}/${investimento.endDate.year}
                                            </c:if>
                                        </td>
                                        <td>
                                            <a href="DetalhesInvestimento?id=${investimento.id}"
                                                class="btn btn-secondary" style="padding: 6px 12px; font-size: 14px;">
                                                Detalhes
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty listaInvestimentos}">
                                    <tr>
                                        <td colspan="4" class="text-center">Você ainda não possui investimentos.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </section>

                    <section id="formNovoInvestimento" class="form-container" style="display: none;">
                        <h4>Aplicar Novo Investimento</h4>
                        <p>Saldo disponível em conta: <strong>R$
                                <fmt:formatNumber value="${conta.balance}" type="number" minFractionDigits="2" />
                            </strong></p>

                        <c:if test="${not empty mensagem}">
                            <p class="text-danger" style="margin-top: 15px;">${mensagem}</p>
                        </c:if>

                        <form id="formInvestir" action="Investir" method="post">
                            <div class="form-group">
                                <label for="tipo">Tipo de Investimento</label>
                                <select id="tipo" name="tipo" class="form-control" required>
                                    <option value="">Selecione um tipo</option>
                                    <option value="POUPANCA">Poupança</option>
                                    <option value="CDB">CDB</option>
                                    <option value="TESOURO">Tesouro</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="valor">Valor a ser investido (R$)</label>
                                <input type="number" id="valor" name="valor" class="form-control" step="0.01" min="1"
                                    required>
                            </div>
                            <div class="form-group">
                                <label for="tempo">Prazo (em meses)</label>
                                <input type="number" id="tempo" name="tempo" class="form-control" min="1" required>
                            </div>
                            <div class="btn-container">
                                <button type="submit" class="btn">Confirmar Investimento</button>
                                <button type="button" id="btnCancelar" class="btn btn-secondary">Cancelar</button>
                            </div>

                            <!-- Div para mensagem de erro -->
                            <div id="mensagemErro" style="
                                margin-top: 15px; 
                                padding: 10px; 
                                background-color: #e74c3c; 
                                color: white; 
                                border-radius: 4px; 
                                text-align: center;
                                display: none;
                            "></div>
                        </form>
                    </section>
                </div>

                <script>
                    const saldoAtual = parseFloat('${conta.balance}');

                    document.getElementById('btnNovoInvestimento').addEventListener('click', function () {
                        document.getElementById('formNovoInvestimento').style.display = 'block';
                        this.style.display = 'none';

                        // Scroll automático para o formulário
                        setTimeout(() => {
                            document.getElementById('formNovoInvestimento').scrollIntoView({
                                behavior: 'smooth',
                                block: 'start'
                            });
                        }, 100);
                    });

                    document.getElementById('btnCancelar').addEventListener('click', function () {
                        document.getElementById('formNovoInvestimento').style.display = 'none';
                        document.getElementById('btnNovoInvestimento').style.display = 'block';
                        // Limpa mensagem de erro ao cancelar
                        document.getElementById('mensagemErro').style.display = 'none';
                    });

                    // Validação do formulário - igual ao saque
                    document.getElementById('formInvestir').addEventListener('submit', function (e) {
                        const valor = parseFloat(document.getElementById('valor').value);
                        const mensagemErro = document.getElementById('mensagemErro');

                        // Limpa mensagem anterior
                        mensagemErro.style.display = 'none';

                        if (isNaN(valor) || valor <= 0) {
                            e.preventDefault();
                            mensagemErro.textContent = 'Digite um valor válido para o investimento.';
                            mensagemErro.style.display = 'block';
                            return;
                        }

                        if (valor > saldoAtual) {
                            e.preventDefault();
                            mensagemErro.textContent = 'Erro: saldo insuficiente para o investimento.';
                            mensagemErro.style.display = 'block';
                            return;
                        }
                    });
                </script>
            </body>

            </html>