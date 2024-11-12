package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.model.User;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void shouldMapToDto() {
        User user = TestDataProvider.createUser();

        UserDto userDto = mapper.userToDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getBooks().size()).isEqualTo(user.getBooks().size());
        assertUsersAreEqual(userDto, user);
    }

    @Test
    void shouldMapDtoToUser() {
        UserDto userDto = TestDataProvider.createUserDto();
        userDto.setId(1L);

        User user = mapper.dtoToUser(userDto);

        assertThat(user).isNotNull();
        assertUsersAreEqual(userDto, user);
    }

    @Test
    void shouldUpdateUserFromDto_whenBooksNotNull() {
        User userToBeUpdated = TestDataProvider.createUser();
        UserDto newUserDto = TestDataProvider.createUserDto();
        newUserDto.setId(userToBeUpdated.getId());

        User updatedUser = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(updatedUser).isNotNull();
        assertUsersAreEqual(newUserDto, updatedUser);
    }

    @Test
    void shouldNotUpdateUser_whenUserDtoIsNull() {
        User userToBeUpdated = TestDataProvider.createUser();

        User updatedUser = mapper.updateUserFromDto(null, userToBeUpdated);

        assertThat(updatedUser).isEqualTo(userToBeUpdated);
    }

    @Test
    void shouldUpdateUserFromDto_whenBookListIsEmpty() {
        User userToBeUpdated = TestDataProvider.createUser();
        UserDto newUserDto = TestDataProvider.createUserDto();
        newUserDto.setBooks(Collections.emptyList());

        User updatedUser = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(updatedUser.getBooks()).isEmpty();
    }

    @Test
    void shouldUpdateUserFromDto_whenUserHasNullBookList() {
        int listSize = 10;
        User userToBeUpdated = TestDataProvider.createUser();
        userToBeUpdated.setBooks(null);
        UserDto newUserDto = TestDataProvider.createUserDto();
        newUserDto.setBooks(TestDataProvider.createListBookDto(listSize));

        User updatedUser = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(updatedUser.getBooks())
                .isNotNull()
                .isNotEmpty()
                .hasSize(listSize);
    }

    @Test
    void shouldUpdateUserFromDto_whenDtoHasNullBookList() {
        int listSize = 10;

        User userToBeUpdated = TestDataProvider.createUser();
        userToBeUpdated.setBooks(TestDataProvider.createListBook(listSize));

        UserDto newUserDto = TestDataProvider.createUserDto();
        newUserDto.setBooks(null);

        User updatedUser = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(updatedUser.getBooks()).isNull();
    }

    @Test
    void shouldReturnNull_whenUserIsNullInUserToUserDto() {
        UserDto userDto = mapper.userToDto(null);
        assertThat(userDto).isNull();
    }

    @Test
    void shouldReturnNull_whenUserDtoIsNullInUserDtoToUser() {
        User user = mapper.dtoToUser(null);
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
