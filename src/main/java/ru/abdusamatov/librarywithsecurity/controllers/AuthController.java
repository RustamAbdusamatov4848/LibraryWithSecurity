package ru.abdusamatov.librarywithsecurity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.abdusamatov.librarywithsecurity.dto.AuthenticationDto;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.services.LibrarianService;
import ru.abdusamatov.librarywithsecurity.util.ApiResponse;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final LibrarianService librarianService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/lib/registration",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<ApiResponse<LibrarianDto>> createLibrarian(@Valid @RequestBody LibrarianDto librarianDto) {
        return new ResponseEntity<>(librarianService.createLibrarian(librarianDto), HttpStatus.CREATED);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/lib/login",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody AuthenticationDto authenticationDto) {
        return new ResponseEntity<>(librarianService.validateLibrarian(authenticationDto), HttpStatus.OK);
    }
}
