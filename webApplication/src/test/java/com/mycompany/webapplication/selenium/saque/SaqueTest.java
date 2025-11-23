// package com.mycompany.webapplication.selenium.saque;

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

// public class SaqueTest {
//     private WebDriver driver;
//     private WebDriverWait wait;

//     @BeforeEach
//     public void setUp() {
//         // Configurações do Chrome (como no Python)
//         ChromeOptions options = new ChromeOptions();
//         options.addArguments("--start-maximized");
//         options.addArguments("--disable-notifications");
//         options.addArguments("--disable-infobars");

//         driver = new ChromeDriver(options);
//         wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//     }

//     @Test
//     public void deveRetornarErroQuandoValorMenorQueMinimo() throws InterruptedException {
//         driver.get("http://localhost:8080/Login");

//         WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//         WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

//         emailInput.sendKeys("teste11@teste.com");
//         senhaInput.sendKeys("Abc123@w");
//         senhaInput.sendKeys(Keys.ENTER);

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
//         System.out.println("✅ Login realizado com sucesso! Home carregada.");

//         driver.get("http://localhost:8080/Sacar?horaTeste=10:00");
//         Thread.sleep(1000);

//         WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
//         valorInput.clear();
//         valorInput.sendKeys("9");

//         WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//         botaoSubmit.click();

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message")));
//         WebElement msgElement = driver.findElement(By.cssSelector(".message"));

//         String mensagemTexto = msgElement.getText().trim().replace("\n", "");

//         Assertions.assertEquals("Erro: valor menor que o saque mínimo.", mensagemTexto);
//     }

//     @Test
//     public void deveRetornarErroQuandoValorMaiorQueMaximo() throws InterruptedException {
//         driver.get("http://localhost:8080/Login");

//         WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//         WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

//         emailInput.sendKeys("teste11@teste.com");
//         senhaInput.sendKeys("Abc123@w");
//         senhaInput.sendKeys(Keys.ENTER);

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
//         System.out.println("✅ Login realizado com sucesso! Home carregada.");

//         driver.get("http://localhost:8080/Sacar?horaTeste=10:00");
//         Thread.sleep(1000);

//         WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
//         valorInput.clear();
//         valorInput.sendKeys("2001");

//         WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//         botaoSubmit.click();

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message")));
//         WebElement msgElement = driver.findElement(By.cssSelector(".message"));

//         String mensagemTexto = msgElement.getText().trim().replace("\n", "");

//         Assertions.assertEquals("Erro: valor maior que o saque máximo.", mensagemTexto);
//     }

//     @Test
//     public void deveRetornarErroQuandoValorNaoMultiploDe10() throws InterruptedException {
//         driver.get("http://localhost:8080/Login");

//         WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//         WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

//         emailInput.sendKeys("teste11@teste.com");
//         senhaInput.sendKeys("Abc123@w");
//         senhaInput.sendKeys(Keys.ENTER);

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
//         System.out.println("✅ Login realizado com sucesso! Home carregada.");

//         driver.get("http://localhost:8080/Sacar?horaTeste=10:00");
//         Thread.sleep(1000);

//         WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
//         valorInput.clear();
//         valorInput.sendKeys("11");

//         WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//         botaoSubmit.click();

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message")));
//         WebElement msgElement = driver.findElement(By.cssSelector(".message"));

//         String mensagemTexto = msgElement.getText().trim().replace("\n", "");

//         Assertions.assertEquals("Erro: valor deve ser múltiplo de 10.", mensagemTexto);
//     }

//     @Test
//     public void deveBloquearSaqueEntre12he12h30m() throws InterruptedException {
//         driver.get("http://localhost:8080/Login");

//         WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//         WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

//         emailInput.sendKeys("teste11@teste.com");
//         senhaInput.sendKeys("Abc123@w");
//         senhaInput.sendKeys(Keys.ENTER);

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
//         System.out.println("✅ Login realizado com sucesso! Home carregada.");

//         driver.get("http://localhost:8080/Sacar?horaTeste=12:10");
//         Thread.sleep(1000);

//         WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
//         valorInput.clear();
//         valorInput.sendKeys("10");

//         WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//         botaoSubmit.click();

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message")));
//         WebElement msgElement = driver.findElement(By.cssSelector(".message"));

//         String mensagemTexto = msgElement.getText().trim().replace("\n", "");

//         Assertions.assertEquals("Saque não permitido entre 12:00 e 12:30.", mensagemTexto);
//     }

//     @Test
//     public void deveBloquearSaqueEntre18he18h30m() throws InterruptedException {
//         driver.get("http://localhost:8080/Login");

//         WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//         WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

//         emailInput.sendKeys("teste11@teste.com");
//         senhaInput.sendKeys("Abc123@w");
//         senhaInput.sendKeys(Keys.ENTER);

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
//         System.out.println("✅ Login realizado com sucesso! Home carregada.");

//         driver.get("http://localhost:8080/Sacar?horaTeste=18:10");
//         Thread.sleep(1000);

//         WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
//         valorInput.clear();
//         valorInput.sendKeys("10");

//         WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//         botaoSubmit.click();

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message")));
//         WebElement msgElement = driver.findElement(By.cssSelector(".message"));

//         String mensagemTexto = msgElement.getText().trim().replace("\n", "");

//         Assertions.assertEquals("Saque não permitido entre 18:00 e 18:30.", mensagemTexto);
//     }

//     @Test
//     public void deveRetornarErroQuandoSaldoInsuficiente() throws InterruptedException {
//         driver.get("http://localhost:8080/Login");

//         WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//         WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

//         emailInput.sendKeys("teste11@teste.com");
//         senhaInput.sendKeys("Abc123@w");
//         senhaInput.sendKeys(Keys.ENTER);

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
//         System.out.println("✅ Login realizado com sucesso! Home carregada.");

//         driver.get("http://localhost:8080/Sacar?horaTeste=11:10");
//         Thread.sleep(1000);

//         WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
//         valorInput.clear();
//         valorInput.sendKeys("1990");

//         WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//         botaoSubmit.click();

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message")));
//         WebElement msgElement = driver.findElement(By.cssSelector(".message"));

//         String mensagemTexto = msgElement.getText().trim().replace("\n", "");

//         Assertions.assertEquals("Erro: saldo insuficiente.", mensagemTexto);
//     }

//     @Test
//     public void devePermitirSaqueValido() throws InterruptedException {
//         driver.get("http://localhost:8080/Login");

//         WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//         WebElement senhaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

//         emailInput.sendKeys("teste11@teste.com");
//         senhaInput.sendKeys("Abc123@w");
//         senhaInput.sendKeys(Keys.ENTER);

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.className("balance")));
//         System.out.println("✅ Login realizado com sucesso! Home carregada.");

//         driver.get("http://localhost:8080/Sacar?horaTeste=10:00&isTest=true");
//         Thread.sleep(1000);

//         WebElement valorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("valor")));
//         valorInput.clear();
//         valorInput.sendKeys("20");

//         WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//         botaoSubmit.click();

//         wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message")));
//         WebElement msgElement = driver.findElement(By.cssSelector(".message"));

//         String mensagemTexto = msgElement.getText().trim().replace("\n", "");

//         Assertions.assertEquals("Saque realizado com sucesso!", mensagemTexto);
//     }

//     @AfterEach
//     public void tearDown() {
//         if (driver != null) {
//             driver.quit();
//         }
//     }
// }
