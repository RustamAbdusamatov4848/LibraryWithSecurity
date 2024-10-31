package ru.abdusamatov.librarywithsecurity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.services.UserService;
import ru.abdusamatov.librarywithsecurity.util.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users",
            produces = {"application/json"}
    )
    public ResponseEntity<ApiResponse<List<UserDto>>> getUserList(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {

        return ResponseEntity.ok(userService.getUserList(page, size));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/users",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/users",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(userDto));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/users/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<ApiResponse<String>> deleteUserByID(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.deleteUserById(id),HttpStatus.NO_CONTENT);
    }
}
