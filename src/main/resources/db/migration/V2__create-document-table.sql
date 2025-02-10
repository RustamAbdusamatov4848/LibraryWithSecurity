-- ==========================================
-- Таблица: library.document
-- Хранит информацию о документах, связанных с пользователями, файл документа хранится в MinIO
-- Поля:
-- - id: Уникальный идентификатор документа (генерируется автоматически).
-- - bucket_name: Название бакета в MinIO — должно быть уникальным и обязательным.
-- - file_name: Название файла внутри MinIO — должно быть уникальным и обязательным.
-- - reader_id: Ссылка на пользователя, владеющего документом (обязательное поле).
-- Связи:
-- - reader_id является внешним ключом, ссылающимся на id в таблице library.reader.
--   Если пользователь удаляется, все его документы автоматически удаляются `ON DELETE CASCADE`.
-- ==========================================
CREATE TABLE library.document
(
    id          BIGSERIAL PRIMARY KEY,
    bucket_name VARCHAR(70) UNIQUE  NOT NULL,
    file_name   VARCHAR(150) UNIQUE NOT NULL,
    reader_id     BIGINT              NOT NULL,
    CONSTRAINT fk_reader
        FOREIGN KEY (reader_id)
            REFERENCES library.reader (id)
            ON DELETE CASCADE
);
