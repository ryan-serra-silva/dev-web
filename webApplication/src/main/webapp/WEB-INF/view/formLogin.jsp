<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="#">
    <title>School</title>
    <link href="http://localhost:8080/webApplication/views/bootstrap/bootstrap.min.css" rel="stylesheet">
</head>
<body class="d-flex h-100 bg-dark text-light">
    <div class="container">
        <jsp:include page="../comum/menu.jsp" />
        <div class="row justify-content-center">
            <div class="col-12 col-md-8 col-lg-6 mt-5">

                <h3>Faça seu login administrador!</h3>

                <%
                    String msgError = (String) request.getAttribute("msgError");
                    if ((msgError != null) && (!msgError.isEmpty())) {
                %>
                <div class="alert alert-danger" role="alert">
                    <%= msgError %>
                </div>
                <% } %>

                <form action="/aplicacaoMVC/LoginVerify" method="POST">
                    <div class="mb-3">
                        <label for="email" class="form-label">E-mail</label>
                        <input type="email" name="email" class="form-control" required placeholder="exemplo@email.com" maxlength="255">
                    </div>
                    <div class="mb-3">
                        <label for="senha" class="form-label">Senha</label>
                        <input type="password" name="senha" class="form-control" maxlength="255" placeholder="Digite sua senha" required>
                    </div>
                    <div class="d-flex justify-content-between">
                        <a href="/aplicacaoMVC/LoginController" class="btn btn-secondary">Voltar</a>
                        <input type="submit" value="Entrar" class="btn btn-primary">
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script src="http://localhost:8080/aplicacaoMVC/views/bootstrap/bootstrap.bundle.min.js"></script>
</body>
</html>
