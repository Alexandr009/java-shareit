-- Удаление таблиц, если они существуют
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Создание таблицы users
CREATE TABLE IF NOT EXISTS users
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    email
    VARCHAR
(
    100
) NOT NULL UNIQUE,
    name VARCHAR
(
    100
) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE
(
    email
)
    );

-- Создание таблицы requests
CREATE TABLE IF NOT EXISTS requests
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    description
    VARCHAR
(
    100
) NOT NULL,
    requestor_id INTEGER REFERENCES users
(
    id
) ON DELETE CASCADE,
    created TIMESTAMP NOT NULL
    );

-- Создание таблицы items
CREATE TABLE IF NOT EXISTS items
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    100
) NOT NULL,
    description VARCHAR
(
    100
) NOT NULL,
    is_available BOOLEAN NOT NULL, -- Исправлено на BOOLEAN
    owner_id INTEGER REFERENCES users
(
    id
) ON DELETE CASCADE,
    request_id INTEGER REFERENCES requests
(
    id
)
  ON DELETE SET NULL -- Исправлено на requests
    );

-- Создание таблицы bookings
CREATE TABLE IF NOT EXISTS bookings
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    start_date
    TIMESTAMP
    NOT
    NULL,
    end_date
    TIMESTAMP
    NOT
    NULL,
    item_id
    INTEGER
    REFERENCES
    items
(
    id
) ON DELETE CASCADE, -- Исправлено на items
    booker_id INTEGER REFERENCES users
(
    id
)
  ON DELETE CASCADE,
    status VARCHAR
(
    100
) NOT NULL
    -- approved BOOLEAN NOT NULL -- Исправлено на BOOLEAN
    );

-- Создание таблицы comments
CREATE TABLE IF NOT EXISTS comments
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    text
    VARCHAR
(
    100
) NOT NULL,
    item_id INTEGER REFERENCES items
(
    id
) ON DELETE CASCADE, -- Исправлено на items
    author_id INTEGER REFERENCES users
(
    id
)
  ON DELETE CASCADE,
    created TIMESTAMP NOT NULL -- Добавлено поле created
    );