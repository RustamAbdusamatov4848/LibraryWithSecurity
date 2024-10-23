package ru.abdusamatov.librarywithsecurity.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    public User getUserByID(Long ID) {
        return userRepository.findById(ID).orElse(null);
    }

    public Optional<User> getUserByFullname(String fullname) {
        return userRepository.findByFullName(fullname);
    }

    public List<Book> getBooksByPersonID(Long ID) {
        Optional<User> user = userRepository.findById(ID);

        if (user.isPresent()) {
            Hibernate.initialize(user.get().getBooks());

            user.get().getBooks().forEach(book -> {
                long holdingTime = Math.abs(book.getTakenAt().getTime() - new Date().getTime());
                if (holdingTime > 864000000) {
                    book.setExpired(true);
                }
            });
            return user.get().getBooks();
        } else {
            return Collections.emptyList();
        }
    }

    @Transactional
    public boolean createUser(User user) {
        String userEmail = user.getEmail();
        if (userRepository.findByEmail(userEmail) != null) return false;
        log.info("Saving new User with email: {}", userEmail);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public void editPerson(User user, Long ID) {
        user.setId(ID);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserByID(Long ID) {
        userRepository.deleteById(ID);
    }
}
