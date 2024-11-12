package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.model.Book;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class BookMapperTest {

    private final BookMapper mapper = new BookMapperImpl();

    @Test
    void shouldMapBookToDto() {
        final var book = TestDataProvider.createBook().build();

        final var bookDto = mapper.bookToBookDto(book);

        assertThat(bookDto).isNotNull();
        assertBooksAreEqual(bookDto, book);
    }

    @Test
    void shouldMapDtoToBook() {
        final var bookDto = TestDataProvider.createBookDto().build();

        final var book = mapper.bookDtoToBook(bookDto);

        assertThat(book).isNotNull();
        assertBooksAreEqual(bookDto, book);
    }

    @Test
    void shouldMapDtoToBook_whenDtoIsNull() {
        final var book = mapper.bookDtoToBook(null);

        assertThat(book).isNull();
    }

    @Test
    void shouldUpdateBookFromDto() {
        final var bookToBeUpdated = TestDataProvider.createBook().build();
        final var newBookDto = TestDataProvider
                .createBookDto()
                .id(bookToBeUpdated.getId())
                .userId(bookToBeUpdated.getOwner().getId()).build();

        final var updatedBook = mapper.updateBookFromDto(newBookDto, bookToBeUpdated);

        assertBooksAreEqual(newBookDto, updatedBook);
    }

    @Test
    void shouldUpdateBookFromDto_whenDtoIsNull() {
        final var bookToBeUpdated = TestDataProvider.createBook().build();

        final var updatedBook = mapper.updateBookFromDto(null, bookToBeUpdated);

        assertThat(updatedBook)
                .usingRecursiveComparison()
                .isEqualTo(bookToBeUpdated);
    }

    @Test
    void shouldReturnNull_whenBookIsNullInBookToDto() {
        final var bookDto = mapper.bookToBookDto(null);

        assertThat(bookDto).isNull();
    }

    public static Stream<Arguments> shouldMapBookToDto() {
        Book book = TestDataProvider.createBook();
        BookDto bookDto = mapper.bookToBookDto(book);

        return Stream.of(Arguments.arguments(book, bookDto));
    }

    public static Stream<Arguments> shouldMapDtoToBook() {
        BookDto bookDto = TestDataProvider.createBookDto();
        Book book = mapper.bookDtoToBook(bookDto);

        return Stream.of(Arguments.arguments(bookDto, book));
    }

    public static Stream<Arguments> shouldUpdateBookFromDto() {
        Book bookToBeUpdated = TestDataProvider.createBook();

        BookDto newBookDto = TestDataProvider.createBookDto();
        newBookDto.setId(bookToBeUpdated.getId());
        newBookDto.setUserId(bookToBeUpdated.getOwner().getId());

        return Stream.of(Arguments.arguments(bookToBeUpdated, newBookDto));
    }

    private static void assertBooksAreEqual(final BookDto bookDto, final Book book) {
        assertThat(book)
                .withFailMessage("Books are not equal")
                .usingRecursiveComparison()
                .ignoringFields("owner")
                .isEqualTo(bookDto);
        assertThat(book.getOwner().getId()).isEqualTo(bookDto.getUserId());
    }

}
