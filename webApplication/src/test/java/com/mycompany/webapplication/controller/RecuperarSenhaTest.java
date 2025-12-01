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

import java.util.List;

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

    // --- NOVO TESTE: Cobre o método doGet ---
    @Test
    void doGet_encaminhaParaPaginaCorreta() throws Exception {
        // Arrange
        when(request.getRequestDispatcher("/views/recuperarSenha.jsp")).thenReturn(dispatcher);

        // Act
        recuperarSenhaServlet.doGet(request, response);

        // Assert
        verify(request).getRequestDispatcher("/views/recuperarSenha.jsp");
        verify(dispatcher).forward(request, response);
    }

    // --- NOVO TESTE: Cobre o primeiro IF (usuário null) ---
    @Test
    void doPost_usuarioNaoEncontrado_exibeErro() throws Exception {
        // Arrange
        when(request.getParameter("email")).thenReturn("inexistente@email.com");
        // Não precisamos configurar senhas aqui, pois ele deve falhar antes de checar as senhas
        when(request.getRequestDispatcher("/views/recuperarSenha.jsp")).thenReturn(dispatcher);

        // Mockamos o DAO para retornar NULL ao buscar o email
        try (MockedConstruction<JDBC> mockJdbc = mockConstruction(JDBC.class);
             MockedConstruction<UserDAO> mockDao = mockConstruction(UserDAO.class,
                     (mock, context) -> when(mock.getByEmail("inexistente@email.com")).thenReturn(null))) {

            // Act
            recuperarSenhaServlet.doPost(request, response);

            // Assert
            verify(request).setAttribute("msgError", "E-mail não encontrado em nosso sistema.");
            // Garante que NÃO tentou atualizar senha
            UserDAO daoCriado = mockDao.constructed().get(0);
            verify(daoCriado, never()).updatePasswordByEmail(anyString(), anyString());
            verify(dispatcher).forward(request, response);
        }
    }

    // --- TESTE ORIGINAL 1 MELHORADO: Senhas não conferem ---
    @Test
    void doPost_senhasNaoConferem_exibeErro() throws Exception {
        // Arrange
        when(request.getParameter("email")).thenReturn("teste@email.com");
        when(request.getParameter("novaSenha")).thenReturn("Senha123!");
        when(request.getParameter("confirmSenha")).thenReturn("SenhaDiferente!");
        when(request.getRequestDispatcher("/views/recuperarSenha.jsp")).thenReturn(dispatcher);

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

    // --- TESTE ORIGINAL 2: Senha Inválida (UC) ---
    @Test
    void doPost_senhaInvalida_exibeErroDoUseCase() throws Exception {
        // Arrange
        String senhaFraca = "123";
        when(request.getParameter("email")).thenReturn("teste@email.com");
        when(request.getParameter("novaSenha")).thenReturn(senhaFraca);
        when(request.getParameter("confirmSenha")).thenReturn(senhaFraca);
        when(request.getRequestDispatcher("/views/recuperarSenha.jsp")).thenReturn(dispatcher);

        try (MockedConstruction<JDBC> mockJdbc = mockConstruction(JDBC.class);
             MockedConstruction<UserDAO> mockDao = mockConstruction(UserDAO.class,
                     (mock, context) -> when(mock.getByEmail("teste@email.com")).thenReturn(new Users()));
             MockedStatic<RecuperarSenhaUC> mockUC = mockStatic(RecuperarSenhaUC.class)) {

            mockUC.when(() -> RecuperarSenhaUC.validarSenha(senhaFraca, request)).thenReturn(false);

            // Act
            recuperarSenhaServlet.doPost(request, response);

            // Assert
            mockUC.verify(() -> RecuperarSenhaUC.validarSenha(senhaFraca, request));
            verify(dispatcher).forward(request, response);
        }
    }

    // --- TESTE ORIGINAL 3 MELHORADO: Caminho Feliz ---
    @Test
    void doPost_tudoValido_atualizaSenha() throws Exception {
        // Arrange
        String email = "teste@email.com";
        String senhaForte = "SenhaForte123!";

        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("novaSenha")).thenReturn(senhaForte);
        when(request.getParameter("confirmSenha")).thenReturn(senhaForte);
        when(request.getRequestDispatcher("/views/recuperarSenha.jsp")).thenReturn(dispatcher);

        try (MockedConstruction<JDBC> mockJdbc = mockConstruction(JDBC.class);
             MockedConstruction<UserDAO> mockDao = mockConstruction(UserDAO.class, (mock, context) -> {
                 when(mock.getByEmail(email)).thenReturn(new Users());
             });
             MockedStatic<RecuperarSenhaUC> mockUC = mockStatic(RecuperarSenhaUC.class)) {

            mockUC.when(() -> RecuperarSenhaUC.validarSenha(senhaForte, request)).thenReturn(true);

            // Act
            recuperarSenhaServlet.doPost(request, response);

            // Assert
            verify(request).setAttribute("msgSuccess", "Senha atualizada com sucesso! Faça login com a nova senha.");
            verify(dispatcher).forward(request, response);

            // --- MELHORIA CRÍTICA PARA MUTAÇÃO ---
            // Recuperamos a instância do Mock que foi criada DENTRO do método doPost
            List<UserDAO> daosCriados = mockDao.constructed();
            // Verifica se o método de update foi de fato chamado nessa instância
            verify(daosCriados.get(0)).updatePasswordByEmail(email, senhaForte);
        }
    }
}
