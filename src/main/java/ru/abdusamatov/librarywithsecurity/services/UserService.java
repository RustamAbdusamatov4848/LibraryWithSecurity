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
import ru.abdusamatov.librarywithsecurity.util.mappers.UserMapper;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getUserList(Integer page, Integer size) {
        return userRepository
                .findAll(PageRequest.of(page, size))
                .map(userMapper::userToDto)
                .getContent();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository
                .findById(id)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", id));
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User createdUser = userRepository
                .save(userMapper.dtoToUser(userDto));
        log.info("Saving new User with ID: {}", createdUser.getId());
        return userMapper.userToDto(createdUser);
    }

    @Transactional
    public void updateUser(UserDto userDto) {
        Long id = userDto.getId();
        if (!isUserExist(id)) {
            throw new ResourceNotFoundException("User", "ID", id);
        }
        userRepository
                .findById(userDto.getId())
                .map(user -> userMapper.updateUserFromDto(userDto, user))
                .map(userRepository::save);
        log.info("Update user with ID: {}", userDto.getId());

    }

    @Transactional
    public void deleteUserById(Long id) {
        if (!isUserExist(id)) {
            throw new ResourceNotFoundException("User", "ID", id);
        }
        userRepository.deleteById(id);
        log.info("Delete user with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean isUserExist(Long id) {
        return userRepository.existsById(id);
    }
}
