from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
import time

def main():
    chrome_options = Options()
    chrome_options.add_argument("--start-maximized")
    chrome_options.add_argument("--disable-notifications")
    chrome_options.add_argument("--disable-infobars")

    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=chrome_options)

    try:
        driver.get("http://localhost:8080/Login")
        email_input = WebDriverWait(driver, 15).until(
            EC.visibility_of_element_located((By.ID, "email"))
        )
        senha_input = WebDriverWait(driver, 15).until(
            EC.visibility_of_element_located((By.ID, "senha"))
        )

        email_input.send_keys("ryan@gmail.com")
        senha_input.send_keys("CD=hj0=r")
        senha_input.send_keys(Keys.ENTER)

        WebDriverWait(driver, 15).until(
            EC.presence_of_element_located((By.CLASS_NAME, "balance"))
        )
        print("Login realizado com sucesso! Home carregada.")

        driver.get("http://localhost:8080/Depositar")
        time.sleep(1) 

        valor_input = WebDriverWait(driver, 15).until(
            EC.visibility_of_element_located((By.ID, "valor"))
        )
        valor_input.clear()
        valor_input.send_keys("9")

        botao_submit = driver.find_element(By.CSS_SELECTOR, "#formDeposito button[type='submit']")
        botao_submit.click()

        mensagem_div = WebDriverWait(driver, 30).until(
            EC.visibility_of_element_located((By.CSS_SELECTOR, ".message"))
        )
        mensagem_texto = mensagem_div.text

        if "Erro: valor menor que o depósito mínimo de R$10" in mensagem_texto:
            print("Teste de depósito menor que 10 passou!")
        else:
            print("Teste falhou. Mensagem exibida:", mensagem_texto)

        input("Pressione Enter para fechar o navegador...")

    finally:
        driver.quit()

if __name__ == "__main__":
    main()
