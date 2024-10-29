CREATE SCHEMA IF NOT EXISTS library;

CREATE TABLE IF NOT EXISTS library.user
(
    id            BIGSERIAL PRIMARY KEY,
    full_name     VARCHAR(30) NOT NULL CHECK (LENGTH(full_name) BETWEEN 2 AND 30),
    email         VARCHAR(255) UNIQUE,
    date_of_birth DATE        NOT NULL
);

CREATE TABLE IF NOT EXISTS library.book
(
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(200) NOT NULL CHECK (LENGTH(title) BETWEEN 2 AND 200),
    author_name         VARCHAR(30)  NOT NULL CHECK (LENGTH(author_name) BETWEEN 2 AND 30),
    author_surname      VARCHAR(30)  NOT NULL CHECK (LENGTH(author_surname) BETWEEN 2 AND 30),
    year_of_publication INTEGER      NOT NULL CHECK (year_of_publication >= 1500),
    taken_at            TIMESTAMP                           default now(),
    owner_id            BIGINT REFERENCES library.user (id) default null
);

CREATE TABLE IF NOT EXISTS library.librarian
(
    id        BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(30)  NOT NULL CHECK (LENGTH(full_name) BETWEEN 2 AND 30),
    email     VARCHAR(255) UNIQUE,
    password  VARCHAR(100) NOT NULL
);
