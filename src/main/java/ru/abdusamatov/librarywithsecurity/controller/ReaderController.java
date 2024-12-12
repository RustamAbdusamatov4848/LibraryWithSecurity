package ru.abdusamatov.librarywithsecurity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.dto.response.Result;
import ru.abdusamatov.librarywithsecurity.service.ReaderService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class ReaderController {
    private final ReaderService readerService;

    @GetMapping
    public Mono<Response<List<UserDto>>> getUserList(
            @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") final Integer size) {

        return readerService
                .getUserList(page, size)
                .map(users -> Response.buildResponse(
                        Result.success(OK, "List of users"),
                        users));
    }

    @GetMapping(value = "/{id}")
    public Mono<Response<UserDto>> getUserById(@PathVariable("id") final Long id) {
        return readerService
                .getUserById(id)
                .map(user -> Response.buildResponse(
                        Result.success(OK, "User successfully found"),
                        user));
    }

    @GetMapping(value = "/{id}/document")
    public Mono<Response<MultiValueMap<String, Object>>> getUserDocument(@PathVariable("id") final Long id) {
        return readerService
                .getDocument(id)
                .map(document -> Response.buildResponse(
                        Result.success(OK, "User document successfully found"),
                        document));
    }

    @PostMapping
    public Mono<Response<UserDto>> createUser(@RequestParam("file") final MultipartFile file,
                                              @Valid @RequestBody final UserDto userDto) {
        return readerService
                .createUser(file, userDto)
                .map(user -> Response.buildResponse(
                        Result.success(CREATED, "User successfully saved"),
                        user));
    }


    @PutMapping
    public Mono<Response<UserDto>> updateUser(@Valid @RequestBody final UserDto userDto) {
        return readerService
                .updateUser(userDto)
                .map(updatedUser -> Response.buildResponse(
                        Result.success(OK, "User successfully updated"),
                        updatedUser));
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Response<Void>> deleteUserByID(@PathVariable("id") final Long id) {
        return readerService
                .deleteUserById(id)
                .then(Mono.just(Response.buildResponse(
                        Result.success(NO_CONTENT, "Successfully deleted"),
                        null)));
    }
}
