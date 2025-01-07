-- ==========================================
-- Таблица: library.document
-- Хранит информацию о документах, связанных с пользователями, файл документа хранится в MinIO
-- Поля:
-- - id: Уникальный идентификатор документа (генерируется автоматически).
-- - bucket_name: Название бакета в MinIO — должно быть уникальным и обязательным.
-- - file_name: Название файла внутри MinIO — должно быть уникальным и обязательным.
-- - user_id: Ссылка на пользователя, владеющего документом (обязательное поле).
-- Связи:
-- - user_id является внешним ключом, ссылающимся на id в таблице library.user.
--   Если пользователь удаляется, все его документы автоматически удаляются `ON DELETE CASCADE`.
-- ==========================================
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
