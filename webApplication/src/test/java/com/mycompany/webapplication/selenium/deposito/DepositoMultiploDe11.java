package com.mycompany.webapplication.selenium.deposito;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class DepositoMultiploDe11 {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    public void testDepositoMultiploDe11() {
        // 1. Login
        driver.get("http://localhost:8080/Login");
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        emailInput.sendKeys("ryan@gmail.com");
        senhaInput.sendKeys("CD=hj0=r");
        senhaInput.sendKeys(Keys.ENTER);

        // 2. Aguarda home carregar
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
        System.out.println("✅ Login realizado com sucesso! Home carregada.");

        // 3. Acessa página de depósito
        driver.get("http://localhost:8080/Depositar");

        // 4. Preenche valor múltiplo de 11
        WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
        valorInput.clear();
        valorInput.sendKeys("22");

        // 5. Clica no botão de enviar
        WebElement botaoSubmit = driver.findElement(By.cssSelector("#formDeposito button[type='submit']"));
        botaoSubmit.click();

        // 6. Aguarda mensagem
        WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
        String mensagemTexto = mensagemDiv.getText().trim().replace("\n", "");

        // 7. Validação
        if (mensagemTexto.contains("Erro: valor não pode ser múltiplo de 11")) {
            System.out.println("✅ Teste de depósito 22 passou!");
        } else {
            Assertions.fail("❌ Teste falhou. Mensagem exibida: " + mensagemTexto);
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
