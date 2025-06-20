<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login Administrativo</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .login-container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            width: 300px;
        }

        h2 {
            text-align: center;
        }

        .error {
            color: red;
            text-align: center;
            margin-bottom: 10px;
        }

        label, input {
            display: block;
            width: 100%;
            margin-bottom: 10px;
        }

        input[type="submit"] {
            background-color: #007BFF;
            color: white;
            border: none;
            padding: 10px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<div class="login-container">
    <h2>Login</h2>

    <!-- Exibir mensagem de erro -->
    <c:if test="${not empty msgError}">
        <div class="error">${msgError}</div>
    </c:if>

    <form method="post" action="LoginVerify">
        <label for="email">Email:</label>
        <input type="text" name="email" id="email" required />

        <label for="senha">Senha:</label>
        <input type="password" name="senha" id="senha" required />

        <input type="submit" value="Entrar" />
    </form>
</div>

</body>
</html>
