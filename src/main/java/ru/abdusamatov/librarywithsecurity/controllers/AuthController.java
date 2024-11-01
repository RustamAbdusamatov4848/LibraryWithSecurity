package ru.abdusamatov.librarywithsecurity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.abdusamatov.librarywithsecurity.dto.AuthenticationDto;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.services.LibrarianService;
import ru.abdusamatov.librarywithsecurity.util.Response;

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
    public Response<LibrarianDto> createLibrarian(@Valid @RequestBody LibrarianDto librarianDto) {
        return librarianService.createLibrarian(librarianDto);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/lib/login",
            consumes = "application/json",
            produces = "application/json"
    )
    public Response<Void> login(@Valid @RequestBody AuthenticationDto authenticationDto) {
        return librarianService.validateLibrarian(authenticationDto);
    }
}
