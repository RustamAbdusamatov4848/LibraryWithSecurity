package ru.abdusamatov.librarywithsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abdusamatov.librarywithsecurity.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
