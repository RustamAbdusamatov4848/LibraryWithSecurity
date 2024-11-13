package ru.abdusamatov.librarywithsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.abdusamatov.librarywithsecurity.model.Librarian;

import java.util.Optional;


@Repository
public interface LibrarianRepository extends JpaRepository<Librarian, Long> {
    Optional<Librarian> findByEmail(String email);

    Boolean existsByEmail(String email);
}
