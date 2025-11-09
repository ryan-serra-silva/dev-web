package com.mycompany.webapplication.selenium.deposito;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class DepositoValorMenor10Test {

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

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
