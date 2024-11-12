package ru.abdusamatov.librarywithsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abdusamatov.librarywithsecurity.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByFullName(String fullName);
}
