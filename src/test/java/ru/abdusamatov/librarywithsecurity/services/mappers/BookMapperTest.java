package ru.abdusamatov.librarywithsecurity.services.mappers;

import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class BookMapperTest {

    private final BookMapper mapper = new BookMapperImpl();

    @Test
    void shouldMapBookToDto() {
        Book book = TestDataProvider.createBook();

        BookDto bookDto = mapper.bookToBookDto(book);

        assertThat(bookDto).isNotNull();
        assertBookAndBookDtoEqual(bookDto, book);
    }

    @Test
    void shouldMapDtoToBook() {
        BookDto bookDto = TestDataProvider.createBookDto();

        Book book = mapper.bookDtoToBook(bookDto);

        assertThat(book).isNotNull();
        assertBookAndBookDtoEqual(bookDto, book);
    }

    @Test
    void shouldMapDtoToBook_whenDtoIsNull() {
        Book book = mapper.bookDtoToBook(null);

        assertThat(book).isNull();
    }

    @Test
    void shouldUpdateBookFromDto() {
        Book bookToBeUpdated = TestDataProvider.createBook();
        BookDto newBookDto = TestDataProvider.createBookDto();
        newBookDto.setId(bookToBeUpdated.getId());
        newBookDto.setUserId(bookToBeUpdated.getOwner().getId());

        Book updatedBook = mapper.updateBookFromDto(newBookDto, bookToBeUpdated);

        assertBookAndBookDtoEqual(newBookDto, updatedBook);
    }

    @Test
    void shouldUpdateBookFromDto_whenDtoIsNull() {
        Book bookToBeUpdated = TestDataProvider.createBook();

        Book updatedBook = mapper.updateBookFromDto(null, bookToBeUpdated);

        assertThat(updatedBook)
                .usingRecursiveComparison()
                .isEqualTo(bookToBeUpdated);
    }

    @Test
    void shouldReturnNull_whenBookIsNullInBookToDto() {
        BookDto bookDto = mapper.bookToBookDto(null);

        assertThat(bookDto).isNull();
    }

    private static void assertBookAndBookDtoEqual(BookDto bookDto, Book book) {
        assertThat(book)
                .withFailMessage("Books are not equal")
                .usingRecursiveComparison()
                .ignoringFields("owner")
                .isEqualTo(bookDto);
        assertThat(book.getOwner().getId()).isEqualTo(bookDto.getUserId());
    }
}
