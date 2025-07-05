<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="shortcut icon" href="#" />
    <title>Cadastro - Bank</title>

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

      .login-container {
        background-color: #1f1f1f;
        padding: 40px;
        border-radius: 10px;
        width: 100%;
        max-width: 400px;
        box-shadow: 0 0 20px rgba(255, 255, 255, 0.1);
        text-align: center;
      }

      h1 {
        margin-bottom: 30px;
        font-size: 32px;
        font-weight: bold;
      }

      .form-group {
        margin-bottom: 20px;
        text-align: left;
      }

      label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
      }

      input[type="text"],
      input[type="email"],
      input[type="password"] {
        width: 100%;
        padding: 10px;
        border: none;
        border-radius: 5px;
        box-sizing: border-box;
      }

      .btn-submit {
        width: 100%;
        padding: 12px;
        background-color: #28a745;
        border: none;
        border-radius: 5px;
        color: white;
        font-size: 16px;
        cursor: pointer;
        margin-top: 10px;
      }

      .btn-submit:hover {
        background-color: #218838;
      }

      .error-msg {
        background-color: #dc3545;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 20px;
        color: white;
        font-weight: bold;
      }

      .success-msg {
        background-color: #28a745;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 20px;
        color: white;
        font-weight: bold;
      }

      .login-link {
        margin-top: 20px;
        font-size: 14px;
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
    <div class="login-container">
      <h1>Cadastro</h1>

      <%
        String msgError = (String) request.getAttribute("msgError");
        String msgSuccess = (String) request.getAttribute("msgSuccess");
        if (msgError != null) {
      %>
        <div class="error-msg"><%= msgError %></div>
      <%
        } else if (msgSuccess != null) {
      %>
        <div class="success-msg"><%= msgSuccess %></div>
      <%
        }
      %>

      <form action="/webApplication/CadastroUsuario" method="post">
        <div class="form-group">
          <label for="nome">Nome</label>
          <input type="text" id="nome" name="nome" required />
        </div>

        <div class="form-group">
          <label for="email">Email</label>
          <input type="email" id="email" name="email" required />
        </div>

        <div class="form-group">
          <label for="senha">Senha</label>
          <input type="password" id="senha" name="senha" required />
        </div>

        <button type="submit" class="btn-submit">Cadastrar</button>
      </form>

      <div class="login-link">
        Já tem uma conta? <a href="/webApplication/Login">Faça login aqui</a>
      </div>
    </div>
  </body>
</html>
