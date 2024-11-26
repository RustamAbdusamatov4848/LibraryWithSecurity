package ru.abdusamatov.librarywithsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abdusamatov.librarywithsecurity.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
