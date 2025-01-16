CREATE TABLE library.outbox_events
(
    id        BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255)  NOT NULL,
    book_name VARCHAR(255) NOT NULL
);