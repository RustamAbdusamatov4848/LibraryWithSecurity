package ru.abdusamatov.librarywithsecurity.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.repository.UserRepository;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

public class UserServiceCacheTest extends TestBase {

    private static final String USER_CACHE = "user";

    @Autowired
    private UserService service;

    @Autowired
    private CacheManager cacheManager;

    @SpyBean
    private UserRepository repository;

    @Override
    protected void clearDatabase() {
        repository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("createUser")
    void shouldCallRepositoryOnce_whenGetUserById(final UserDto dtoToSaved) {
        final var savedUser = service
                .createUser(dtoToSaved)
                .block();

        assertNotNull(savedUser);
        assertUserNotInCache(savedUser.getId());

        final var retrieveUser = service
                .getUserById(savedUser.getId())
                .block();

        assertNotNull(retrieveUser);
        assertUserInCache(retrieveUser);

        final var cachedUser = service
                .getUserById(savedUser.getId())
                .block();

        assertNotNull(cachedUser);
        verify(repository, atMostOnce())
                .findById(savedUser.getId());
    }

    @ParameterizedTest
    @MethodSource("createUser")
    void shouldUpdateCacheUser_whenUpdateUser(final UserDto dtoToSaved) {
        final var savedUser = addSavedEntityToCache(dtoToSaved);

        final var updatedUser = service
                .updateUser(TestDataProvider
                        .updateUserDto(savedUser)
                        .build())
                .block();

        assertNotNull(updatedUser);
        assertUserInCache(updatedUser);
    }

    @ParameterizedTest
    @MethodSource("createUser")
    void shouldDeleteUserFromCache_whenDeleteUser(final UserDto dtoToSaved) {
        final var savedUser = addSavedEntityToCache(dtoToSaved);

        service.deleteUserById(savedUser.getId()).block();

        assertUserNotInCache(savedUser.getId());
    }

    private Cache assertCacheNotNull() {
        final var cache = cacheManager.getCache(USER_CACHE);

        assertNotNull(cache);

        return cache;
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

    private static Stream<Arguments> createUser() {
        final var user = TestDataProvider
                .createUserDto()
                .build();

        return Stream.of(Arguments.arguments(user));
    }

    private UserDto addSavedEntityToCache(final UserDto dtoToSaved) {
        final var savedUser = service
                .createUser(dtoToSaved)
                .block();

        cacheManager.getCache(USER_CACHE).put(savedUser.getId(), savedUser);

        return savedUser;
    }
}
