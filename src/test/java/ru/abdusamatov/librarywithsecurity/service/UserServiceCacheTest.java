package ru.abdusamatov.librarywithsecurity.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.cache.Cache;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.model.User;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class UserServiceCacheTest extends TestBase {

    private static final String USER_CACHE = "user";

    @Override
    protected void clearDatabase() {
        spyUserRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(spyUserRepository);
    }

    @ParameterizedTest
    @MethodSource("createUser")
    void shouldCallRepositoryOnce_whenGetUserById(final UserDto dtoToSaved) {
        final var savedUser = userService.createUser(dtoToSaved);
        assertUserNotInCache(savedUser.getId());

        final var retrieveUser = userService
                .getUserById(savedUser.getId());
        assertUserInCache(retrieveUser);

        userService.getUserById(savedUser.getId());

        verify(spyUserRepository)
                .save(any(User.class));
        verify(spyUserRepository)
                .findById(savedUser.getId());
    }

    @ParameterizedTest
    @MethodSource("createUser")
    void shouldUpdateCacheUser_whenUpdateUser(final UserDto dtoToSaved) {
        final var savedUser = addSavedEntityToCache(dtoToSaved);

        final var updatedUser = userService
                .updateUser(TestDataProvider
                        .updateUserDto(savedUser)
                        .build());

        assertUserInCache(updatedUser);
        verify(spyUserRepository, times(2))
                .save(any(User.class));
        verify(spyUserRepository)
                .findById(updatedUser.getId());
    }

    @ParameterizedTest
    @MethodSource("createUser")
    void shouldDeleteUserFromCache_whenDeleteUser(final UserDto dtoToSaved) {
        final var savedUser = addSavedEntityToCache(dtoToSaved);

        userService.deleteUserById(savedUser.getId());

        assertUserNotInCache(savedUser.getId());
        verify(spyUserRepository)
                .save(any(User.class));
        verify(spyUserRepository)
                .findById(savedUser.getId());
        verify(spyUserRepository)
                .delete(any(User.class));
    }

    private void assertUserInCache(final UserDto expectedUser) {
        final var cache = assertCacheNotNull();

        assertThat(cache.get(expectedUser.getId(), UserDto.class))
                .isNotNull()
                .extracting(UserDto::getId)
                .isEqualTo(expectedUser.getId());
    }

    private void assertUserNotInCache(final Long id) {
        final var cache = assertCacheNotNull();

        assertThat(cache.get(id))
                .isNull();
    }

    private Cache assertCacheNotNull() {
        final var cache = cacheManager.getCache(USER_CACHE);

        assertNotNull(cache);

        return cache;
    }

    private static Stream<Arguments> createUser() {
        final var user = TestDataProvider
                .createUserDto()
                .build();

        return Stream.of(Arguments.arguments(user));
    }

    private UserDto addSavedEntityToCache(final UserDto dtoToSaved) {
        final var savedUser = userService.createUser(dtoToSaved);

        cacheManager.getCache(USER_CACHE).put(savedUser.getId(), savedUser);

        return savedUser;
    }
}
