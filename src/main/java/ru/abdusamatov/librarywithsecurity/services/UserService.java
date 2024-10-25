package ru.abdusamatov.librarywithsecurity.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;
import ru.abdusamatov.librarywithsecurity.util.mappers.UserMapper;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserDto> getUserList(Pageable pageable) {
        return userRepository
                .findAll(pageable)
                .map(userMapper::userToDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByID(Long id) {
        return userRepository
                .findById(id)
                .map(userMapper::userToDto);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User createdUser = userRepository
                .save(userMapper.dtoToUser(userDto));
        log.info("Saving new User with ID: {}", createdUser.getId());
        return userMapper.userToDto(createdUser);
    }

    @Transactional
    public void editPerson(UserDto userDto) {
        userRepository
                .findById(userDto.getId())
                .map(user -> userMapper.updateUserFromDto(userDto, user))
                .map(userRepository::save);
        log.info("Update user with ID: {}", userDto.getId());

    }

    @Transactional
    public void deleteUserByID(Long id) {
        log.info("Delete user with ID: {}", id);
    }
}
