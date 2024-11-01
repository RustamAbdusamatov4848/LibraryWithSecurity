package ru.abdusamatov.librarywithsecurity.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.exceptions.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;
import ru.abdusamatov.librarywithsecurity.util.Response;
import ru.abdusamatov.librarywithsecurity.util.Result;
import ru.abdusamatov.librarywithsecurity.util.mappers.UserMapper;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Response<List<UserDto>> getUserList(Integer page, Integer size) {
        List<UserDto> userDtoList = userRepository
                .findAll(PageRequest.of(page, size))
                .map(userMapper::userToDto)
                .getContent();

        return Response.buildResponse(Result.success(OK, "List of users"), userDtoList);
    }

    @Transactional(readOnly = true)
    public Response<UserDto> getUserById(Long id) {
        UserDto foundUser = userRepository
                .findById(id)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", id));

        return Response.buildResponse(Result.success(OK, "User successfully found"), foundUser);
    }

    @Transactional
    public Response<UserDto> createUser(UserDto userDto) {
        User createdUser = userRepository.save(userMapper.dtoToUser(userDto));

        log.info("Saving new User with ID: {}", createdUser.getId());
        return Response.buildResponse(Result.success(CREATED, "User successfully saved"),
                userMapper.userToDto(createdUser));
    }

    @Transactional
    public Response<UserDto> updateUser(UserDto dtoToBeUpdated) {
        UserDto updatedUser = userRepository.findById(dtoToBeUpdated.getId())
                .map(user -> userMapper.updateUserFromDto(dtoToBeUpdated, user))
                .map(userRepository::save)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", dtoToBeUpdated.getId()));

        log.info("Updated user with ID: {}", dtoToBeUpdated.getId());
        return Response.buildResponse(Result.success(OK, "User successfully updated"), updatedUser);
    }

    @Transactional
    public Response<Void> deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", id));

        userRepository.delete(user);

        log.info("Deleted user with ID: {}", id);
        return Response.buildResponse(Result.success(OK, "Successfully deleted"), null);
    }
}
