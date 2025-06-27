CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT UNIQUE NOT NULL,
  password_user TEXT NOT NULL
);

CREATE TABLE account (
  id BIGSERIAL PRIMARY KEY,
  account_number TEXT UNIQUE NOT NULL,
  agency TEXT NOT NULL,
  balance NUMERIC DEFAULT 0,
  user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE transactions (
  id BIGSERIAL PRIMARY KEY,
  type_transaction VARCHAR(20) CHECK (type_transaction IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER_IN', 'TRANSFER_OUT', 'INVEST')),
  amount NUMERIC NOT NULL,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  description TEXT,
  account_id BIGINT REFERENCES account(id) ON DELETE CASCADE
);

CREATE TABLE investment_product (
  id BIGSERIAL PRIMARY KEY,
  type_investiment VARCHAR(20) CHECK (type_investiment IN ('CDB', 'TESOURO', 'POUPANÇA')),
  return_rate NUMERIC NOT NULL
);

CREATE TABLE investiment (
  id BIGSERIAL PRIMARY KEY,
  amount NUMERIC NOT NULL,
  start_date DATE DEFAULT CURRENT_DATE,
  end_date DATE DEFAULT CURRENT_DATE,
  account_id BIGINT REFERENCES account(id) ON DELETE CASCADE,
  invest_product_id BIGINT REFERENCES investment_product(id) ON DELETE CASCADE
);

CREATE TABLE investment_transaction (
  id BIGSERIAL PRIMARY KEY,
  type VARCHAR(20) CHECK (type IN ('INVEST', 'REDEEM', 'EARN')),
  amount NUMERIC NOT NULL,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  investment_id BIGINT REFERENCES investiment(id) ON DELETE CASCADE,
  description TEXT
);

INSERT INTO users
VALUES (1, 'teste', 'teste@gmail.com', '123');

INSERT INTO account (id, account_number, agency, balance, user_id)
VALUES (1, '1234567890', '0001', 1000.00, 1);

INSERT INTO transactions (id, type_transaction, amount, description, account_id)
VALUES 
  (1, 'DEPOSIT', 500.00, 'Depósito inicial', 1),
  (2, 'WITHDRAW', 200.00, 'Saque no caixa eletrônico', 1);

INSERT INTO investment_product (id, type_investiment, return_rate)
VALUES 
  (1, 'CDB', 0.10),
  (2, 'TESOURO', 0.07),
  (3, 'POUPANÇA', 0.05);

INSERT INTO investiment (id, amount, start_date, end_date, account_id, invest_product_id)
VALUES (1, 300.00, '2025-06-01', '2025-12-01', 1, 1);

INSERT INTO investment_transaction (id, type, amount, investment_id, description)
VALUES 
  (1, 'INVEST', 300.00, 1, 'Investimento inicial em CDB'),
  (2, 'EARN', 15.00, 1, 'Rendimento do CDB');