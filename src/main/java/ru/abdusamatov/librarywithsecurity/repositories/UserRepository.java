package ru.abdusamatov.librarywithsecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abdusamatov.librarywithsecurity.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByFullName(String fullName);
}
