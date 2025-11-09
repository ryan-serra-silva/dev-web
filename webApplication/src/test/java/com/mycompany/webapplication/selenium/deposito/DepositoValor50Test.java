package com.mycompany.webapplication.selenium.deposito;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class DepositoValor50Test {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
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

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
