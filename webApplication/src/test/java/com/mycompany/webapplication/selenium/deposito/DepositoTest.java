// package com.mycompany.webapplication.selenium.deposito;

// import java.time.Duration;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.openqa.selenium.By;
// import org.openqa.selenium.Keys;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;

// public class DepositoTest {

//     private WebDriver driver;
//     private WebDriverWait wait;

//     @BeforeEach
//     public void setUp() {
//         ChromeOptions options = new ChromeOptions();
//         options.addArguments("--start-maximized");
//         options.addArguments("--disable-notifications");
//         options.addArguments("--disable-infobars");

//         driver = new ChromeDriver(options);
//         wait = new WebDriverWait(driver, Duration.ofSeconds(30));

//         driver.get("http://localhost:8080/Login");

//         WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//         WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

//         emailInput.sendKeys("teste11@teste.com");
//         senhaInput.sendKeys("Abc123@w");
//         senhaInput.sendKeys(Keys.ENTER);

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
//     }

//     private void acessarDeposito() {
//         driver.get("http://localhost:8080/Depositar");
//         wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
//     }

//     @Test
//     public void testDepositoValorImpar() {
//         acessarDeposito();
//         WebElement valorInput = driver.findElement(By.id("valor"));
//         valorInput.clear();
//         valorInput.sendKeys("11");

//         driver.findElement(By.cssSelector("#formDeposito button[type='submit']")).click();

//         WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
//         Assertions.assertTrue(mensagemDiv.getText().contains("Erro: valor não pode ser impar"));
//     }

//     @Test
//     public void testDepositoMultiploDe7() {
//         acessarDeposito();
//         WebElement valorInput = driver.findElement(By.id("valor"));
//         valorInput.clear();
//         valorInput.sendKeys("14");

//         driver.findElement(By.cssSelector("#formDeposito button[type='submit']")).click();

//         WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
//         Assertions.assertTrue(mensagemDiv.getText().contains("Erro: valor não pode ser múltiplo de 7"));
//     }

//     @Test
//     public void testDepositoMultiploDe11() {
//         acessarDeposito();
//         WebElement valorInput = driver.findElement(By.id("valor"));
//         valorInput.clear();
//         valorInput.sendKeys("22");

//         driver.findElement(By.cssSelector("#formDeposito button[type='submit']")).click();

//         WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
//         Assertions.assertTrue(mensagemDiv.getText().contains("Erro: valor não pode ser múltiplo de 11"));
//     }

//     @Test
//     public void testValor30InseridoCorretamente() {
//         acessarDeposito();
//         WebElement valorInput = driver.findElement(By.id("valor"));
//         valorInput.clear();
//         valorInput.sendKeys("30");

//         Assertions.assertEquals("30", valorInput.getAttribute("value"));
//     }

//     @Test
//     public void testDepositoSucessoComValor50() {
//         acessarDeposito();
//         WebElement valorInput = driver.findElement(By.id("valor"));
//         valorInput.clear();
//         valorInput.sendKeys("50");

//         driver.findElement(By.cssSelector("#formDeposito button[type='submit']")).click();

//         WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
//         Assertions.assertTrue(mensagemDiv.getText().contains("Depósito realizado com sucesso"));
//     }

//     @Test
//     public void testDepositoMaiorQue5000() {
//         acessarDeposito();
//         WebElement valorInput = driver.findElement(By.id("valor"));
//         valorInput.clear();
//         valorInput.sendKeys("5001");

//         driver.findElement(By.cssSelector("#formDeposito button[type='submit']")).click();

//         WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
//         Assertions.assertTrue(mensagemDiv.getText().contains("Erro: valor maior que o depósito máximo de R$5000"));
//     }

//     @Test
//     public void testDepositoMenorQue10() {
//         acessarDeposito();
//         WebElement valorInput = driver.findElement(By.id("valor"));
//         valorInput.clear();
//         valorInput.sendKeys("9");

//         driver.findElement(By.cssSelector("#formDeposito button[type='submit']")).click();

//         WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message")));
//         Assertions.assertTrue(mensagemDiv.getText().contains("Erro: valor menor que o depósito mínimo de R$10"));
//     }

//     @Test
//     public void testCarregamentoPaginaDeposito() {
//         acessarDeposito();
//         WebElement valorInput = driver.findElement(By.id("valor"));
//         Assertions.assertNotNull(valorInput);
//     }

//     @AfterEach
//     public void tearDown() {
//         if (driver != null) {
//             driver.quit();
//         }
//     }
// }
