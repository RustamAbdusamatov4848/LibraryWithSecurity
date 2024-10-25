package ru.abdusamatov.librarywithsecurity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.exceptions.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.services.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users",
            produces = {"application/json"})
    public ResponseEntity<List<UserDto>> getUserList(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        List<UserDto> userDtoList = userService.getUserList(pageable).getContent();
        return ResponseEntity.ok(userDtoList);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id)
                .map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }


    @RequestMapping(
            method = RequestMethod.POST,
            value = "/users",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUserDto = userService.createUser(userDto);
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/users",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto) {
        userService.updateUser(userDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/users/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteUserByID(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
