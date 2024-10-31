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
import ru.abdusamatov.librarywithsecurity.util.ApiResponse;
import ru.abdusamatov.librarywithsecurity.util.Response;
import ru.abdusamatov.librarywithsecurity.util.mappers.UserMapper;

import java.util.List;

import static ru.abdusamatov.librarywithsecurity.util.ApiResponseStatus.SUCCESS;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public ApiResponse<List<UserDto>> getUserList(Integer page, Integer size) {
        List<UserDto> userDtoList = userRepository
                .findAll(PageRequest.of(page, size))
                .map(userMapper::userToDto)
                .getContent();

        return buildApiResponse("List of users", userDtoList);
    }

    @Transactional(readOnly = true)
    public ApiResponse<UserDto> getUserById(Long id) {
        UserDto foundUser = userRepository
                .findById(id)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", id));

        return buildApiResponse("User successfully found", foundUser);
    }

    @Transactional
    public ApiResponse<UserDto> createUser(UserDto userDto) {
        User createdUser = userRepository.save(userMapper.dtoToUser(userDto));
        log.info("Saving new User with ID: {}", createdUser.getId());
        return buildApiResponse("User successfully saved", userMapper.userToDto(createdUser));
    }

    @Transactional
    public ApiResponse<UserDto> updateUser(UserDto dtoToBeUpdated) {
        UserDto updatedUser = userRepository.findById(dtoToBeUpdated.getId())
                .map(user -> userMapper.updateUserFromDto(dtoToBeUpdated, user))
                .map(userRepository::save)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", dtoToBeUpdated.getId()));

        log.info("Updated user with ID: {}", dtoToBeUpdated.getId());
        return buildApiResponse("User successfully updated", updatedUser);
    }

    @Transactional
    public ApiResponse<String> deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", id));

        userRepository.delete(user);
        log.info("Deleted user with ID: {}", id);
        return buildApiResponse("Successfully deleted", "User successfully deleted");
    }

    private <T> ApiResponse<T> buildApiResponse(String message, T data) {
        return new ApiResponse<>(message, new Response<>(SUCCESS, data));
    }
}
