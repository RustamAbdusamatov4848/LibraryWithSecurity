package ru.abdusamatov.librarywithsecurity.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.services.mappers.BookMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class BookMapperTest {

    private BookMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BookMapper.class);
    }

    @Test
    void shouldMapBookToBookDto() {
        Book book = TestDataProvider.createBook();

        BookDto bookDto = mapper.bookToBookDto(book);

        assertThat(bookDto).isNotNull();
        assertEquals(bookDto, book);
    }

    @Test
    void shouldMapBookDtoToBook() {
        BookDto bookDto = TestDataProvider.createBookDto();
        bookDto.setId(1L);

        Book book = mapper.bookDtoToBook(bookDto);

        assertThat(book).isNotNull();
        assertEquals(bookDto, book);
    }

    @Test
    void shouldUpdateBookFromDto() {
        Book bookToBeUpdated = TestDataProvider.createBook();
        BookDto newBookDto = TestDataProvider.createBookDto();
        newBookDto.setId(bookToBeUpdated.getId());
        newBookDto.setUserId(bookToBeUpdated.getOwner().getId());

        Book updatedBook = mapper.updateBookFromDto(newBookDto, bookToBeUpdated);

        assertThat(updatedBook).isNotNull();
        assertEquals(newBookDto, updatedBook);
    }

    @Test
    void shouldReturnNull_whenBookIsNullInBookToBookDto() {
        BookDto bookDto = mapper.bookToBookDto(null);
        assertThat(bookDto).isNull();
    }

    @Test
    void shouldReturnNull_whenBookDtoIsNullInBookDtoToBook() {
        Book book = mapper.bookDtoToBook(null);
        assertThat(book).isNull();
    }

    private static void assertEquals(BookDto bookDto, Book book) {
        assertThat(bookDto).extracting(
                BookDto::getId,
                BookDto::getTitle,
                BookDto::getAuthorName,
                BookDto::getAuthorSurname,
                BookDto::getYearOfPublication,
                BookDto::getTakenAt,
                BookDto::getUserId
        ).containsExactly(
                book.getId(),
                book.getTitle(),
                book.getAuthorName(),
                book.getAuthorSurname(),
                book.getYearOfPublication(),
                book.getTakenAt(),
                book.getOwner().getId()
        );
    }
}
