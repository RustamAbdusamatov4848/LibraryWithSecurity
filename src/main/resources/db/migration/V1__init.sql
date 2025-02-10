-- Создание схемы library, если она еще не существует
CREATE SCHEMA IF NOT EXISTS library;

-- ==========================================
-- Таблица: library.reader
-- Хранит информацию о читателях библиотеки.
-- Поля:
-- - id: Уникальный идентификатор читателя (генерируется автоматически).
-- - full_name: Полное имя читателя (длина от 2 до 30 символов, обязательное поле).
-- - email: Электронная почта читателя (уникальная, обязательное поле).
-- - date_of_birth: Дата рождения читателя (обязательное поле).
-- ==========================================
CREATE TABLE library.reader
(
    id            BIGSERIAL PRIMARY KEY,
    full_name     VARCHAR(30)         NOT NULL CHECK (LENGTH(full_name) BETWEEN 2 AND 30),
    email         VARCHAR(255) UNIQUE NOT NULL,
    date_of_birth DATE                NOT NULL
);

-- ==========================================
-- Таблица: library.book
-- Хранит информацию о книгах в библиотеке.
-- Поля:
-- - id: Уникальный идентификатор книги (генерируется автоматически).
-- - title: Название книги (длина от 2 до 200 символов, обязательное поле).
-- - author_name: Имя автора книги (длина от 2 до 30 символов, обязательное поле).
-- - author_surname: Фамилия автора книги (длина от 2 до 30 символов, обязательное поле).
-- - year_of_publication: Год издания книги (не ранее 1500 года, необязательное поле).
-- - taken_at: Дата и время, когда книга была взята (необязательное поле).
-- - owner_id: Ссылка на пользователя, взявшего книгу (может быть NULL, если книга не на руках).
-- Связи:
-- - owner_id является внешним ключом, ссылающимся на id в таблице library.reader.
--   Если пользователь удаляется, owner_id для книги устанавливается в NULL.
-- ==========================================
CREATE TABLE library.book
(
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(200) NOT NULL CHECK (LENGTH(title) BETWEEN 2 AND 200),
    author_name         VARCHAR(30)  NOT NULL CHECK (LENGTH(author_name) BETWEEN 2 AND 30),
    author_surname      VARCHAR(30)  NOT NULL CHECK (LENGTH(author_surname) BETWEEN 2 AND 30),
    year_of_publication INT CHECK (year_of_publication >= 1500),
    taken_at            TIMESTAMP,
    owner_id            BIGINT,
    CONSTRAINT fk_reader
        FOREIGN KEY (owner_id)
            REFERENCES library.reader (id)
            ON DELETE SET NULL
);
