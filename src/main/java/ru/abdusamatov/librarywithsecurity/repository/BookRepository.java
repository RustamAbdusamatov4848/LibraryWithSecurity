package ru.abdusamatov.librarywithsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.abdusamatov.librarywithsecurity.model.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.title LIKE CONCAT('%',:query, '%')")
    List<Book> findByTitleStartingWith(String query);
}
