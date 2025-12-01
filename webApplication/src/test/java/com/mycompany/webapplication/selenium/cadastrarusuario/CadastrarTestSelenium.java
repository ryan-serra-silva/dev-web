//package com.mycompany.webapplication.selenium.cadastrarusuario;
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
//public class CadastrarTestSelenium {
//   private WebDriver driver;
//   private WebDriverWait wait;
//
//   @BeforeEach
//   public void setUp() {
//       // Configurações do Chrome
//       ChromeOptions options = new ChromeOptions();
//       options.addArguments("--start-maximized");
//       options.addArguments("--disable-notifications");
//        options.addArguments("--headless"); // Descomente se não quiser ver o navegador abrindo
//
//       driver = new ChromeDriver(options);
//       wait = new WebDriverWait(driver, Duration.ofSeconds(25));
//       driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(25));
//   }
//
//   @Test
//public void deveCadastrarComSucesso() throws InterruptedException {
//
//    // URL correta do Codespaces
//    String url = "http://localhost:8080/CadastroUsuario";
//
//    driver.get(url);
//
//    System.out.println(">>> URL acessada: " + driver.getCurrentUrl());
//    Thread.sleep(2000); // Espera inicial para a página carregar (Codespaces costuma ser lento)
//    System.out.println(">>> HTML inicial:");
//    System.out.println(driver.getPageSource());
//
//    // 1. Busca os campos
//    WebElement nomeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));
//    WebElement emailInput = driver.findElement(By.id("email"));
//    WebElement senhaInput = driver.findElement(By.id("senha"));
//
//    // 2. Preenche o formulário
//    nomeInput.sendKeys("Teste Nome");
//    emailInput.sendKeys("teste1234@teste.com");
//    senhaInput.sendKeys("Abc123@b");
//
//    // 3. Clica no botão submit
//    WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//    botaoSubmit.click();
//
//    // Espera o backend responder
//    Thread.sleep(2000);
//
//    System.out.println(">>> HTML após submit:");
//    System.out.println(driver.getPageSource());
//
//    // 4. Procura pela mensagem de sucesso
//    WebElement msgElement = wait.until(
//            ExpectedConditions.visibilityOfElementLocated(By.className("success-msg"))
//    );
//
//    String mensagemTexto = msgElement.getText().trim();
//
//    Assertions.assertEquals(
//            "Cadastro realizado com sucesso! Conta criada. Redirecionando para login...",
//            mensagemTexto
//    );
//}
//
//
//   @Test
//   public void deveRetornarErroQuandoEmailJaCadastrado() {
//      driver.get("http://localhost:8080/CadastroUsuario");
//    //    System.out.println(driver.getCurrentUrl());
//    //     System.out.println(driver.getPageSource());
//
//       WebElement nomeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));
//       WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//       WebElement senhaInput = driver.findElement(By.id("senha"));
//
//       // Preenche com email já cadastrado
//       nomeInput.sendKeys("Teste Nome");
//       emailInput.sendKeys("teste11@teste.com");
//       senhaInput.sendKeys("Abc123@b");
//
//       WebElement botaoSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
//       botaoSubmit.click();
//
//       WebElement mensagemDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-msg")));
//        String mensagemTexto = mensagemDiv.getText().trim().replace("\n","");
//         if (mensagemTexto.contains("E-mail já está em uso. Tente outro.")) {
//            System.out.println("Teste de Cadastro de Usuario Passou");
//         } else {
//             Assertions.fail("Teste falhou. Mensagem exibida: " + mensagemTexto);
//         }
//   }
//
//   @AfterEach
//   public void tearDown() {
//       if (driver != null) {
//           driver.quit();
//       }
//   }
//}