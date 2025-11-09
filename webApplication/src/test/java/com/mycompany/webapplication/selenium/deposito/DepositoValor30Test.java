package com.mycompany.webapplication.selenium.deposito;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class DepositoValor30Test {

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
    public void testValor30InseridoCorretamente() throws InterruptedException {

        // 1. Login
        driver.get("http://localhost:8080/Login");
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        emailInput.sendKeys("ryan@gmail.com");
        senhaInput.sendKeys("CD=hj0=r");
        senhaInput.submit();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));

        // 2. Ir para Depositar
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

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
