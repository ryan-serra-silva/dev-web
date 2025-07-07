<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recuperar Senha - Meu Banco</title>
    <style>
      body {
        background-color: #121212;
        color: #ffffff;
        font-family: Arial, sans-serif;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        margin: 0;
      }

      .recupera-container {
        background-color: #1f1f1f;
        padding: 40px;
        border-radius: 10px;
        width: 100%;
        max-width: 400px;
        box-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
        text-align: center;
      }

      h1 {
        margin-bottom: 10px;
        font-size: 28px;
        font-weight: bold;
      }
      
      p {
        color: #b3b3b3;
        margin-bottom: 25px;
      }

      .form-group {
        margin-bottom: 20px;
        text-align: left;
      }

      label {
        display: block;
        margin-bottom: 8px;
        font-weight: bold;
        color: #e0e0e0;
      }

      input[type="email"] {
        width: 100%;
        padding: 12px;
        border: 1px solid #333;
        background-color: #2a2a2a;
        color: #ffffff;
        border-radius: 5px;
        box-sizing: border-box;
        transition: border-color 0.3s, box-shadow 0.3s;
      }
      
      input[type="email"]:focus {
        outline: none;
        border-color: #007bff;
        box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.2);
      }

      .btn-submit {
        width: 100%;
        padding: 12px;
        background-color: #007bff;
        border: none;
        border-radius: 5px;
        color: white;
        font-size: 16px;
        font-weight: bold;
        cursor: pointer;
        margin-top: 10px;
        transition: background-color 0.3s;
      }

      .btn-submit:hover {
        background-color: #0056b3;
      }
      
      .message-box {
        padding: 12px;
        border-radius: 5px;
        margin-bottom: 20px;
        color: white;
        font-weight: bold;
        text-align: center;
      }

      .success-msg {
        background-color: #28a745;
      }

      .error-msg {
        background-color: #dc3545;
      }
      
      .login-link {
        text-align: center; 
        margin-top: 20px;
      }
      
      .login-link a {
        color: #007bff; 
        text-decoration: none;
        font-weight: bold;
      }
      
      .login-link a:hover {
        text-decoration: underline;
      }

    </style>
</head>
<body>
    <div class="recupera-container">
        <form action="${pageContext.request.contextPath}/RecuperarSenha" method="POST">
            <h1>Recuperar Senha</h1>
            <p>Digite seu e-mail para receber uma nova senha.</p>

            <%-- Exibe mensagens de erro ou sucesso --%>
            <c:if test="${not empty msgError}">
                <div class="message-box error-msg">${msgError}</div>
            </c:if>
            <c:if test="${not empty msgSuccess}">
                <div class="message-box success-msg">${msgSuccess}</div>
            </c:if>

            <div class="form-group">
                <label for="email">E-mail</label>
                <input type="email" id="email" name="email" required placeholder="Digite seu e-mail de cadastro">
            </div>

            <button type="submit" class="btn-submit">Enviar</button>

            <div class="login-link">
                 <a href="/webApplication/Login">Faça login aqui</a>
            </div>
        </form>
    </div>
</body>
</html>