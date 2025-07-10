# dev-web

Este projeto Java realiza operações bancárias e simulações de investimento. Para executá-lo corretamente, siga atentamente as instruções abaixo.

## Requisitos

Antes de iniciar, certifique-se de que os seguintes requisitos estejam atendidos:

* PostgreSQL instalado em sua máquina.
* Usuário padrão do PostgreSQL seja `postgres`.
* Senha padrão do usuário seja `123`.
* O nome do banco do PostgreSQL seja `postgres`.
* A porta de conexão seja `5432`.

> Caso você deseje utilizar um nome de usuário,senha, nome de banco, numero de conexão diferentes, é necessário ajustar as configurações de conexão no código, na classe `JDBC`:

```java
private String url = "jdbc:postgresql://localhost:5432/postgres";
private String user = "postgres";
private String password = "123";
```

Caso você também altere o nome do banco de dados, lembre-se de atualizar o valor correspondente na variável `url`.

## Configuração do Banco de Dados

Utilize o script SQL abaixo para criar as tabelas necessárias para execução do projeto. É recomendado executar as instruções **uma por uma e na ordem indicada** para evitar conflitos de chave primária.

```sql
CREATE TABLE users (
    id bigserial NOT NULL,
    "name" text NOT NULL,
    email text NOT NULL,
    password_user text NOT NULL,
    CONSTRAINT users_email_key UNIQUE (email),
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE account (
    id bigserial NOT NULL,
    account_number text NOT NULL,
    agency text NOT NULL,
    balance numeric DEFAULT 0,
    user_id bigint NULL,
    CONSTRAINT account_account_number_key UNIQUE (account_number),
    CONSTRAINT account_pkey PRIMARY KEY (id),
    CONSTRAINT account_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE investment_product (
    id bigserial NOT NULL,
    type_investment varchar(20) NULL,
    return_rate numeric NOT NULL,
    CONSTRAINT investment_product_pkey PRIMARY KEY (id),
    CONSTRAINT investment_product_type_investment_check CHECK (type_investment IN ('CDB', 'TESOURO', 'POUPANÇA'))
);

CREATE TABLE investment (
    id bigserial NOT NULL,
    amount numeric NOT NULL,
    start_date date DEFAULT CURRENT_DATE,
    end_date date DEFAULT CURRENT_DATE,
    account_id bigint NULL,
    investment_product_id bigint NULL,
    CONSTRAINT investment_pkey PRIMARY KEY (id),
    CONSTRAINT investment_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT investment_investment_product_id_fkey FOREIGN KEY (investment_product_id) REFERENCES investment_product(id) ON DELETE CASCADE
);

CREATE TABLE investment_transaction (
    id bigserial NOT NULL,
    "type" varchar(20) NULL,
    amount numeric NOT NULL,
    "timestamp" timestamp DEFAULT CURRENT_TIMESTAMP,
    investment_id bigint NULL,
    description text NULL,
    account_id bigint NULL,
    CONSTRAINT investment_transaction_pkey PRIMARY KEY (id),
    CONSTRAINT investment_transaction_type_check CHECK ("type" IN ('INVEST', 'REDEEM', 'EARN')),
    CONSTRAINT investment_transaction_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(id),
    CONSTRAINT investment_transaction_investment_id_fkey FOREIGN KEY (investment_id) REFERENCES investment(id) ON DELETE CASCADE
);

-- Tabela de transações gerais
CREATE TABLE transactions (
    id bigserial NOT NULL,
    type_transaction varchar(20) NULL,
    amount numeric NOT NULL,
    "timestamp" timestamp DEFAULT CURRENT_TIMESTAMP,
    description text NULL,
    account_id bigint NULL,
    CONSTRAINT transaction_pkey PRIMARY KEY (id),
    CONSTRAINT transaction_type_transaction_check CHECK (type_transaction IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER_IN', 'TRANSFER_OUT', 'INVEST')),
    CONSTRAINT transaction_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

INSERT INTO investment_product (id, type_investment, return_rate)
VALUES
  (1, 'CDB', 0.10),
  (2, 'TESOURO', 0.07),
  (3, 'POUPANÇA', 0.05);
```

## Observações Finais

* Você pode utilizar ferramentas como **DBeaver** ou **pgAdmin** para criar e gerenciar seu banco de dados.
* Certifique-se de que o nome do banco seja `postgres` (ou altere a variável `url` no código para refletir o nome correto).
* Os dados inseridos são fundamentais para que o sistema realize os cálculos de investimento corretamente.

---

Em caso de dúvidas ou erros na execução, verifique as credenciais, nome do banco e o script utilizado.
