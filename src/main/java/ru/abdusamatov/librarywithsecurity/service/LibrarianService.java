package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.AuthenticationDto;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.exception.ExistEmailException;
import ru.abdusamatov.librarywithsecurity.model.Librarian;
import ru.abdusamatov.librarywithsecurity.repository.LibrarianRepository;
import ru.abdusamatov.librarywithsecurity.service.mapper.LibrarianMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibrarianService {
    private final LibrarianRepository librarianRepository;
    private final PasswordEncoder passwordEncoder;
    private final LibrarianMapper librarianMapper;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LibrarianDto createLibrarian(final LibrarianDto librarianDto) {
        checkIfEmailExists(librarianDto.getEmail());

        var librarianFromDto = librarianMapper.librarianDtoToLibrarian(librarianDto);
        librarianFromDto.setPassword(passwordEncoder.encode(librarianDto.getPassword()));
        Librarian savedLibrarian = librarianRepository.save(librarianFromDto);

        log.info("Saving new Librarian with ID: {}", savedLibrarian.getId());
        return librarianMapper.librarianToLibrarianDto(savedLibrarian);
    }

    @Transactional(readOnly = true)
    public void checkIfEmailExists(final String librarianEmail) {
        if (librarianRepository.existsByEmail(librarianEmail)) {
            log.warn("Attempt to register with existing email: {}", librarianEmail);
            throw new ExistEmailException(librarianEmail);
        }
    }

    public void validateLibrarian(final AuthenticationDto authenticationDto) {
        final var authentication = authenticate(authenticationDto);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Authentication authenticate(final AuthenticationDto authenticationDto) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationDto.getEmail(), authenticationDto.getPassword())
        );
    }
}
