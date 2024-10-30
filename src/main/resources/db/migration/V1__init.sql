CREATE SCHEMA IF NOT EXISTS library;

CREATE SEQUENCE library.user_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE library.book_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE library.librarian_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE library.user
(
    id            BIGSERIAL PRIMARY KEY DEFAULT nextval('library.user_id_seq'),
    full_name     VARCHAR(30)         NOT NULL CHECK (LENGTH(full_name) BETWEEN 2 AND 30),
    email         VARCHAR(255) UNIQUE NOT NULL,
    date_of_birth DATE                NOT NULL
);

CREATE TABLE library.book
(
    id                  BIGSERIAL PRIMARY KEY DEFAULT nextval('library.book_id_seq'),
    title               VARCHAR(200) NOT NULL CHECK (LENGTH(title) BETWEEN 2 AND 200),
    author_name         VARCHAR(30)  NOT NULL CHECK (LENGTH(author_name) BETWEEN 2 AND 30),
    author_surname      VARCHAR(30)  NOT NULL CHECK (LENGTH(author_surname) BETWEEN 2 AND 30),
    year_of_publication INT CHECK (year_of_publication >= 1500),
    taken_at            TIMESTAMP,
    owner_id            BIGINT,
    CONSTRAINT fk_user
        FOREIGN KEY (owner_id)
            REFERENCES library.user (id)
            ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS library.librarian
(
    id        BIGSERIAL PRIMARY KEY DEFAULT nextval('library.librarian_id_seq'),
    full_name VARCHAR(30)         NOT NULL CHECK (LENGTH(full_name) BETWEEN 2 AND 30),
    email     VARCHAR(255) UNIQUE NOT NULL,
    password  VARCHAR(100)        NOT NULL
);
