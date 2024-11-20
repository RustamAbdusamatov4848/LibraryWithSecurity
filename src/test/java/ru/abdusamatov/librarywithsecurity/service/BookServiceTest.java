package ru.abdusamatov.librarywithsecurity.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.repository.BookRepository;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BookServiceTest extends TestBase {

    @Autowired
    private BookService bookService;

    @Autowired
    private CacheManager cacheManager;

    @SpyBean
    private BookRepository bookRepository;

    @Override
    protected void clearDatabase() {
        bookRepository.deleteAll();
    }

    @Test
    void shouldCallRepositoryOnce_whenGetBookById() {
        final var book = createAndCacheBook();
        final var id = book.getId();

        bookService.getBookById(id);
        verify(bookRepository, times(1))
                .findById(id);


        bookService.getBookById(id);
        verify(bookRepository, atMostOnce())
                .findById(id);
        assertBookInCache(id, book);
    }

    @Test
    void shouldUpdateCacheBook_whenUpdateBook() {
        final var book = createAndCacheBook();
        final var updatedBook = TestDataProvider
                .updateBookDto(book)
                .build();

        bookService.updateBook(updatedBook);

        assertBookInCache(updatedBook.getId(), updatedBook);
        verify(bookRepository, times(1))
                .findById(book.getId());
        verify(bookRepository, times(2))
                .save(any());
    }

    @Test
    void shouldDeleteBookFromCache_whenDeleteBook() {
        final var book = createAndCacheBook();
        final var id = book.getId();

        bookService.deleteBook(id);

        assertBookNotInCache(id);
        verify(bookRepository, times(1))
                .findById(id);
        verify(bookRepository, times(1))
                .delete(any());
    }

    @Test
    void shouldCacheSearchByTitle() {
        final var titleQuery = "SomeTitle";
        final var book = TestDataProvider.createBookDto().title(titleQuery).build();
        bookService.createBook(book);

        bookService.searchByTitle(titleQuery);
        verify(bookRepository, times(1)).findByTitleStartingWith(titleQuery);

        bookService.searchByTitle(titleQuery);
        verify(bookRepository, times(1)).findByTitleStartingWith(titleQuery);

        final var cache = cacheManager.getCache("bookTitle");
        assertThat(cache).isNotNull();
        assertThat(cache.get(titleQuery, List.class)).isNotNull();
    }

    private BookDto createAndCacheBook() {
        final var book = TestDataProvider
                .createBookDto()
                .build();
        return bookService.createBook(book);
    }

    private void assertBookInCache(Long id, BookDto expectedBook) {
        final var cache = cacheManager.getCache("book");

        assertThat(cache)
                .isNotNull();
        assertThat(cache.get(id, BookDto.class))
                .isNotNull()
                .extracting(BookDto::getId)
                .isEqualTo(expectedBook.getId());
    }

    private void assertBookNotInCache(Long id) {
        final var cache = cacheManager.getCache("book");

        assertThat(cache)
                .isNotNull();
        assertThat(cache.get(id))
                .isNull();
    }
}
