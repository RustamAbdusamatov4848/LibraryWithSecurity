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
import ru.abdusamatov.librarywithsecurity.errors.ErrorResponse;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;
import ru.abdusamatov.librarywithsecurity.util.ApiResponse;
import ru.abdusamatov.librarywithsecurity.util.ApiResponseStatus;
import ru.abdusamatov.librarywithsecurity.util.Response;
import ru.abdusamatov.librarywithsecurity.util.mappers.LibrarianMapper;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibrarianService {
    private final LibrarianRepository librarianRepository;
    private final PasswordEncoder passwordEncoder;
    private final LibrarianMapper librarianMapper;
    private AuthenticationManager authenticationManager;

    @Transactional
    public ApiResponse createLibrarian(LibrarianDto librarianDto) {

        if (librarianRepository.existsByEmail(librarianDto.getEmail())) {
            ErrorResponse errorResponse = new ErrorResponse("Email is already taken",
                    Map.of("cause", librarianDto.getEmail()));
            Response<ErrorResponse> response = new Response<>(ApiResponseStatus.ERROR, errorResponse);
            return new ApiResponse("Email error", response);
        }

        Librarian librarianFromDto = librarianMapper.librarianDtoToLibrarian(librarianDto);
        librarianFromDto.setPassword(passwordEncoder.encode(librarianDto.getPassword()));
        Librarian savedLibrarian = librarianRepository.save(librarianFromDto);
        log.info("Saving new Librarian with ID: {}", savedLibrarian.getId());
        Response<LibrarianDto> response =
                new Response<>(ApiResponseStatus.SUCCESS, librarianMapper.librarianToLibrarianDto(savedLibrarian));
        return new ApiResponse("Librarian was created", response);
    }

    public ApiResponse validateLibrarian(AuthenticationDto authenticationDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationDto.getEmail(), authenticationDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Response<String> response = new Response<>(ApiResponseStatus.SUCCESS);

        return new ApiResponse("Successful validation", response);
    }
}
