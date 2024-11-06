package ru.abdusamatov.librarywithsecurity.support;

import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TestDataProvider {
    public static final int MAX_NAME_LENGTH = 30;
    public static final String LONG_NAME = "a".repeat(MAX_NAME_LENGTH + 1);
    public static final int INVALID_YEAR_OF_PUBLICATION = 1499;
    public static final String INVALID_EMAIL = "invalid-email";
    public static final LocalDate INVALID_DATA_OF_BIRTH = LocalDate.now().plusDays(1);

    public static BookDto createBookDto() {
        return BookDto.builder()
                .title("Book Title" + getLimitUUID())
                .authorName("AuthorName")
                .authorSurname("AuthorSurname")
                .yearOfPublication(2020)
                .build();
    }

    public static BookDto createInvalidBookDto() {
        return BookDto.builder()
                .title("Book Title" + getLimitUUID())
                .authorName(LONG_NAME)
                .authorSurname(LONG_NAME)
                .yearOfPublication(INVALID_YEAR_OF_PUBLICATION)
                .build();
    }

    public static BookDto updatedBookDto(BookDto bookToBeUpdated) {
        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(bookToBeUpdated.getId());
        updatedBookDto.setTitle("Title updated ");
        updatedBookDto.setAuthorName("Author name updated");
        updatedBookDto.setAuthorSurname("Author surname updated");
        updatedBookDto.setYearOfPublication(bookToBeUpdated.getYearOfPublication());
        updatedBookDto.setUserId(bookToBeUpdated.getUserId());
        updatedBookDto.setTakenAt(bookToBeUpdated.getTakenAt());

        return updatedBookDto;
    }

    public static BookDto updatedBookDtoWithInvalidField(BookDto bookToBeUpdated) {
        BookDto bookDto = updatedBookDto(bookToBeUpdated);
        bookDto.setAuthorName(LONG_NAME);
        bookDto.setAuthorSurname(LONG_NAME);
        bookDto.setYearOfPublication(INVALID_YEAR_OF_PUBLICATION);

        return bookDto;
    }

    public static List<BookDto> createListBookDto(int size) {
        List<BookDto> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(createBookDto());
        }

        return list;
    }

    public static UserDto createUserDto() {
        return UserDto.builder()
                .fullName("Test User" + getLimitUUID(10))
                .email("testuser" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    public static UserDto createInvalidUserDto() {
        return UserDto.builder()
                .fullName(LONG_NAME)
                .email(INVALID_EMAIL)
                .dateOfBirth(INVALID_DATA_OF_BIRTH)
                .build();
    }

    public static List<UserDto> createListUserDto(int size) {
        List<UserDto> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(createUserDto());
        }

        return list;
    }


    public static BookDto createBookDtoWithOwner(Long userId) {
        BookDto book = createBookDto();
        book.setUserId(userId);
        return book;
    }

    private static String getLimitUUID(int limit) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "").substring(0, limit + 1);
    }

    private static UUID getLimitUUID() {
        return UUID.randomUUID();
    }
}
