package ru.abdusamatov.librarywithsecurity.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.repository.UserRepository;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

public class UserServiceTest extends TestBase {

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

    @Test
    void shouldCallRepositoryOnce_whenGetUserById() {
        final var savedUser = createAndCacheUser();
        final var id = savedUser.getId();

        service.getUserById(id);
        final var cache = cacheManager.getCache(USER_CACHE);
        assertThat(cache)
                .isNotNull();
        service.getUserById(id);

        assertUserInCache(savedUser);
        verify(repository, atMostOnce())
                .findById(id);
    }

    @Test
    void shouldUpdateCacheUser_whenUpdateUser() {
        final var savedUser = createAndCacheUser();
        cacheManager.getCache(USER_CACHE).put(savedUser.getId(), savedUser);

        final var updatedUser = TestDataProvider
                .updateUserDto(savedUser)
                .build();

        service.updateUser(updatedUser);

        assertUserInCache(updatedUser);
    }

    @Test
    void shouldDeleteUserFromCache_whenDeleteUser() {
        final var savedUser = createAndCacheUser();

        service.deleteUserById(savedUser.getId());

        final var cache = cacheManager.getCache(USER_CACHE);

        assertThat(cache)
                .isNotNull();
        assertThat(cache.get(savedUser.getId()))
                .isNull();
    }

    private UserDto createAndCacheUser() {
        final var user = TestDataProvider
                .createUserDto()
                .build();

        return service.createUser(user);
    }

    private void assertUserInCache(final UserDto expectedUser) {
        final var cache = cacheManager.getCache(USER_CACHE);

        assertThat(cache.get(expectedUser.getId(), UserDto.class))
                .isNotNull()
                .extracting(UserDto::getId)
                .isEqualTo(expectedUser.getId());
    }
}
