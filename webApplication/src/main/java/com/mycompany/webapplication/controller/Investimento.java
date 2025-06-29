package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.AccountDAO;
import com.mycompany.webapplication.model.InvestmentDAO;
import com.mycompany.webapplication.model.InvestmentProductDAO;
import com.mycompany.webapplication.entity.InvestmentType;
import com.mycompany.webapplication.entity.InvestmentProduct;
import com.mycompany.webapplication.entity.Investment;
import com.mycompany.webapplication.entity.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "Investir", urlPatterns = {"/Investir"})
public class Investimento extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String tipo = request.getParameter("tipo");
            BigDecimal valor = new BigDecimal(request.getParameter("valor"));
            int tempoMeses = Integer.parseInt(request.getParameter("tempo"));

            HttpSession session = request.getSession();
            Users usuario = (Users) session.getAttribute("usuario");
            Long userId = usuario.getId();

            AccountDAO accountDAO = new AccountDAO();
            InvestmentProductDAO productDAO = new InvestmentProductDAO();
            InvestmentDAO investmentDAO = new InvestmentDAO();

            Account conta = accountDAO.getByUserId(userId);

            if (conta != null && valor.compareTo(BigDecimal.ZERO) > 0 && tipo != null && !tipo.isEmpty()) {
                if (conta.getBalance().compareTo(valor) >= 0) {

                    // Subtrai o valor da conta
                    conta.setBalance(conta.getBalance().subtract(valor));
                    accountDAO.update(conta);

                    // Busca o produto
                    InvestmentType tipoEnum = InvestmentType.valueOf(tipo.toUpperCase());
                    InvestmentProduct produto = productDAO.getByType(tipoEnum);

                    if (produto == null) {
                        request.setAttribute("mensagem", "Produto de investimento não encontrado.");
                        request.getRequestDispatcher("/views/investir.jsp").forward(request, response);
                        return;
                    }

                    // Cria o investimento
                    Investment investimento = new Investment();
                    investimento.setAmount(valor);
                    investimento.setStartDate(LocalDateTime.now().toLocalDate());
                    investimento.setEndDate(LocalDateTime.now().plusMonths(tempoMeses).toLocalDate());
                    investimento.setAccount(conta);
                    investimento.setInvestmentProduct(produto);
                    investmentDAO.insert(investimento);

                    // Calcula retorno
                    BigDecimal taxa = produto.getReturnRate();
                    BigDecimal rendimento = valor.multiply(taxa).multiply(BigDecimal.valueOf(tempoMeses));
                    BigDecimal valorFinal = valor.add(rendimento);

                    // Envia para a página de detalhes
                    request.setAttribute("investimento", investimento);
                    request.setAttribute("produto", produto);
                    request.setAttribute("tempo", tempoMeses);
                    request.setAttribute("valorFinal", valorFinal);
                    request.setAttribute("usuario", usuario);

                    request.getRequestDispatcher("/views/detalheInvestimento.jsp").forward(request, response);
                    return;

                } else {
                    request.setAttribute("mensagem", "Erro: saldo insuficiente.");
                }
            } else {
                request.setAttribute("mensagem", "Erro: dados inválidos ou conta inexistente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensagem", "Erro ao processar investimento.");
        }

        request.getRequestDispatcher("/views/investir.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Users usuario = (Users) session.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        AccountDAO accountDAO = new AccountDAO();
        Account conta = accountDAO.getByUserId(usuario.getId());

        request.setAttribute("usuario", usuario);
        request.setAttribute("conta", conta);

        request.getRequestDispatcher("/views/investir.jsp").forward(request, response);
    }
}
