package ru.abdusamatov.librarywithsecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.abdusamatov.librarywithsecurity.models.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
