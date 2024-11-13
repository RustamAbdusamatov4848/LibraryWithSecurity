package ru.abdusamatov.librarywithsecurity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.abdusamatov.librarywithsecurity.dto.AuthenticationDto;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.service.LibrarianService;
import ru.abdusamatov.librarywithsecurity.util.Response;
import ru.abdusamatov.librarywithsecurity.util.Result;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

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
    public Response<LibrarianDto> createLibrarian(@Valid @RequestBody final LibrarianDto librarianDto) {
        return Response.buildResponse(
                Result.success(CREATED, "Librarian was created"),
                librarianService.createLibrarian(librarianDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/lib/login",
            consumes = "application/json",
            produces = "application/json"
    )
    public Response<Void> login(@Valid @RequestBody final AuthenticationDto authenticationDto) {
        librarianService.validateLibrarian(authenticationDto);
        return Response.buildResponse(Result.success(NO_CONTENT, "Successful validation"), null);
    }
}
