CREATE TABLE library.document
(
    id          BIGSERIAL PRIMARY KEY,
    bucket_name VARCHAR(70) UNIQUE  NOT NULL,
    file_name   VARCHAR(150) UNIQUE NOT NULL,
    user_id     BIGINT              NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES library.user (id)
            ON DELETE CASCADE
);
