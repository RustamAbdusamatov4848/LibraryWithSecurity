package ru.abdusamatov.librarywithsecurity.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;
import ru.abdusamatov.librarywithsecurity.util.mappers.LibrarianMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibrarianService {
    private final LibrarianRepository librarianRepository;
    private final PasswordEncoder passwordEncoder;
    private final LibrarianMapper librarianMapper;

    @Transactional
    public void createLibrarian(LibrarianDto librarianDto) {
        Librarian librarian = librarianMapper.librarianDtoToLibrarian(librarianDto);
        librarian.setPassword(passwordEncoder.encode(librarianDto.getPassword()));
        librarian = librarianRepository.save(librarian);
        log.info("Saving new Librarian with ID: {}", librarian.getId());
    }
}
