<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="shortcut icon" href="#" />
    <title>Login - Bank</title>

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
        box-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
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
        background-color: #007bff;
        border: none;
        border-radius: 5px;
        color: white;
        font-size: 16px;
        cursor: pointer;
        margin-top: 10px;
      }

      .btn-submit:hover {
        background-color: #0056b3;
      }

      .error-msg {
        background-color: #dc3545;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 20px;
        color: white;
        font-weight: bold;
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
    <div class="login-container">
      <h1>Login</h1>

      <%
        String msgError = (String) request.getAttribute("msgError");
        if (msgError != null) {
      %>
        <div class="error-msg"><%= msgError %></div>
      <%
        }
      %>

      <form action="/webApplication/LoginVerify" method="post">
        <div class="form-group">
          <label for="email">Email</label>
          <input type="email" id="email" name="email" required />
        </div>

        <div class="form-group">
          <label for="senha">Senha</label>
          <input type="password" id="senha" name="senha" required />
        </div>
          
          
            <div class="login-link">
              Esqueceu sua senha? <a href="RecuperarSenha">Recupere-a aqui!</a>
             </div>

        <button type="submit" class="btn-submit">Entrar</button>
      </form>
    </div>
  </body>
</html>
