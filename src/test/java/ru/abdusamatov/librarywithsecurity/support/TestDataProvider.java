package ru.abdusamatov.librarywithsecurity.support;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.dto.ReaderDto;
import ru.abdusamatov.librarywithsecurity.model.Book;
import ru.abdusamatov.librarywithsecurity.model.Document;
import ru.abdusamatov.librarywithsecurity.model.Reader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;


public class TestDataProvider {
    private static final String FILE_NAME = "passport.jpg";
    private static final int MAX_NAME_LENGTH = 30;
    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MIN_YEAR_OF_PUBLICATION = 1500;
    private static final String LONG_NAME = "a".repeat(MAX_NAME_LENGTH + 1);
    private static final String LONG_TITLE_NAME = "a".repeat(MAX_TITLE_LENGTH + 1);
    private static final int INVALID_YEAR_OF_PUBLICATION = 1499;
    private static final String INVALID_EMAIL = "invalid-email";
    private static final LocalDate INVALID_DATA_OF_BIRTH = LocalDate.now().plusDays(1);
    private static final Random RANDOM = new Random();

    public static Book.BookBuilder createBook() {
        return Book.builder()
                .title("Book Title" + getLimitUUID())
                .authorName("AuthorName")
                .authorSurname("AuthorSurname")
                .yearOfPublication(RANDOM.nextInt(1500, LocalDate.now().getYear()))
                .takenAt(LocalDateTime.now())
                .owner(null);
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
                .readerId(bookToBeUpdated.getReaderId())
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
                .readerId(bookToBeUpdated.getReaderId());
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

    public static Reader createReader() {
        final var reader = TestDataProvider.createReaderWithoutDocument().build();
        final var document = TestDataProvider.createDocument().owner(reader).build();

        reader.setDocument(document);

        return reader;
    }

    public static Reader.ReaderBuilder createReaderWithoutDocument() {
        return Reader.builder()
                .fullName("Test Reader" + getLimitUUID(10))
                .email("testReader" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .books(Collections.emptyList());
    }

    public static ReaderDto.ReaderDtoBuilder createReaderDto() {
        return ReaderDto.builder()
                .fullName("Test Reader" + getLimitUUID(10))
                .email("testReader" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .documentDto(createDocumentDto().build());
    }

    public static ReaderDto.ReaderDtoBuilder createReaderDtoWithInvalidFields() {
        return ReaderDto.builder()
                .fullName(LONG_NAME)
                .email(INVALID_EMAIL)
                .dateOfBirth(INVALID_DATA_OF_BIRTH)
                .documentDto(createDocumentDto().build());
    }

    public static ReaderDto.ReaderDtoBuilder updateReaderDto(final ReaderDto readerToBeUpdated) {
        return ReaderDto.builder()
                .id(readerToBeUpdated.getId())
                .fullName("Fullname updated")
                .email("testReader" + getLimitUUID(10) + "@example.com")
                .dateOfBirth(getRandomDate(LocalDate.now()))
                .books(readerToBeUpdated.getBooks())
                .documentDto(readerToBeUpdated.getDocumentDto());
    }

    public static ReaderDto.ReaderDtoBuilder updateReaderDtoWithInvalidFields(final ReaderDto readerToBeUpdated) {
        return ReaderDto.builder()
                .id(readerToBeUpdated.getId())
                .fullName(LONG_NAME)
                .email(INVALID_EMAIL)
                .dateOfBirth(INVALID_DATA_OF_BIRTH)
                .documentDto(readerToBeUpdated.getDocumentDto());
    }

    public static List<Reader> createListReader(final int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> createReader())
                .toList();
    }

    public static Document.DocumentBuilder createDocument() {
        return Document.builder()
                .bucketName("bucket-example" + "-" + getLimitUUID(8))
                .fileName(FILE_NAME + "-" + getLimitUUID(8));
    }

    public static DocumentDto.DocumentDtoBuilder createDocumentDto() {
        return DocumentDto.builder()
                .bucketName("bucket-example" + "-" + getLimitUUID(8))
                .fileName(FILE_NAME + "-" + getLimitUUID(8));
    }

    @SneakyThrows
    public static byte[] getImageBytes(final String filePath) {
        final var resource = new ClassPathResource(filePath);

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
}
