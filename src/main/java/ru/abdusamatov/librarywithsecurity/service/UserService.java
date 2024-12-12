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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.repository.UserRepository;
import ru.abdusamatov.librarywithsecurity.service.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "user")
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    public Mono<List<UserDto>> getUserList(final Integer page, final Integer size) {
        return Mono.fromCallable(() ->
                        userRepository.findAll(PageRequest.of(page, size, Sort.by("id").ascending())))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(pageResult -> {
                    if (pageResult.hasContent()) {
                        return Mono.just(pageResult.getContent()
                                .stream()
                                .map(mapper::userToDto)
                                .collect(Collectors.toList()));
                    } else {
                        return Mono.empty();
                    }
                });
    }


    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public Mono<UserDto> getUserById(final Long id) {
        return Mono.fromCallable(() ->
                        userRepository.findById(id)
                                .map(mapper::userToDto)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public Mono<UserDto> createUser(final UserDto dto) {
        return Mono.fromCallable(() ->
                        userRepository.save(mapper.dtoToUser(dto)))
                .doOnNext(savedUser -> log.info("Saving new User with ID: {}", savedUser.getId()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(mapper::userToDto);
    }


    @CachePut(key = "#dtoToBeUpdated.id")
    @Transactional
    public Mono<UserDto> updateUser(final UserDto dtoToBeUpdated) {
        return Mono.fromCallable(() ->
                        userRepository
                                .findById(dtoToBeUpdated.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", dtoToBeUpdated.getId())))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(user -> {
                    final var updatedUserEntity = mapper.updateUserFromDto(dtoToBeUpdated, user);
                    return Mono.fromCallable(() -> userRepository.save(updatedUserEntity))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .map(mapper::userToDto)
                .doOnSuccess(userDto -> log.info("Updated user with ID: {}", userDto.getId()));
    }

    @CacheEvict(key = "#id")
    @Transactional
    public Mono<Void> deleteUserById(final Long id) {
        return Mono.fromCallable(() ->
                        userRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalUser -> optionalUser
                        .map(user -> Mono.fromRunnable(() -> userRepository.delete(user))
                                .subscribeOn(Schedulers.boundedElastic()))
                        .orElseGet(() -> Mono.error(new ResourceNotFoundException("User", "ID", id))))
                .doOnSuccess(aVoid -> log.info("Deleted user with ID: {}", id))
                .then();
    }
}
