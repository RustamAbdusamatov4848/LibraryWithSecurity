package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.repository.UserRepository;
import ru.abdusamatov.librarywithsecurity.service.mapper.DocumentMapper;
import ru.abdusamatov.librarywithsecurity.service.mapper.UserMapper;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "user")
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DocumentMapper documentMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getUserList(final Integer page, final Integer size) {
        return userRepository
                .findAll(PageRequest.of(page, size, Sort.by("id").ascending()))
                .getContent()
                .stream()
                .map(userMapper::userToDto)
                .toList();
    }

    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public UserDto getUserById(final Long id) {
        return userRepository.findById(id)
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", id));
    }

    @Transactional
    public UserDto createUser(final UserDto dto) {
        final var document = documentMapper.dtoToDocument(dto.getDocumentDto());
        final var user = userMapper.dtoToUser(dto);
        user.setDocument(document);

        final var createdUser = userRepository.save(user);

        log.info("Saving new User with ID: {}", createdUser.getId());
        return userMapper.userToDto(createdUser);
    }

    @CachePut(key = "#dtoToBeUpdated.id")
    @Transactional
    public UserDto updateUser(final UserDto dtoToBeUpdated) {
        final var updatedUser = userRepository.findById(dtoToBeUpdated.getId())
                .map(user -> {
                    final var updatedUserEntity = userMapper.updateUserFromDto(dtoToBeUpdated, user);

                    return userRepository.save(updatedUserEntity);
                })
                .map(userMapper::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", dtoToBeUpdated.getId()));

        log.info("Updated user with ID: {}", dtoToBeUpdated.getId());
        return updatedUser;
    }

    @CacheEvict(key = "#id")
    @Transactional
    public void deleteUserById(final Long id) {
        final var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", id));

        userRepository.delete(user);
        log.info("Deleted user with ID: {}", id);
    }
}
