package ru.abdusamatov.librarywithsecurity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.abdusamatov.librarywithsecurity.dto.AuthenticationDto;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.services.LibrarianService;
import ru.abdusamatov.librarywithsecurity.util.ApiResponse;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final LibrarianService librarianService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/lib/registration"
    )
    public ResponseEntity<ApiResponse> createLibrarian(@Valid @RequestBody LibrarianDto librarianDto) {
        return new ResponseEntity<>(librarianService.createLibrarian(librarianDto), HttpStatus.CREATED);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "lib/login"
    )
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody AuthenticationDto authenticationDto) {
        return new ResponseEntity<>(librarianService.validateLibrarian(authenticationDto), HttpStatus.OK);
    }
}
