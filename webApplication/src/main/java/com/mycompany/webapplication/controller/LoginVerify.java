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
        RequestDispatcher rd = request.getRequestDispatcher("/views/login.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
            request.setAttribute("msgError", "Preencha todos os campos");
            RequestDispatcher rd = request.getRequestDispatcher("/views/login.jsp");
            rd.forward(request, response);
        } 
        if (!email.contains("@")) {
            request.setAttribute("msgError", "E-mail inválido");
            RequestDispatcher rd = request.getRequestDispatcher("/views/login.jsp");
            rd.forward(request, response);
           return;
        }
        if (senha.length() < 6) {
            request.setAttribute("msgError", "Senha deve ter pelo menos 6 caracteres");
            RequestDispatcher rd = request.getRequestDispatcher("/views/login.jsp");
            rd.forward(request, response);
            return;
        }
         if (!senha.matches(".*\\d.*")) { 
        request.setAttribute("msgError", "Senha deve conter pelo menos um número");
        RequestDispatcher rd = request.getRequestDispatcher("/views/login.jsp");
        rd.forward(request, response);
        return;
    }

    if (!senha.matches(".*[A-Z].*")) { 
        request.setAttribute("msgError", "Senha deve conter pelo menos uma letra maiúscula");
        RequestDispatcher rd = request.getRequestDispatcher("/views/login.jsp");
        rd.forward(request, response);
        return;
    }
        else {
                UserDAO userDAO = new UserDAO();
                Users usuario = userDAO.login(email, senha);
                 
                
                    if (usuario == null) {
                        request.setAttribute("msgError", "Credenciais inválidas");
                        RequestDispatcher rd = request.getRequestDispatcher("/views/login.jsp");
                        rd.forward(request, response);
                    } else {
                        HttpSession session = request.getSession();
                        session.setAttribute("usuario", usuario);
                        response.sendRedirect("Home");
                    }
        }
    }
}
