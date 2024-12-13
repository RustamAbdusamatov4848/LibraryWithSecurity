package ru.abdusamatov.librarywithsecurity.support;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.model.Book;
import ru.abdusamatov.librarywithsecurity.model.Document;
import ru.abdusamatov.librarywithsecurity.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;


public class TestDataProvider {
    public static final String FILE_NAME = "passport.jpg";
    public static final int MAX_NAME_LENGTH = 30;
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MIN_YEAR_OF_PUBLICATION = 1500;
    public static final String LONG_NAME = "a".repeat(MAX_NAME_LENGTH + 1);
    public static final String LONG_TITLE_NAME = "a".repeat(MAX_TITLE_LENGTH + 1);
    public static final int INVALID_YEAR_OF_PUBLICATION = 1499;
    public static final String INVALID_EMAIL = "invalid-email";
    public static final LocalDate INVALID_DATA_OF_BIRTH = LocalDate.now().plusDays(1);
    public static final Random RANDOM = new Random();

    public static Book.BookBuilder createBook() {
        final var owner = createUser().build();
        return Book.builder()
                .id(1L)
                .title("Book Title" + getLimitUUID())
                .authorName("AuthorName")
                .authorSurname("AuthorSurname")
                .yearOfPublication(RANDOM.nextInt(1500, LocalDate.now().getYear()))
                .takenAt(LocalDateTime.now())
                .owner(owner);
    }

    public static BookDto.BookDtoBuilder createBookDto() {
        return BookDto.builder()
                .title("Book Title" + getLimitUUID())
                .authorName("AuthorName")
                .authorSurname("AuthorSurname")
                .yearOfPublication(RANDOM.nextInt(1500, LocalDate.now().getYear()));
    }

    public static BookDto.BookDtoBuilder createBookDtoWithInvalidFields() {
        return BookDto.builder()
                .title("Book Title" + getLimitUUID())
                .authorName(LONG_NAME)
                .authorSurname(LONG_NAME)
                .yearOfPublication(INVALID_YEAR_OF_PUBLICATION);
    }

    public static BookDto.BookDtoBuilder updateBookDto(final BookDto bookToBeUpdated) {
        return BookDto.builder()
                .id(bookToBeUpdated.getId())
                .title("Title updated ")
                .authorName("Author name updated")
                .authorSurname("Author surname updated")
                .yearOfPublication(RANDOM.nextInt(1500, LocalDate.now().getYear()))
                .userId(bookToBeUpdated.getUserId())
                .takenAt(bookToBeUpdated.getTakenAt());
    }

    public static BookDto.BookDtoBuilder updateBookDtoWithInvalidFields(final BookDto bookToBeUpdated) {
        return BookDto.builder()
                .id(bookToBeUpdated.getId())
                .title(LONG_TITLE_NAME)
                .authorName(LONG_NAME)
                .authorSurname(LONG_NAME)
                .yearOfPublication(getRandomInvalidYearOfPublication())
                .takenAt(bookToBeUpdated.getTakenAt())
                .userId(bookToBeUpdated.getUserId());
    }

    public static List<Book> createListBook(final int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> createBook().build())
                .toList();
    }

    public static List<BookDto> createListBookDto(final int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> createBookDto().build())
                .toList();
    }

    public static User.UserBuilder createUser() {
        var document = createDocument().build();
        var user = createUserWithoutDocument();

        document.setOwner(user.build());
        user.document(document);

        return user;
    }

    public static UserDto.UserDtoBuilder createUserDto() {
        return UserDto.builder()
                .fullName("Test User" + getLimitUUID(10))
                .email("testuser" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .documentDto(createDocumentDto().build());
    }

    public static UserDto.UserDtoBuilder createUserDtoWithInvalidFields() {
        return UserDto.builder()
                .fullName(LONG_NAME)
                .email(INVALID_EMAIL)
                .dateOfBirth(INVALID_DATA_OF_BIRTH)
                .documentDto(createDocumentDto().build());
    }

    public static UserDto.UserDtoBuilder updateUserDto(final UserDto userToBeUpdated) {
        return UserDto.builder()
                .id(userToBeUpdated.getId())
                .fullName("Fullname updated")
                .email("testuser" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(getRandomDate(LocalDate.now()))
                .books(userToBeUpdated.getBooks())
                .documentDto(userToBeUpdated.getDocumentDto());
    }

    public static UserDto.UserDtoBuilder updateUserDtoWithInvalidFields(final UserDto userToBeUpdated) {
        return UserDto.builder()
                .id(userToBeUpdated.getId())
                .fullName(LONG_NAME)
                .email(INVALID_EMAIL)
                .dateOfBirth(INVALID_DATA_OF_BIRTH)
                .documentDto(userToBeUpdated.getDocumentDto());
    }

    public static List<UserDto> createListUserDto(final int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> createUserDto().build())
                .toList();
    }

    public static Document.DocumentBuilder createDocument() {
        return Document.builder()
                .id(1L)
                .bucketName("bucket-example")
                .fileName(FILE_NAME);
    }

    public static DocumentDto.DocumentDtoBuilder createDocumentDto() {
        return DocumentDto.builder()
                .bucketName("bucket-example")
                .fileName(FILE_NAME)
                .userId(1L);
    }

    @SneakyThrows
    public static byte[] getImageBytes(final String filePath) {
        var resource = new ClassPathResource(filePath);
        return FileCopyUtils.copyToByteArray(resource.getInputStream());
    }

    public static MultipartFile getMultipartFile() {
        return new MockMultipartFile(
                FILE_NAME,
                FILE_NAME,
                "application/octet-stream",
                TestDataProvider.getImageBytes(FILE_NAME)
        );
    }

    private static String getLimitUUID(final int limit) {
        final var uuid = UUID.randomUUID();
        return uuid.toString()
                .replace("-", "")
                .substring(0, limit + 1);
    }

    private static UUID getLimitUUID() {
        return UUID.randomUUID();
    }

    private static LocalDate getRandomDate(final LocalDate now) {
        return now.minusYears(RANDOM.nextLong(100));
    }

    private static int getRandomInvalidYearOfPublication() {
        return RANDOM.nextInt(MIN_YEAR_OF_PUBLICATION);
    }

    private static User.UserBuilder createUserWithoutDocument() {

        return User.builder()
                .id(1L)
                .fullName("Test User" + getLimitUUID(10))
                .email("testuser" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .books(Collections.emptyList());
    }
}
