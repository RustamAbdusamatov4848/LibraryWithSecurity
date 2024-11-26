CREATE TABLE library.document
(
    id        BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(150) UNIQUE NOT NULL,
    file_type VARCHAR(50)         NOT NULL,
    file_size BIGINT              NOT NULL,
    owner_id  BIGINT              NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY (owner_id)
            REFERENCES library.user (id)
            ON DELETE CASCADE
);
