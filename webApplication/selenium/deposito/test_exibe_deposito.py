# test_fazer_deposito.py
from selenium import webdriver
from selenium.webdriver.common.by import By
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
        email_input = WebDriverWait(driver, 15).until(EC.visibility_of_element_located((By.ID, "email")))
        senha_input = WebDriverWait(driver, 15).until(EC.visibility_of_element_located((By.ID, "senha")))

        email_input.send_keys("ryan@gmail.com")
        senha_input.send_keys("CD=hj0=r")
        senha_input.submit()

        WebDriverWait(driver, 15).until(EC.presence_of_element_located((By.CLASS_NAME, "balance")))
        print("Login realizado com sucesso! Home carregada.")

        driver.get("http://localhost:8080/Depositar")
        WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "valor")))
        print("Tela de Depósito carregada com sucesso!")

        time.sleep(3)  

    finally:
        driver.quit()

if __name__ == "__main__":
    main()
