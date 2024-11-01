package ru.abdusamatov.librarywithsecurity.services;

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
import ru.abdusamatov.librarywithsecurity.exceptions.ExistEmailException;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;
import ru.abdusamatov.librarywithsecurity.util.Response;
import ru.abdusamatov.librarywithsecurity.util.Result;
import ru.abdusamatov.librarywithsecurity.util.mappers.LibrarianMapper;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibrarianService {
    private final LibrarianRepository librarianRepository;
    private final PasswordEncoder passwordEncoder;
    private final LibrarianMapper librarianMapper;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Response<LibrarianDto> createLibrarian(LibrarianDto librarianDto) {
        checkIfEmailExists(librarianDto.getEmail());

        Librarian librarianFromDto = librarianMapper.librarianDtoToLibrarian(librarianDto);
        librarianFromDto.setPassword(passwordEncoder.encode(librarianDto.getPassword()));
        Librarian savedLibrarian = librarianRepository.save(librarianFromDto);

        log.info("Saving new Librarian with ID: {}", savedLibrarian.getId());
        return Response.buildResponse(Result.success(CREATED, "Librarian was created"),
                librarianMapper.librarianToLibrarianDto(savedLibrarian));
    }

    @Transactional(readOnly = true)
    public void checkIfEmailExists(String librarianEmail) {
        if (librarianRepository.existsByEmail(librarianEmail)) {
            log.warn("Attempt to register with existing email: {}", librarianEmail);
            throw new ExistEmailException(librarianEmail);
        }
    }

    public Response<Void> validateLibrarian(AuthenticationDto authenticationDto) {
        Authentication authentication = authenticate(authenticationDto);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return Response.buildResponse(Result.success(NO_CONTENT, "Successful validation"), null);
    }

    private Authentication authenticate(AuthenticationDto authenticationDto) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationDto.getEmail(), authenticationDto.getPassword())
        );
    }
}
