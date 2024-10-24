CREATE TABLE IF NOT EXISTS library.users
(
    user_id            BIGSERIAL PRIMARY KEY,
    user_full_name     VARCHAR(30) NOT NULL CHECK (LENGTH(user_full_name) BETWEEN 2 AND 30),
    user_email         VARCHAR(255) UNIQUE,
    user_date_of_birth DATE        NOT NULL
);

CREATE TABLE IF NOT EXISTS library.books
(
    book_id                  BIGSERIAL PRIMARY KEY,
    book_title               VARCHAR(200) NOT NULL CHECK (LENGTH(book_title) BETWEEN 2 AND 200),
    book_author_name         VARCHAR(30)  NOT NULL CHECK (LENGTH(book_author_name) BETWEEN 2 AND 30),
    book_author_surname      VARCHAR(30)  NOT NULL CHECK (LENGTH(book_author_surname) BETWEEN 2 AND 30),
    book_year_of_publication INTEGER      NOT NULL CHECK (book_year_of_publication >= 1500),
    book_taken_at            TIMESTAMP    NOT NULL,
    book_owner_id            BIGINT REFERENCES library.users (user_id)
);

CREATE TABLE IF NOT EXISTS library.librarians
(
    librarian_id        BIGSERIAL PRIMARY KEY,
    librarian_full_name VARCHAR(30)   NOT NULL CHECK (LENGTH(librarian_full_name) BETWEEN 2 AND 30),
    librarian_email     VARCHAR(255) UNIQUE,
    librarian_password  VARCHAR(1000) NOT NULL
);
