package ru.abdusamatov.librarywithsecurity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.dto.response.Result;
import ru.abdusamatov.librarywithsecurity.service.UserService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Response<List<UserDto>> getUserList(
            @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") final Integer size) {

        return Response.buildResponse(
                Result.success(OK, "List of users"),
                userService.getUserList(page, size)
        );
    }

    @GetMapping(value = "/{id}")
    public Response<UserDto> getUserById(@PathVariable("id") final Long id) {
        return Response.buildResponse(
                Result.success(OK, "User successfully found"),
                userService.getUserById(id)
        );
    }

    @PostMapping
    public Response<UserDto> createUser(@Valid @RequestBody final UserDto userDto) {
        return Response.buildResponse(
                Result.success(CREATED, "User successfully saved"),
                userService.createUser(userDto)
        );
    }

    @PutMapping
    public Response<UserDto> updateUser(@Valid @RequestBody final UserDto userDto) {
        return Response.buildResponse(
                Result.success(OK, "User successfully updated"),
                userService.updateUser(userDto)
        );
    }

    @DeleteMapping(value = "/{id}")
    public Response<Void> deleteUserByID(@PathVariable("id") final Long id) {
        userService.deleteUserById(id);
        return Response.buildResponse(Result.success(NO_CONTENT, "Successfully deleted"), null);
    }
}
