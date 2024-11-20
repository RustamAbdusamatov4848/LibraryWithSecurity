package ru.abdusamatov.librarywithsecurity.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.repository.BookRepository;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

public class BookServiceTest extends TestBase {

    private static final String BOOK_CACHE = "book";

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
        assertCacheNotNull();
        bookService.getBookById(id);

        verify(bookRepository, atMostOnce())
                .findById(id);
        assertBookInCache(id, book);
    }

    @Test
    void shouldUpdateCacheBook_whenUpdateBook() {
        final var savedBook = createAndCacheBook();
        cacheManager.getCache(BOOK_CACHE).put(savedBook.getId(), savedBook);

        final var updatedBook = TestDataProvider
                .updateBookDto(savedBook)
                .build();

        bookService.updateBook(updatedBook);

        assertBookInCache(updatedBook.getId(), updatedBook);
    }

    @Test
    void shouldDeleteBookFromCache_whenDeleteBook() {
        final var savedBook = createAndCacheBook();
        final var id = savedBook.getId();
        cacheManager.getCache(BOOK_CACHE).put(id, savedBook);

        bookService.deleteBook(id);

        assertBookNotInCache(id);
    }

    private void assertCacheNotNull() {
        var cache = cacheManager.getCache(BOOK_CACHE);

        assertThat(cache)
                .isNotNull();
    }

    private BookDto createAndCacheBook() {
        final var book = TestDataProvider
                .createBookDto()
                .build();
        return bookService.createBook(book);
    }

    private void assertBookInCache(final Long id, final BookDto expectedBook) {
        final var cache = cacheManager.getCache(BOOK_CACHE);

        assertThat(cache)
                .isNotNull();
        assertThat(cache.get(id, BookDto.class))
                .isNotNull()
                .extracting(BookDto::getId)
                .isEqualTo(expectedBook.getId());
    }

    private void assertBookNotInCache(final Long id) {
        final var cache = cacheManager.getCache(BOOK_CACHE);

        assertThat(cache)
                .isNotNull();
        assertThat(cache.get(id))
                .isNull();
    }
}
