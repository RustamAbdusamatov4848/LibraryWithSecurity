package ru.abdusamatov.librarywithsecurity.services.mappers;

import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void shouldMapToDto() {
        final var user = TestDataProvider.createUser().build();

        final var userDto = mapper.userToDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getBooks().size()).isEqualTo(user.getBooks().size());
        assertUsersAreEqual(userDto, user);
    }

    @Test
    void shouldMapDtoToUser() {
        final var userDto = TestDataProvider.createUserDto().id(1L).build();

        final var user = mapper.dtoToUser(userDto);

        assertThat(user).isNotNull();
        assertUsersAreEqual(userDto, user);
    }

    @Test
    void shouldUpdateUserFromDto_whenBooksNotNull() {
        final var userToBeUpdated = TestDataProvider.createUser().build();
        final var newUserDto = TestDataProvider.createUserDto().id(userToBeUpdated.getId()).build();

        final var updatedUser = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(updatedUser).isNotNull();
        assertUsersAreEqual(newUserDto, updatedUser);
    }

    @Test
    void shouldNotUpdateUser_whenUserDtoIsNull() {
        final var userToBeUpdated = TestDataProvider.createUser().build();

        final var updatedUser = mapper.updateUserFromDto(null, userToBeUpdated);

        assertThat(updatedUser).isEqualTo(userToBeUpdated);
    }

    @Test
    void shouldUpdateUserFromDto_whenBookListIsEmpty() {
        final var userToBeUpdated = TestDataProvider.createUser().build();
        final var newUserDto = TestDataProvider.createUserDto().books(Collections.emptyList()).build();

        final var updatedUser = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(updatedUser.getBooks()).isEmpty();
    }

    @Test
    void shouldUpdateUserFromDto_whenUserHasNullBookList() {
        final var listSize = 10;
        final var userToBeUpdated = TestDataProvider.createUser().books(null).build();
        final var newUserDto = TestDataProvider
                .createUserDto()
                .books(TestDataProvider.createListBookDto(listSize))
                .build();

        final var updatedUser = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(updatedUser.getBooks())
                .isNotNull()
                .isNotEmpty()
                .hasSize(listSize);
    }

    @Test
    void shouldUpdateUserFromDto_whenDtoHasNullBookList() {
        final var listSize = 10;
        final var userToBeUpdated = TestDataProvider
                .createUser()
                .books(TestDataProvider.createListBook(listSize))
                .build();

        final var newUserDto = TestDataProvider.createUserDto().books(null).build();

        final var updatedUser = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(updatedUser.getBooks()).isNull();
    }

    @Test
    void shouldReturnNull_whenUserIsNullInUserToUserDto() {
        final var userDto = mapper.userToDto(null);
        assertThat(userDto).isNull();
    }

    @Test
    void shouldReturnNull_whenUserDtoIsNullInUserDtoToUser() {
        final var user = mapper.dtoToUser(null);
        assertThat(user).isNull();
    }

    private static void assertUsersAreEqual(UserDto userDto, User user) {
        assertThat(userDto)
                .withFailMessage(() -> "Users are not equal")
                .usingRecursiveComparison()
                .ignoringFields("books")
                .isEqualTo(user);
    }
}
