package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.ReaderDto;
import ru.abdusamatov.librarywithsecurity.model.Book;
import ru.abdusamatov.librarywithsecurity.model.Reader;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ReaderMapperTest extends TestBase {

    @Autowired
    private ReaderMapper mapper;

    @ParameterizedTest
    @MethodSource("shouldMapReaderToDto")
    void shouldMapReaderToDto(final Reader toBeMapped, final ReaderDto expected) {
        final var actual = mapper.readerToDto(toBeMapped);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldMapDtoToReader")
    void shouldMapDtoToReader(final ReaderDto dtoToBeMapped, final Reader expected) {
        final var actual = mapper.dtoToReader(dtoToBeMapped);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateReaderFromDto")
    void shouldUpdateReaderFromDto(
            final ReaderDto newReaderDto,
            final Reader readerToBeUpdated,
            final Reader expected
    ) {
        final var actual = mapper.updateReaderFromDto(newReaderDto, readerToBeUpdated);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateReaderFromDto")
    void shouldNotUpdateReader_whenReaderDtoIsNull(
            final ReaderDto newReaderDto,
            final Reader readerToBeUpdated,
            final Reader expected
    ) {
        final var actual = mapper.updateReaderFromDto(null, readerToBeUpdated);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(readerToBeUpdated);
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateReaderFromDto")
    void shouldUpdateReaderFromDto_whenReaderHasNullBookList(
            ReaderDto newReaderDto,
            Reader readerToBeUpdated,
            final Reader expected
    ) {
        final var listSize = 10;
        newReaderDto.setBooks(TestDataProvider.createListBookDto(listSize));
        readerToBeUpdated.setBooks(null);

        final var actual = mapper.updateReaderFromDto(newReaderDto, readerToBeUpdated);

        assertThat(actual.getBooks())
                .isNotNull()
                .hasSize(listSize);
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateReaderFromDto")
    void shouldUpdateReaderFromDto_whenDtoHasNullBookList(
            ReaderDto newReaderDto,
            Reader readerToBeUpdated,
            final Reader expected
    ) {
        final var listSize = 10;
        readerToBeUpdated.setBooks(TestDataProvider.createListBook(listSize));
        newReaderDto.setBooks(null);

        final var actual = mapper.updateReaderFromDto(newReaderDto, readerToBeUpdated);

        assertThat(actual.getBooks())
                .isNull();
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateReaderFromDto")
    void shouldReturnNull_whenReaderDtoIsNullInUpdateReaderFromDto(
            ReaderDto newReaderDto,
            Reader readerToBeUpdated,
            final Reader expected
    ) {
        final var updatedReader = mapper.updateReaderFromDto(null, readerToBeUpdated);

        assertThat(updatedReader)
                .isEqualTo(readerToBeUpdated);
    }

    @Test
    void shouldReturnNull_whenReaderIsNullInReaderToReaderDto() {
        final var readerDto = mapper.readerToDto(null);
        assertThat(readerDto).isNull();
    }

    @Test
    void shouldReturnNull_whenReaderDtoIsNullInReaderDtoToReader() {
        final var reader = mapper.dtoToReader(null);
        assertThat(reader).isNull();
    }

    public static Stream<Arguments> shouldMapReaderToDto() {
        final var reader = TestDataProvider.createReader();
        final var document = reader.getDocument();

        final var expected = TestDataProvider
                .createReaderDto()
                .id(reader.getId())
                .fullName(reader.getFullName())
                .email(reader.getEmail())
                .dateOfBirth(reader.getDateOfBirth())
                .books(Collections.emptyList())
                .documentDto(TestDataProvider.createDocumentDto()
                        .id(document.getId())
                        .bucketName(document.getBucketName())
                        .fileName(document.getFileName())
                        .id(document.getOwner().getId())
                        .build())
                .build();

        return Stream.of(Arguments.arguments(reader, expected));
    }

    public static Stream<Arguments> shouldMapDtoToReader() {
        final var dtoToBeMapped = TestDataProvider
                .createReaderDto()
                .books(Collections.emptyList())
                .build();
        final var document = dtoToBeMapped.getDocumentDto();

        final var expected = Reader.builder()
                .id(dtoToBeMapped.getId())
                .fullName(dtoToBeMapped.getFullName())
                .email(dtoToBeMapped.getEmail())
                .dateOfBirth(dtoToBeMapped.getDateOfBirth())
                .books(Collections.emptyList())
                .document(TestDataProvider.createDocument()
                        .id(document.getId())
                        .bucketName(document.getBucketName())
                        .fileName(document.getFileName())
                        .owner(Reader.builder().id(document.getReaderId()).build())
                        .build())
                .build();

        return Stream.of(Arguments.arguments(dtoToBeMapped, expected));
    }

    public static Stream<Arguments> shouldUpdateReaderFromDto() {
        final var existingReader = TestDataProvider.createReader();
        final var document = existingReader.getDocument();

        final var newDto = TestDataProvider
                .createReaderDto()
                .id(existingReader.getId())
                .fullName("Updated FullName")
                .email("updated@example.com")
                .dateOfBirth(existingReader.getDateOfBirth())
                .books(Collections.emptyList())
                .documentDto(TestDataProvider.createDocumentDto()
                        .id(document.getId())
                        .bucketName(document.getBucketName())
                        .fileName(document.getFileName())
                        .readerId(document.getOwner().getId())
                        .build())
                .build();

        final var expected = Reader.builder()
                .id(newDto.getId())
                .fullName(newDto.getFullName())
                .email(newDto.getEmail())
                .dateOfBirth(existingReader.getDateOfBirth())
                .books(newDto
                        .getBooks()
                        .stream()
                        .map(ReaderMapperTest::getBook)
                        .toList())
                .document(TestDataProvider
                        .createDocument()
                        .id(document.getId())
                        .fileName(document.getFileName())
                        .bucketName(document.getBucketName())
                        .owner(existingReader)
                        .build())
                .build();

        return Stream.of(Arguments.arguments(newDto, existingReader, expected));
    }

    private static Book getBook(final BookDto bookDto) {
        return TestDataProvider.createBook()
                .id(bookDto.getId())
                .title(bookDto.getTitle())
                .authorName(bookDto.getAuthorName())
                .authorSurname(bookDto.getAuthorSurname())
                .takenAt(bookDto.getTakenAt())
                .yearOfPublication(bookDto.getYearOfPublication())
                .owner(Reader.builder()
                        .id(bookDto.getReaderId())
                        .build())
                .build();
    }

    @Override
    protected void clearDatabase() {
    }
}
