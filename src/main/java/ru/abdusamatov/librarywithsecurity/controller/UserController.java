package ru.abdusamatov.librarywithsecurity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "user")
public class UserController {
    private final UserService userService;

    @GetMapping(
            produces = {"application/json"}
    )
    public Response<List<UserDto>> getUserList(
            @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") final Integer size) {

        return Response.buildResponse(
                Result.success(OK, "List of users"),
                userService.getUserList(page, size)
        );
    }

    @Cacheable(key = "#id")
    @GetMapping(
            value = "/{id}",
            produces = {"application/json"}
    )
    public Response<UserDto> getUserById(@PathVariable("id") final Long id) {
        return Response.buildResponse(
                Result.success(OK, "User successfully found"),
                userService.getUserById(id)
        );
    }

    @PostMapping(
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Response<UserDto> createUser(@Valid @RequestBody final UserDto userDto) {
        return Response.buildResponse(
                Result.success(CREATED, "User successfully saved"),
                userService.createUser(userDto)
        );
    }


    @CachePut(key = "#userDto.id")
    @PutMapping(
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Response<UserDto> updateUser(@Valid @RequestBody final UserDto userDto) {
        return Response.buildResponse(
                Result.success(OK, "User successfully updated"),
                userService.updateUser(userDto)
        );
    }

    @CacheEvict(key = "#id")
    @DeleteMapping(
            value = "/{id}",
            produces = {"application/json"}
    )
    public Response<Void> deleteUserByID(@PathVariable("id") final Long id) {
        userService.deleteUserById(id);
        return Response.buildResponse(Result.success(NO_CONTENT, "Successfully deleted"), null);
    }
}
