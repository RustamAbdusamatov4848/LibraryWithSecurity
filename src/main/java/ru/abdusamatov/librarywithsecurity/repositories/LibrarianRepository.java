package ru.abdusamatov.librarywithsecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.abdusamatov.librarywithsecurity.models.Librarian;

import java.util.Optional;


@Repository
public interface LibrarianRepository extends JpaRepository<Librarian, Long> {
    Optional<Librarian> findByEmail(String email);
}
