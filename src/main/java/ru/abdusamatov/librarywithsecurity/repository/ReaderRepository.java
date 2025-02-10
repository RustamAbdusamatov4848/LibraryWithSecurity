package ru.abdusamatov.librarywithsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.abdusamatov.librarywithsecurity.model.Reader;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
}
