package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginVerify", urlPatterns = {"/LoginVerify"})
public class LoginVerify extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/views/autenticacao/formLoginAdm.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        RequestDispatcher rd;

        // pegando os parâmetros do request
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
            request.setAttribute("msgError", "Preencha todos os campos");
            rd = request.getRequestDispatcher("/views/autenticacao/formLoginAdm.jsp");
            rd.forward(request, response);
        } else {
            try {
                UserDAO userDAO = new UserDAO();
                Users usuario = userDAO.getUserByEmail(email);

                if (usuario == null || usuario.getId() == 0) {
                    request.setAttribute("msgError", "Usuário não cadastrado");
                    rd = request.getRequestDispatcher("/views/autenticacao/formLoginAdm.jsp");
                    rd.forward(request, response);
                } else if (!usuario.getpassword().equals(senha)) {
                    request.setAttribute("msgError", "Senha incorreta");
                    rd = request.getRequestDispatcher("/views/autenticacao/formLoginAdm.jsp");
                    rd.forward(request, response);
                } else {
                    HttpSession session = request.getSession();
                    session.setAttribute("usuario", usuario);
                    response.sendRedirect("/aplicacaoMVC/user/dashboard"); // ajuste conforme sua rota
                }
            } catch (ServletException | IOException ex) {
                System.out.println("Erro ao autenticar: " + ex.getMessage());
                throw new RuntimeException("Erro ao tentar fazer login");
            }
        }
    }
}
