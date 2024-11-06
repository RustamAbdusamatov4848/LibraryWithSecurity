package ru.abdusamatov.librarywithsecurity.support;

import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class TestDataProvider {
    public static final int MAX_NAME_LENGTH = 30;
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MIN_YEAR_OF_PUBLICATION = 1500;
    public static final String LONG_NAME = "a".repeat(MAX_NAME_LENGTH + 1);
    public static final String LONG_TITLE_NAME = "a".repeat(MAX_TITLE_LENGTH + 1);
    public static final int INVALID_YEAR_OF_PUBLICATION = 1499;
    public static final String INVALID_EMAIL = "invalid-email";
    public static final LocalDate INVALID_DATA_OF_BIRTH = LocalDate.now().plusDays(1);
    public static final Random RANDOM = new Random();

    public static BookDto createBookDto() {
        return BookDto.builder()
                .title("Book Title" + getLimitUUID())
                .authorName("AuthorName")
                .authorSurname("AuthorSurname")
                .yearOfPublication(RANDOM.nextInt(1500, LocalDate.now().getYear()))
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

    public static BookDto updateBookDto(BookDto bookToBeUpdated) {
        return BookDto.builder()
                .id(bookToBeUpdated.getId())
                .title("Title updated ")
                .authorName("Author name updated")
                .authorSurname("Author surname updated")
                .yearOfPublication(RANDOM.nextInt(1500, LocalDate.now().getYear()))
                .userId(bookToBeUpdated.getUserId())
                .takenAt(bookToBeUpdated.getTakenAt())
                .build();
    }

    public static BookDto updateBookDtoWithInvalidField(BookDto bookToBeUpdated) {
        return BookDto.builder()
                .id(bookToBeUpdated.getId())
                .title(LONG_TITLE_NAME)
                .authorName(LONG_NAME)
                .authorSurname(LONG_NAME)
                .yearOfPublication(getRandomInvalidYearOfPublication())
                .takenAt(bookToBeUpdated.getTakenAt())
                .userId(bookToBeUpdated.getUserId())
                .build();
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

    public static UserDto updateUserDto(UserDto userToBeUpdated) {
        return UserDto.builder()
                .id(userToBeUpdated.getId())
                .fullName("Fullname updated")
                .email("testuser" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(getRandomDate(LocalDate.now()))
                .books(userToBeUpdated.getBooks())
                .build();
    }

    public static UserDto updateUserDtoWithInvalidField(UserDto userToBeUpdated) {
        return UserDto.builder()
                .id(userToBeUpdated.getId())
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

    private static LocalDate getRandomDate(LocalDate now) {
        return now.minusYears(RANDOM.nextLong(100));
    }

    private static int getRandomInvalidYearOfPublication() {
        return RANDOM.nextInt(MIN_YEAR_OF_PUBLICATION);
    }
}
