package ru.abdusamatov.librarywithsecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abdusamatov.librarywithsecurity.models.User;

public interface UserRepository extends JpaRepository<User,Long> {

}
