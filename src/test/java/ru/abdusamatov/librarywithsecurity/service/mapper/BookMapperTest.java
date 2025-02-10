package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.model.Book;
import ru.abdusamatov.librarywithsecurity.model.Reader;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class BookMapperTest {

    private final BookMapper mapper = new BookMapperImpl();

    @ParameterizedTest
    @MethodSource("shouldMapBookToBookDto")
    void shouldMapBookToBookDto(final Book book, final BookDto expected) {
        final var actual = mapper.bookToBookDto(book);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldMapBookDtoToBook")
    void shouldMapBookDtoToBook(final BookDto dtoToBeMapped, final Book expected) {
        final var actual = mapper.bookDtoToBook(dtoToBeMapped);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateBookFromDto")
    void shouldUpdateBookFromDto(
            final BookDto newDto,
            final Book existingBook,
            final Book expected
    ) {
        final var actual = mapper.updateBookFromDto(newDto, existingBook);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldUpdateBookFromDto")
    void shouldUpdateBookFromDto_whenDtoIsNull(
            final BookDto newDto,
            final Book existingBook,
            final Book expected
    ) {
        final var actual = mapper.updateBookFromDto(null, existingBook);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(existingBook);
    }

    @Test
    void shouldReturnNull_whenBookIsNullInBookToBookDto() {
        final var bookDto = mapper.bookToBookDto(null);

        assertThat(bookDto)
                .isNull();
    }

    @Test
    void shouldReturnNull_whenDtoIsNullInBookDtoToBook() {
        final var book = mapper.bookDtoToBook(null);

        assertThat(book)
                .isNull();
    }

    public static Stream<Arguments> shouldMapBookToBookDto() {
        Book book = TestDataProvider
                .createBook()
                .owner(TestDataProvider.createReader())
                .build();

        BookDto expected = TestDataProvider
                .createBookDto()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .authorSurname(book.getAuthorSurname())
                .yearOfPublication(book.getYearOfPublication())
                .takenAt(book.getTakenAt())
                .readerId(book.getOwner().getId())
                .build();

        return Stream.of(Arguments.arguments(book, expected));
    }

    public static Stream<Arguments> shouldMapBookDtoToBook() {
        BookDto dtoToBeMapped = TestDataProvider
                .createBookDto()
                .build();

        Book expected = TestDataProvider
                .createBook()
                .id(dtoToBeMapped.getId())
                .title(dtoToBeMapped.getTitle())
                .authorName(dtoToBeMapped.getAuthorName())
                .authorSurname(dtoToBeMapped.getAuthorSurname())
                .yearOfPublication(dtoToBeMapped.getYearOfPublication())
                .takenAt(dtoToBeMapped.getTakenAt())
                .owner(Reader.builder().id(dtoToBeMapped.getReaderId()).build())
                .build();

        return Stream.of(Arguments.arguments(dtoToBeMapped, expected));
    }

    public static Stream<Arguments> shouldUpdateBookFromDto() {
        Book existingBook = TestDataProvider
                .createBook()
                .owner(TestDataProvider.createReader())
                .build();

        BookDto newDto = TestDataProvider
                .createBookDto()
                .title("Updated Title")
                .authorName("Updated Author")
                .authorSurname("Updated Surname")
                .yearOfPublication(existingBook.getYearOfPublication())
                .takenAt(existingBook.getTakenAt())
                .readerId(existingBook.getOwner() != null ? existingBook.getOwner().getId() : null)
                .build();

        Book expected = TestDataProvider
                .createBook()
                .title("Updated Title")
                .authorName("Updated Author")
                .authorSurname("Updated Surname")
                .yearOfPublication(existingBook.getYearOfPublication())
                .takenAt(existingBook.getTakenAt())
                .owner(existingBook.getOwner())
                .build();

        return Stream.of(Arguments.arguments(newDto, existingBook, expected));
    }
}
