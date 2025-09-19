package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Servlet responsável pelo cadastro de novos usuários
 * 
 * @author ryan
 */
@WebServlet(name = "CadastroUsuario", urlPatterns = { "/CadastroUsuario" })
public class CadastroUsuario extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Encaminha para a página de cadastro
        request.getRequestDispatcher("/views/cadastro.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        UserDAO userDAO = new UserDAO();
        AccountDAO accountDAO = new AccountDAO();

        Users existente = userDAO.getByEmail(email);

        if (existente != null) {
            request.setAttribute("msgError", "E-mail já está em uso. Tente outro.");
        }
          if (!email.contains("@")) {
            request.setAttribute("msgError", "E-mail inválido");
        }
        if (senha.length() < 6) {
            request.setAttribute("msgError", "Senha deve ter pelo menos 6 caracteres");
        }
         if (!senha.matches(".*\\d.*")) { 
        request.setAttribute("msgError", "Senha deve conter pelo menos um número");
        }

    if (!senha.matches(".*[A-Z].*")) { 
        request.setAttribute("msgError", "Senha deve conter pelo menos uma letra maiúscula");
        
    }
        else {
            // Insere o usuário no banco
            Users novo = new Users(nome, email, senha);
            userDAO.insert(novo);

            // Busca o usuário recém cadastrado para pegar o ID
            Users usuarioComId = userDAO.getByEmail(email);

            if (usuarioComId != null) {
                String agencia = "0001";
                String numeroConta = gerarNumeroContaAleatorio();
                BigDecimal saldoInicial = new BigDecimal("0.00");

                Account novaConta = new Account(numeroConta, agencia, saldoInicial, usuarioComId.getId());
                accountDAO.insert(novaConta);

                request.setAttribute("msgSuccess",
                        "Cadastro realizado com sucesso! Conta criada. Redirecionando para login...");
                request.setAttribute("redirecionarLogin", true);
            } else {
                request.setAttribute("msgError", "Erro ao buscar usuário após cadastro.");
            }
        }

        request.getRequestDispatcher("/views/cadastro.jsp")
                .forward(request, response);
    }

    private String gerarNumeroContaAleatorio() {
        int numero = (int) (Math.random() * 900000) + 100000;
        int digito = (int) (Math.random() * 9);
        return numero + "-" + digito;
    }

}
