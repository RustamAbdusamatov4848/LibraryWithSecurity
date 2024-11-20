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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserServiceTest extends TestBase {

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
    void shouldCallRepositoryOnce_whenGetUserByIdUsesCache() {
        final var savedUser = createAndCacheUser();
        final var id = savedUser.getId();

        service.getUserById(id);

        var cache = cacheManager.getCache("user");
        assertThat(cache)
                .isNotNull();

        service.getUserById(id);

        verify(repository, atMostOnce())
                .findById(id);
        assertUserInCache(savedUser);
    }

    @Test
    void shouldUpdateCacheUser_whenUpdateUserUpdatesCache() {
        final var savedUser = createAndCacheUser();
        final var updatedUser = TestDataProvider
                .updateUserDto(savedUser)
                .build();

        service.updateUser(updatedUser);

        assertUserInCache(updatedUser);
        verify(repository, times(1))
                .findById(savedUser.getId());
        verify(repository, times(2))
                .save(any());
    }

    @Test
    void shouldDeleteUserFromCache_whenDeleteUserEvictsCache() {
        final var savedUser = createAndCacheUser();

        service.deleteUserById(savedUser.getId());

        assertUserNotInCache(savedUser);
        verify(repository, times(1))
                .findById(savedUser.getId());
        verify(repository, times(1))
                .delete(any());
    }

    private UserDto createAndCacheUser() {
        final var user = TestDataProvider
                .createUserDto()
                .build();

        return service.createUser(user);
    }

    private void assertUserInCache(final UserDto expectedUser) {
        final var cache = cacheManager.getCache("user");
        assertThat(cache.get(expectedUser.getId(), UserDto.class))
                .isNotNull()
                .extracting(UserDto::getId)
                .isEqualTo(expectedUser.getId());
    }

    private void assertUserNotInCache(final UserDto user) {
        final var cache = cacheManager.getCache("user");

        assertThat(cache)
                .isNotNull();
        assertThat(cache.get(user.getId()))
                .isNull();
    }
}
