package com.mycompany.webapplication.selenium.deposito;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class FazerDepositoPageLoadTest {

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
