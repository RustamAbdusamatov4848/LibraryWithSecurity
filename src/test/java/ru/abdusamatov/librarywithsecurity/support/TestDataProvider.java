package ru.abdusamatov.librarywithsecurity.support;

import ru.abdusamatov.librarywithsecurity.dto.AuthenticationDto;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class TestDataProvider {
    public static final int MAX_NAME_LENGTH = 30;
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MIN_YEAR_OF_PUBLICATION = 1500;
    public static final String LONG_NAME = "a".repeat(MAX_NAME_LENGTH + 1);
    public static final String LONG_TITLE_NAME = "a".repeat(MAX_TITLE_LENGTH + 1);
    public static final String LONG_PASSWORD = "a".repeat(MAX_PASSWORD_LENGTH + 1);
    public static final int INVALID_YEAR_OF_PUBLICATION = 1499;
    public static final String INVALID_EMAIL = "invalid-email";
    public static final LocalDate INVALID_DATA_OF_BIRTH = LocalDate.now().plusDays(1);
    public static final Random RANDOM = new Random();

    public static Book createBook() {
        User owner = createUser();
        return Book.builder()
                .id(1L)
                .title("Book Title" + getLimitUUID())
                .authorName("AuthorName")
                .authorSurname("AuthorSurname")
                .yearOfPublication(RANDOM.nextInt(1500, LocalDate.now().getYear()))
                .takenAt(LocalDateTime.now())
                .owner(owner)
                .build();
    }

    public static BookDto createBookDto() {
        return BookDto.builder()
                .title("Book Title" + getLimitUUID())
                .authorName("AuthorName")
                .authorSurname("AuthorSurname")
                .yearOfPublication(RANDOM.nextInt(1500, LocalDate.now().getYear()))
                .build();
    }

    public static BookDto createBookDtoWithInvalidFields() {
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

    public static BookDto updateBookDtoWithInvalidFields(BookDto bookToBeUpdated) {
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

    public static List<Book> createListBook(int size) {
        List<Book> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(createBook());
        }

        return list;
    }

    public static List<BookDto> createListBookDto(int size) {
        List<BookDto> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(createBookDto());
        }

        return list;
    }

    public static User createUser() {
        return User.builder()
                .id(1L)
                .fullName("Test User" + getLimitUUID(10))
                .email("testuser" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .books(Collections.emptyList())
                .build();
    }

    public static UserDto createUserDto() {
        return UserDto.builder()
                .fullName("Test User" + getLimitUUID(10))
                .email("testuser" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    public static UserDto createUserDtoWithInvalidFields() {
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

    public static UserDto updateUserDtoWithInvalidFields(UserDto userToBeUpdated) {
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

    public static Librarian createLibrarian() {
        return Librarian.builder()
                .id(1L)
                .fullName("Test librarian" + getLimitUUID(10))
                .email("testlibrarian" + getLimitUUID(10) + "@example.com")
                .password(getRandomPassword())
                .build();
    }

    public static LibrarianDto createLibrarianDto() {
        return LibrarianDto.builder()
                .fullName("Test User" + getLimitUUID(10))
                .email("testuser" + getLimitUUID(10) + "@example.com")
                .password(getRandomPassword())
                .build();
    }

    public static LibrarianDto createLibrarianDtoWithInvalidFields() {
        return LibrarianDto.builder()
                .fullName(LONG_NAME)
                .email(INVALID_EMAIL)
                .password(LONG_PASSWORD)
                .build();
    }

    public static AuthenticationDto createAuthenticationDto() {
        return AuthenticationDto.builder()
                .email("testuser" + getLimitUUID(10) + "@example.com")
                .password(getRandomPassword())
                .build();
    }

    public static AuthenticationDto createAuthenticationDto(String email, String password) {
        return AuthenticationDto.builder()
                .email(email)
                .password(password)
                .build();
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

    private static String getRandomPassword() {
        String source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder result = new StringBuilder();
        Random random = new Random();
        int passwordLength = random.nextInt(5, MAX_PASSWORD_LENGTH);

        for (int i = 0; i < passwordLength; i++) {
            result.append(source.charAt(random.nextInt(0, source.length())));
        }

        return result.toString();
    }
}
