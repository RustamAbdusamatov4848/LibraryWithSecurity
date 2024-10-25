package ru.abdusamatov.librarywithsecurity.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.abdusamatov.librarywithsecurity.models.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b ORDER BY b.title")
    Page<Book> findAllSorted(Pageable pageable);

    List<Book> findByTitleStartingWith(String query);
}
