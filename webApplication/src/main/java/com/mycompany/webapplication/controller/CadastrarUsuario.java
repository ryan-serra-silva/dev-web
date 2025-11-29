package com.mycompany.webapplication.controller;

import java.io.IOException;
import java.math.BigDecimal;

import com.mycompany.webapplication.entity.Account;
import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.JDBC;
import com.mycompany.webapplication.model.UserDAO;
import com.mycompany.webapplication.usecases.CadastrarUsuarioUC;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet responsável pelo cadastro de novos usuários
 */
@WebServlet(name = "CadastroUsuario", urlPatterns = { "/CadastroUsuario" })
public class CadastrarUsuario extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/views/cadastro.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        JDBC jdbc = new JDBC();
        UserDAO userDAO = new UserDAO(jdbc);
        AccountDAO accountDAO = new AccountDAO();

        String msgErro = CadastrarUsuarioUC.validarUsuario(nome, email, senha, userDAO);

        if (msgErro != null) {
            request.setAttribute("msgError", msgErro);

        } else {

            Users novo = new Users(nome, email, senha);
            userDAO.insert(novo);

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
        return String.valueOf(numero);
    }
}
