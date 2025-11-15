package com.mycompany.webapplication.selenium.deposito;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class DepositoTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        // Configurações do Chrome (como no Python)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    public void testDepositoValorImpar() throws InterruptedException {
        driver.get("http://localhost:8080/Login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        emailInput.sendKeys("ryan@gmail.com");
        senhaInput.sendKeys("CD=hj0=r");
        senhaInput.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
        System.out.println("✅ Login realizado com sucesso! Home carregada.");

        driver.get("http://localhost:8080/Depositar");
        Thread.sleep(1000);

        WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
        valorInput.clear();
        valorInput.sendKeys("11");

        WebElement botaoSubmit = driver.findElement(By.cssSelector("#formDeposito button[type='submit']"));
        botaoSubmit.click();

        WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
        String mensagemTexto = mensagemDiv.getText().trim().replace("\n", "");

        if (mensagemTexto.contains("Erro: valor não pode ser impar")) {
            System.out.println("✅ Teste de depósito (valor 11) passou!");
        } else {
            Assertions.fail("❌ Teste falhou. Mensagem exibida: " + mensagemTexto);
        }
    }
    @Test
    public void testDepositoMultiploDe7() {

        driver.get("http://localhost:8080/Login");
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        emailInput.sendKeys("ryan@gmail.com");
        senhaInput.sendKeys("CD=hj0=r");
        senhaInput.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
        System.out.println("✅ Login realizado com sucesso! Home carregada.");

        driver.get("http://localhost:8080/Depositar");

        WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
        valorInput.clear();
        valorInput.sendKeys("14");

        WebElement botaoSubmit = driver.findElement(By.cssSelector("#formDeposito button[type='submit']"));
        botaoSubmit.click();

        WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
        String mensagemTexto = mensagemDiv.getText().trim().replace("\n", "");

        if (mensagemTexto.contains("Erro: valor não pode ser múltiplo de 7")) {
            System.out.println("✅ Teste de depósito 14 passou!");
        } else {
            Assertions.fail("❌ Teste falhou. Mensagem exibida: " + mensagemTexto);
        }
    }

    @Test
    public void testDepositoMultiploDe11() {
        driver.get("http://localhost:8080/Login");
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        emailInput.sendKeys("ryan@gmail.com");
        senhaInput.sendKeys("CD=hj0=r");
        senhaInput.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
        System.out.println("✅ Login realizado com sucesso! Home carregada.");
        driver.get("http://localhost:8080/Depositar");

        WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
        valorInput.clear();
        valorInput.sendKeys("22");

        WebElement botaoSubmit = driver.findElement(By.cssSelector("#formDeposito button[type='submit']"));
        botaoSubmit.click();

        WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
        String mensagemTexto = mensagemDiv.getText().trim().replace("\n", "");

        if (mensagemTexto.contains("Erro: valor não pode ser múltiplo de 11")) {
            System.out.println("✅ Teste de depósito 22 passou!");
        } else {
            Assertions.fail("❌ Teste falhou. Mensagem exibida: " + mensagemTexto);
        }
    }

    @Test
    public void testValor30InseridoCorretamente() throws InterruptedException {

        driver.get("http://localhost:8080/Login");
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        emailInput.sendKeys("ryan@gmail.com");
        senhaInput.sendKeys("CD=hj0=r");
        senhaInput.submit();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));

        driver.get("http://localhost:8080/Depositar");

        WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
        valorInput.clear();
        valorInput.sendKeys("30");

        String valorAtual = valorInput.getAttribute("value");

        if (valorAtual.equals("30")) {
            System.out.println("Valor 30 inserido corretamente!");
        } else {
            Assertions.fail("Valor inserido incorreto: " + valorAtual);
        }

        Thread.sleep(3000);
    }
    @Test
    public void testDepositoSucessoComValor50() throws InterruptedException {

        try {
            // 1. Login
            driver.get("http://localhost:8080/Login");

            WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

            emailInput.sendKeys("ryan@gmail.com");
            senhaInput.sendKeys("CD=hj0=r");
            senhaInput.sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
            System.out.println("Login realizado com sucesso! Home carregada.");

            // 2. Ir para Depositar
            driver.get("http://localhost:8080/Depositar");
            Thread.sleep(1000);

            WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
            valorInput.clear();
            valorInput.sendKeys("50");

            WebElement botaoSubmit = driver.findElement(By.cssSelector("#formDeposito button[type='submit']"));
            botaoSubmit.click();

            WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
            String mensagemTexto = mensagemDiv.getText().trim().replace("\n","");

            Thread.sleep(5000);

            if (mensagemTexto.contains("Depósito realizado com sucesso")) {
                System.out.println("Teste de depósito passou com sucesso!");
            } else {
                Assertions.fail("Depósito não realizado. Mensagem: " + mensagemTexto);
            }

        } catch (Exception e) {
            System.out.println("Erro no teste: " + e.getMessage());
            String pageSource = driver.getPageSource();
            if (pageSource.length() > 1000) {
                System.out.println("HTML atual da página (primeiros 1000 caracteres):");
                System.out.println(pageSource.substring(0, 1000));
            } else {
                System.out.println("HTML atual da página:");
                System.out.println(pageSource);
            }
            Assertions.fail("Erro no teste. Exceção capturada: " + e.getMessage());
        }
    }

    @Test
    public void testDepositoMaiorQue5000() throws InterruptedException {

        // 1. Acessa página de login
        driver.get("http://localhost:8080/Login");

        // 2. Localiza campos de login
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        // 3. Realiza login
        emailInput.sendKeys("ryan@gmail.com");
        senhaInput.sendKeys("CD=hj0=r");
        senhaInput.sendKeys(Keys.ENTER);

        // 4. Aguarda carregamento da home
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
        System.out.println("Login realizado com sucesso! Home carregada.");

        // 5. Vai para página de depósito
        driver.get("http://localhost:8080/Depositar");
        Thread.sleep(1000);

        // 6. Preenche valor maior que 5000
        WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
        valorInput.clear();
        valorInput.sendKeys("5001");

        // 7. Clica no botão de enviar
        WebElement botaoSubmit = driver.findElement(By.cssSelector("#formDeposito button[type='submit']"));
        botaoSubmit.click();

        // 8. Aguarda mensagem (visível)
        WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
        String mensagemTexto = mensagemDiv.getText().trim().replace("\n", "");

        // 9. Validação
        if (mensagemTexto.contains("Erro: valor maior que o depósito máximo de R$5000")) {
            System.out.println("Teste de depósito 5001 passou!");
        } else {
            Assertions.fail("Teste falhou. Mensagem exibida: " + mensagemTexto);
        }
    }

    @Test
    public void testDepositoMenorQue10() throws InterruptedException {

        // 1. Acessa a página de login
        driver.get("http://localhost:8080/Login");

        // 2. Localiza campos de login
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        // 3. Realiza login
        emailInput.sendKeys("ryan@gmail.com");
        senhaInput.sendKeys("CD=hj0=r");
        senhaInput.sendKeys(Keys.ENTER);

        // 4. Aguarda carregar home
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
        System.out.println("Login realizado com sucesso! Home carregada.");

        // 5. Vai para página de depósito
        driver.get("http://localhost:8080/Depositar");
        Thread.sleep(1000);

        // 6. Preenche valor menor que 10
        WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
        valorInput.clear();
        valorInput.sendKeys("9");

        // 7. Submete
        WebElement botaoSubmit = driver.findElement(By.cssSelector("#formDeposito button[type='submit']"));
        botaoSubmit.click();

        // 8. Aguarda mensagem visível
        WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
        String mensagemTexto = mensagemDiv.getText().trim().replace("\n","");

        // 9. Validação
        if (mensagemTexto.contains("Erro: valor menor que o depósito mínimo de R$10")) {
            System.out.println("Teste de depósito menor que 10 passou!");
        } else {
            Assertions.fail("Teste falhou. Mensagem exibida: " + mensagemTexto);
        }
    }

    @Test
    public void testCarregamentoPaginaDeposito() throws InterruptedException {

        // 1. Acessa Login
        driver.get("http://localhost:8080/Login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        // 2. Login
        emailInput.sendKeys("ryan@gmail.com");
        senhaInput.sendKeys("CD=hj0=r");
        senhaInput.submit();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
        System.out.println("Login realizado com sucesso! Home carregada.");

        // 3. Acessa página de depósito
        driver.get("http://localhost:8080/Depositar");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("valor")));
        System.out.println("Tela de Depósito carregada com sucesso!");

        Thread.sleep(3000);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
