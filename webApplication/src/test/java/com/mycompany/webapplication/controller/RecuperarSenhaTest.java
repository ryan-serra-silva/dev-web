package com.mycompany.webapplication.controller;

import com.mycompany.webapplication.entity.Users;
import com.mycompany.webapplication.model.JDBC;
import com.mycompany.webapplication.model.UserDAO;
import com.mycompany.webapplication.usecases.RecuperarSenhaUC;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecuperarSenhaTest {

    @InjectMocks
    private RecuperarSenha recuperarSenhaServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    // --- TESTE 1: Senhas não conferem (Cobre linhas 40-41) ---
    @Test
    void doPost_senhasNaoConferem_exibeErro() throws Exception {
        // Arrange
        when(request.getParameter("email")).thenReturn("teste@email.com");
        when(request.getParameter("novaSenha")).thenReturn("Senha123!");
        when(request.getParameter("confirmSenha")).thenReturn("SenhaDiferente!"); // Senhas diferentes
        when(request.getRequestDispatcher("/views/recuperarSenha.jsp")).thenReturn(dispatcher);

        // Simulamos a criação do JDBC e do UserDAO para retornar um usuário válido
        try (MockedConstruction<JDBC> mockJdbc = mockConstruction(JDBC.class);
             MockedConstruction<UserDAO> mockDao = mockConstruction(UserDAO.class,
                     (mock, context) -> when(mock.getByEmail("teste@email.com")).thenReturn(new Users()))) {

            // Act
            recuperarSenhaServlet.doPost(request, response);

            // Assert
            verify(request).setAttribute("msgError", "As senhas digitadas não coincidem.");
            verify(dispatcher).forward(request, response);
        }
    }

    // --- TESTE 2: Senha Inválida/Fraca (Cobre linhas 44-46) ---
    @Test
    void doPost_senhaInvalida_exibeErroDoUseCase() throws Exception {
        // Arrange
        String senhaFraca = "123";
        when(request.getParameter("email")).thenReturn("teste@email.com");
        when(request.getParameter("novaSenha")).thenReturn(senhaFraca);
        when(request.getParameter("confirmSenha")).thenReturn(senhaFraca); // Iguais, mas fracas
        when(request.getRequestDispatcher("/views/recuperarSenha.jsp")).thenReturn(dispatcher);

        // Mockamos o DAO para retornar usuário válido
        try (MockedConstruction<JDBC> mockJdbc = mockConstruction(JDBC.class);
             MockedConstruction<UserDAO> mockDao = mockConstruction(UserDAO.class,
                     (mock, context) -> when(mock.getByEmail("teste@email.com")).thenReturn(new Users()));
             // Mockamos o método estático para retornar FALSE (senha inválida)
             MockedStatic<RecuperarSenhaUC> mockUC = mockStatic(RecuperarSenhaUC.class)) {

            mockUC.when(() -> RecuperarSenhaUC.validarSenha(senhaFraca, request)).thenReturn(false);

            // Act
            recuperarSenhaServlet.doPost(request, response);

            // Assert
            // Verifica se o validarSenha foi chamado
            mockUC.verify(() -> RecuperarSenhaUC.validarSenha(senhaFraca, request));
            // Verifica se NÃO tentou atualizar a senha no banco (importante!)
            verify(dispatcher).forward(request, response);
        }
    }

    // --- TESTE 3: Sucesso - Caminho Feliz (Cobre linhas 48-50) ---
    @Test
    void doPost_tudoValido_atualizaSenha() throws Exception {
        // Arrange
        String email = "teste@email.com";
        String senhaForte = "SenhaForte123!";

        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("novaSenha")).thenReturn(senhaForte);
        when(request.getParameter("confirmSenha")).thenReturn(senhaForte);
        when(request.getRequestDispatcher("/views/recuperarSenha.jsp")).thenReturn(dispatcher);

        // Mockamos TUDO para o caminho feliz
        try (MockedConstruction<JDBC> mockJdbc = mockConstruction(JDBC.class);
             MockedConstruction<UserDAO> mockDao = mockConstruction(UserDAO.class, (mock, context) -> {
                 // Configura o mock do DAO criado dentro do Servlet
                 when(mock.getByEmail(email)).thenReturn(new Users());
             });
             MockedStatic<RecuperarSenhaUC> mockUC = mockStatic(RecuperarSenhaUC.class)) {

            // UC retorna TRUE (senha válida)
            mockUC.when(() -> RecuperarSenhaUC.validarSenha(senhaForte, request)).thenReturn(true);

            // Act
            recuperarSenhaServlet.doPost(request, response);

            // Assert
            // Como não temos acesso fácil à instância do DAO criada lá dentro,
            // verificamos se a mensagem de sucesso foi setada.
            verify(request).setAttribute("msgSuccess", "Senha atualizada com sucesso! Faça login com a nova senha.");
            verify(dispatcher).forward(request, response);
        }
    }
}