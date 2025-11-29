//package com.mycompany.webapplication.selenium.recuperarsenha;
//
//import java.time.Duration;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//public class RecuperarSenhaSeleniumTest {
//    private WebDriver driver;
//    private WebDriverWait wait;
//
//    @BeforeEach
//    public void setUp() {
//        // Configurações do Chrome
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--start-maximized");
//        options.addArguments("--disable-notifications");
//        // options.addArguments("--headless"); // Descomente se não quiser ver o navegador abrindo
//
//        driver = new ChromeDriver(options);
//        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//    }
//
//    @Test
//    public void deveAtualizarSenhaComSucesso() throws InterruptedException {
//        // 1. Acessa a página de Recuperar Senha
//        driver.get("http://localhost:8080/RecuperarSenha");
//
//        // 2. Aguarda e encontra os elementos
//        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//        WebElement novaSenhaInput = driver.findElement(By.id("novaSenha"));
//        WebElement confirmSenhaInput = driver.findElement(By.id("confirmSenha"));
//
//        // 3. Preenche os dados conforme seu pedido
//        emailInput.sendKeys("teste11@teste.com");
//        novaSenhaInput.sendKeys("Abc123@b");
//        confirmSenhaInput.sendKeys("Abc123@b");
//
//        // 4. Clica no botão de atualizar
//        WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//        botaoSubmit.click();
//
//        // 5. Validação: Procura pela mensagem de sucesso
//        // Baseado no seu HTML do Postman, a classe é "message-box success-msg"
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("success-msg")));
//        WebElement msgElement = driver.findElement(By.className("success-msg"));
//
//        String mensagemTexto = msgElement.getText().trim();
//
//        // Verifica se a mensagem é a esperada
//        Assertions.assertEquals("Senha atualizada com sucesso! Faça login com a nova senha.", mensagemTexto);
//    }
//
//    @Test
//    public void deveRetornarErroQuandoSenhasNaoConferem() {
//        driver.get("http://localhost:8080/RecuperarSenha");
//
//        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//        WebElement novaSenhaInput = driver.findElement(By.id("novaSenha"));
//        WebElement confirmSenhaInput = driver.findElement(By.id("confirmSenha"));
//
//        // Preenche com senhas diferentes
//        emailInput.sendKeys("teste11@teste.com");
//        novaSenhaInput.sendKeys("Abc123@b");
//        confirmSenhaInput.sendKeys("SenhaErrada123"); // Diferente
//
//        WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//        botaoSubmit.click();
//
//        // Validação de erro genérica no corpo da página
//        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "As senhas digitadas não coincidem."));
//
//        String pageText = driver.findElement(By.tagName("body")).getText();
//        Assertions.assertTrue(pageText.contains("As senhas digitadas não coincidem."));
//    }
//
//    @AfterEach
//    public void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//}