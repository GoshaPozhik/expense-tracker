-- Пользователи
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL
);

-- Кошельки / Счета (может быть личным или общим)
CREATE TABLE wallets (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
    -- Баланс можно хранить здесь, но его надежнее считать на лету
    -- или обновлять триггерами, чтобы избежать рассинхронизации.
    -- Для простоты курсовой оставим его в виде расчетной величины.
                         owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE -- Кто создал кошелек
);

-- Связующая таблица для связи "многие-ко-многим" между users и wallets
CREATE TABLE user_wallets (
                              user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              wallet_id BIGINT NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
                              PRIMARY KEY (user_id, wallet_id) -- Уникальная пара пользователь-кошелек
);

-- Категории расходов (могут быть общими или пользовательскими)
CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            user_id BIGINT REFERENCES users(id) ON DELETE SET NULL -- NULL, если категория общая (дефолтная)
);

-- Расходы
CREATE TABLE expenses (
                          id BIGSERIAL PRIMARY KEY,
                          amount DECIMAL(10, 2) NOT NULL,
                          description VARCHAR(255),
                          expense_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          user_id BIGINT NOT NULL REFERENCES users(id), -- Кто добавил расход
                          wallet_id BIGINT NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
                          category_id BIGINT NOT NULL REFERENCES categories(id)
);

-- Добавим несколько дефолтных категорий
INSERT INTO categories (name) VALUES ('Продукты'), ('Транспорт'), ('Развлечения'), ('Образование'), ('Коммунальные платежи');

DROP TABLE users CASCADE;
DROP TABLE wallets CASCADE ;
DROP TABLE user_wallets CASCADE ;
DROP TABLE categories CASCADE ;
DROP TABLE expenses CASCADE ;