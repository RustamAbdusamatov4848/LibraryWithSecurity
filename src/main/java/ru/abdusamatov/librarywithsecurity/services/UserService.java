package ru.abdusamatov.librarywithsecurity.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
                .findAll(PageRequest.of(page, size, Sort.by("id").ascending()))
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
        User createdUser = userRepository.save(userMapper.dtoToUser(userDto));

        log.info("Saving new User with ID: {}", createdUser.getId());
        return userMapper.userToDto(createdUser);
    }

    @Transactional
    public UserDto updateUser(UserDto dtoToBeUpdated) {
        UserDto updatedUser = userRepository.findById(dtoToBeUpdated.getId())
                .map(user -> userMapper.updateUserFromDto(dtoToBeUpdated, user))
                .map(userRepository::save)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", dtoToBeUpdated.getId()));

        log.info("Updated user with ID: {}", dtoToBeUpdated.getId());
        return updatedUser;
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", id));

        userRepository.delete(user);
        log.info("Deleted user with ID: {}", id);
    }
}
