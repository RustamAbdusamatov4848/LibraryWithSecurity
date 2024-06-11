package ru.abdusamatov.librarywithsecurity.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibrarianService {
    private final LibrarianRepository librarianRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean createLibrarian(Librarian librarian) {
        String userEmail = librarian.getEmail();
        if (librarianRepository.findByEmail(userEmail) != null) return false;
        librarian.setPassword(passwordEncoder.encode(librarian.getPassword()));
        log.info("Saving new Librarian with email: {}", userEmail);
        librarianRepository.save(librarian);
        return true;
    }
}
