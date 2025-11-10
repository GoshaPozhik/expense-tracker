CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE wallets (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE user_wallets (
                              user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              wallet_id BIGINT NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
                              PRIMARY KEY (user_id, wallet_id)
);

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            user_id BIGINT REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE expenses (
                          id BIGSERIAL PRIMARY KEY,
                          amount DECIMAL(10, 2) NOT NULL,
                          description VARCHAR(255),
                          expense_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          user_id BIGINT NOT NULL REFERENCES users(id),
                          wallet_id BIGINT NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
                          category_id BIGINT NOT NULL REFERENCES categories(id)
);

INSERT INTO categories (name) VALUES ('Продукты'), ('Транспорт'), ('Развлечения'), ('Образование'), ('Коммунальные платежи');

DROP TABLE users CASCADE;
DROP TABLE wallets CASCADE ;
DROP TABLE user_wallets CASCADE ;
DROP TABLE categories CASCADE ;
DROP TABLE expenses CASCADE ;