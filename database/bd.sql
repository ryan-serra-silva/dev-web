INSERT INTO users
VALUES (1, 'teste', 'teste@gmail.com', '123');

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

CREATE TABLE transaction (
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
