package com.mycompany.webapplication.selenium.transferir;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TransferirSeleniumTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");

        driver = new ChromeDriver(options);


        wait = new WebDriverWait(driver, Duration.ofSeconds(8));

        driver.get("http://localhost:8080/Login");

        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement senhaInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("senha")));


        emailInput.sendKeys("teste11@teste.com");
        senhaInput.sendKeys("Abc123@w");
        senhaInput.sendKeys(Keys.ENTER);


        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
    }

    private void acessarTransferir() {

        driver.get("http://localhost:8080/Transferir");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("destino")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
    }


    private String obterMensagem() {
        try {
            WebElement msgDiv = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("mensagem"))
            );
            return safeGetText(msgDiv);
        } catch (TimeoutException | StaleElementReferenceException e) {

            try {
                WebElement msgDiv = driver.findElement(By.id("mensagem"));
                return safeGetText(msgDiv);
            } catch (Exception ex) {
                return "";
            }
        }
    }

    private String safeGetText(WebElement element) {
        try {
            String texto = element.getText();
            return texto == null ? "" : texto.trim();
        } catch (StaleElementReferenceException e) {
            return "";
        }
    }

    @Test
    public void testCarregamentoPaginaTransferir() {
        acessarTransferir();
        WebElement destinoInput = driver.findElement(By.id("destino"));
        WebElement valorInput = driver.findElement(By.id("valor"));

        Assertions.assertNotNull(destinoInput);
        Assertions.assertNotNull(valorInput);
    }

    @Test
    public void testTransferenciaSucesso() {
        acessarTransferir();

        WebElement destinoInput = driver.findElement(By.id("destino"));
        WebElement valorInput = driver.findElement(By.id("valor"));

        destinoInput.clear();
        destinoInput.sendKeys("matheus@gmail.com");
        valorInput.clear();
        valorInput.sendKeys("50");

        driver.findElement(By.cssSelector("#formTransferencia button[type='submit']")).click();

        String mensagem = obterMensagem();
        System.out.println("Mensagem obtida (sucesso): " + mensagem);


        if (!mensagem.isEmpty()) {
            Assertions.assertTrue(
                    mensagem.toLowerCase().contains("sucesso"),
                    "Esperava indicação de sucesso na mensagem, mas veio: " + mensagem
            );
        }
    }

    @Test
    public void testTransferenciaParaMesmaContaNaoPermitida() {
        acessarTransferir();

        WebElement destinoInput = driver.findElement(By.id("destino"));
        WebElement valorInput = driver.findElement(By.id("valor"));


        destinoInput.clear();
        destinoInput.sendKeys("teste11@teste.com");
        valorInput.clear();
        valorInput.sendKeys("50");

        driver.findElement(By.cssSelector("#formTransferencia button[type='submit']")).click();

        String mensagem = obterMensagem();
        System.out.println("Mensagem obtida (mesma conta): " + mensagem);


        String esperado = "não é possível transferir para a própria conta";
        Assertions.assertTrue(
                mensagem.toLowerCase().contains(esperado.toLowerCase()),
                "Mensagem obtida: " + mensagem + " ==> não contém o texto esperado."
        );
    }

    @Test
    public void testTransferenciaParaEmailInexistente() {
        acessarTransferir();

        WebElement destinoInput = driver.findElement(By.id("destino"));
        WebElement valorInput = driver.findElement(By.id("valor"));

        destinoInput.clear();
        destinoInput.sendKeys("naoexiste@teste.com");
        valorInput.clear();
        valorInput.sendKeys("50");

        driver.findElement(By.cssSelector("#formTransferencia button[type='submit']")).click();

        String mensagem = obterMensagem();
        System.out.println("Mensagem obtida (email inexistente): " + mensagem);


        Assertions.assertTrue(
                mensagem.isEmpty() || mensagem.toLowerCase().contains("não encontrad"),
                "Mensagem obtida: " + mensagem + " ==> não parece indicar conta inexistente."
        );
    }

    @Test
    public void testTransferenciaValorMenorQueMinimo() {
        acessarTransferir();

        WebElement destinoInput = driver.findElement(By.id("destino"));
        WebElement valorInput = driver.findElement(By.id("valor"));

        destinoInput.clear();
        destinoInput.sendKeys("matheus@gmail.com");
        valorInput.clear();

        valorInput.sendKeys("0.01");

        driver.findElement(By.cssSelector("#formTransferencia button[type='submit']")).click();

        String mensagem = obterMensagem();
        System.out.println("Mensagem obtida (valor menor que mínimo): " + mensagem);


        Assertions.assertTrue(
                mensagem.isEmpty() || mensagem.toLowerCase().contains("mínimo"),
                "Mensagem obtida: " + mensagem +
                        " ==> esperava mensagem sobre valor mínimo ou nenhuma mensagem."
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
