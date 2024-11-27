package ru.abdusamatov.librarywithsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.abdusamatov.librarywithsecurity.model.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document findByOwnerId(long userId);
}
