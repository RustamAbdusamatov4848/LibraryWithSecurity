package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.model.Book;
import ru.abdusamatov.librarywithsecurity.model.User;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private final DocumentMapper documentMapper = new DocumentMapperImpl();
    private final BookMapper bookMapper = new BookMapperImpl();
    private final UserMapper mapper = new UserMapperImpl(documentMapper, bookMapper);

    @ParameterizedTest
    @MethodSource("shouldMapUserToDto")
    void shouldMapUserToDto(final User toBeMapped, final UserDto expected) {
        final var actual = mapper.userToDto(toBeMapped);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldMapDtoToUser")
    void shouldMapDtoToUser(final UserDto dtoToBeMapped, final User expected) {
        final var actual = mapper.dtoToUser(dtoToBeMapped);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateUserFromDto")
    void shouldUpdateUserFromDto(
            final UserDto newUserDto,
            final User userToBeUpdated,
            final User expected
    ) {
        final var actual = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateUserFromDto")
    void shouldNotUpdateUser_whenUserDtoIsNull(
            final UserDto newUserDto,
            final User userToBeUpdated,
            final User expected
    ) {
        final var actual = mapper.updateUserFromDto(null, userToBeUpdated);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(userToBeUpdated);
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateUserFromDto")
    void shouldUpdateUserFromDto_whenUserHasNullBookList(
            UserDto newUserDto,
            User userToBeUpdated,
            final User expected
    ) {
        final var listSize = 10;
        newUserDto.setBooks(TestDataProvider.createListBookDto(listSize));
        userToBeUpdated.setBooks(null);

        final var actual = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(actual.getBooks())
                .isNotNull()
                .hasSize(listSize);
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateUserFromDto")
    void shouldUpdateUserFromDto_whenDtoHasNullBookList(
            UserDto newUserDto,
            User userToBeUpdated,
            final User expected
    ) {
        final var listSize = 10;
        userToBeUpdated.setBooks(TestDataProvider.createListBook(listSize));
        newUserDto.setBooks(null);

        final var actual = mapper.updateUserFromDto(newUserDto, userToBeUpdated);

        assertThat(actual.getBooks())
                .isNull();
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateUserFromDto")
    void shouldReturnNull_whenUserDtoIsNullInUpdateUserFromDto(
            UserDto newUserDto,
            User userToBeUpdated,
            final User expected
    ) {
        final var updatedUser = mapper.updateUserFromDto(null, userToBeUpdated);

        assertThat(updatedUser)
                .isEqualTo(userToBeUpdated);
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

    public static Stream<Arguments> shouldMapUserToDto() {
        final var user = TestDataProvider.createUser();
        final var document = user.getDocument();

        final var expected = TestDataProvider
                .createUserDto()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .books(Collections.emptyList())
                .documentDto(TestDataProvider.createDocumentDto()
                        .id(document.getId())
                        .bucketName(document.getBucketName())
                        .fileName(document.getFileName())
                        .userId(document.getOwner().getId())
                        .build())
                .build();

        return Stream.of(Arguments.arguments(user, expected));
    }

    public static Stream<Arguments> shouldMapDtoToUser() {
        final var dtoToBeMapped = TestDataProvider
                .createUserDto()
                .books(Collections.emptyList())
                .build();
        final var document = dtoToBeMapped.getDocumentDto();

        final var expected = User.builder()
                .id(dtoToBeMapped.getId())
                .fullName(dtoToBeMapped.getFullName())
                .email(dtoToBeMapped.getEmail())
                .dateOfBirth(dtoToBeMapped.getDateOfBirth())
                .books(Collections.emptyList())
                .document(TestDataProvider.createDocument()
                        .id(document.getId())
                        .bucketName(document.getBucketName())
                        .fileName(document.getFileName())
                        .owner(User.builder().id(document.getUserId()).build())
                        .build())
                .build();

        return Stream.of(Arguments.arguments(dtoToBeMapped, expected));
    }

    public static Stream<Arguments> shouldUpdateUserFromDto() {
        final var existingUser = TestDataProvider.createUser();
        final var document = existingUser.getDocument();

        final var newDto = TestDataProvider
                .createUserDto()
                .id(existingUser.getId())
                .fullName("Updated FullName")
                .email("updated@example.com")
                .dateOfBirth(existingUser.getDateOfBirth())
                .books(Collections.emptyList())
                .documentDto(TestDataProvider.createDocumentDto()
                        .id(document.getId())
                        .bucketName(document.getBucketName())
                        .fileName(document.getFileName())
                        .userId(document.getOwner().getId())
                        .build())
                .build();

        final var expected = User.builder()
                .id(newDto.getId())
                .fullName(newDto.getFullName())
                .email(newDto.getEmail())
                .dateOfBirth(existingUser.getDateOfBirth())
                .books(newDto
                        .getBooks()
                        .stream()
                        .map(UserMapperTest::getBook)
                        .toList())
                .document(TestDataProvider
                        .createDocument()
                        .id(document.getId())
                        .fileName(document.getFileName())
                        .bucketName(document.getBucketName())
                        .owner(existingUser)
                        .build())
                .build();

        return Stream.of(Arguments.arguments(newDto, existingUser, expected));
    }

    private static Book getBook(BookDto bookDto) {
        return TestDataProvider.createBook()
                .id(bookDto.getId())
                .title(bookDto.getTitle())
                .authorName(bookDto.getAuthorName())
                .authorSurname(bookDto.getAuthorSurname())
                .takenAt(bookDto.getTakenAt())
                .yearOfPublication(bookDto.getYearOfPublication())
                .owner(User.builder()
                        .id(bookDto.getUserId())
                        .build())
                .build();
    }
}
