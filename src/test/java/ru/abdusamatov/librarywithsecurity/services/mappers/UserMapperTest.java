package ru.abdusamatov.librarywithsecurity.services.mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {
    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void shouldMapUserToDto() {
        User user = TestDataProvider.createUser();

        UserDto userDto = mapper.userToDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getBooks().size()).isEqualTo(user.getBooks().size());
        assertEquals(userDto, user);
    }

    @Test
    void shouldMapDtoToUser() {
        UserDto userDto = TestDataProvider.createUserDto();
        userDto.setId(1L);

        User user = mapper.dtoToUser(userDto);

        assertThat(user).isNotNull();
        assertEquals(userDto, user);
    }

    @Test
    void shouldUpdateUserFromDtoWithNonNullBooks() {
        User userToBeUpdated = TestDataProvider.createUser();
        UserDto newUserDto = TestDataProvider.createUserDto();
        newUserDto.setId(userToBeUpdated.getId());

        User updatedUser = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(updatedUser).isNotNull();
        assertEquals(newUserDto, updatedUser);
    }

    @Test
    void shouldNotUpdateUser_whenUserDtoIsNull() {
        User userToBeUpdated = TestDataProvider.createUser();

        User updatedUser = mapper.updateUserFromDto(null, userToBeUpdated);

        assertThat(updatedUser).isSameAs(userToBeUpdated);
    }

    @Test
    void shouldUpdateUserFromDtoWithEmptyBookList() {
        User userToBeUpdated = TestDataProvider.createUser();
        UserDto newUserDto = TestDataProvider.createUserDto();
        newUserDto.setBooks(Collections.emptyList());
        User updatedUser = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(updatedUser.getBooks()).isEmpty();
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

    private static void assertEquals(UserDto userDto, User user) {
        assertThat(userDto).extracting(
                UserDto::getId,
                UserDto::getFullName,
                UserDto::getEmail,
                UserDto::getDateOfBirth
        ).containsExactly(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getDateOfBirth()
        );
    }
}
