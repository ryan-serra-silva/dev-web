from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
import sys
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
        valor_input.send_keys("50")

        botao_submit = driver.find_element(By.CSS_SELECTOR, "#formDeposito button[type='submit']")
        botao_submit.click()

 
        mensagem_div = WebDriverWait(driver, 30).until(
            EC.visibility_of_element_located((By.CSS_SELECTOR, ".message"))
        )
        mensagem_texto = mensagem_div.text

        time.sleep(5)  

        if "Depósito realizado com sucesso" in mensagem_texto:
            print("Teste de depósito passou com sucesso!")
        else:
            print("Depósito não realizado. Mensagem:", mensagem_texto)

    except Exception as e:
        print("Erro no teste:", e)
        print("HTML atual da página (primeiros 1000 caracteres):")
        print(driver.page_source[:1000])
        driver.quit()
        sys.exit(1)

    finally:
        driver.quit()


if __name__ == "__main__":
    main()
