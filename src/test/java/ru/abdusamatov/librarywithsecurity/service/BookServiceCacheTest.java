package ru.abdusamatov.librarywithsecurity.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.cache.Cache;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.model.Book;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class BookServiceCacheTest extends TestBase {

    private static final String BOOK_CACHE = "book";

    @Override
    protected void clearDatabase() {
        spyBookRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(spyBookRepository);
    }

    @ParameterizedTest
    @MethodSource("createBook")
    void shouldCallRepositoryOnce_whenGetBookById(final BookDto dtoToBeSaved) {
        final var savedBook = bookService
                .createBook(dtoToBeSaved)
                .block();

        assertNotNull(savedBook);
        assertBookNotInCache(savedBook.getId());

        final var retrievedBook = bookService
                .getBookById(savedBook.getId())
                .block();

        assertNotNull(retrievedBook);
        assertBookInCache(savedBook.getId(), retrievedBook);

        final var cachedBook = bookService.getBookById(savedBook.getId()).block();

        assertNotNull(cachedBook);
        verify(spyBookRepository)
                .save(any(Book.class));
        verify(spyBookRepository)
                .findById(savedBook.getId());
    }


    @ParameterizedTest
    @MethodSource("createBook")
    void shouldUpdateCacheBook_whenUpdateBook(final BookDto dtoToBeSaved) {
        final var savedBook = addSavedEntityToCache(dtoToBeSaved);

        final var updatedBook = bookService
                .updateBook(TestDataProvider
                        .updateBookDto(savedBook)
                        .build())
                .block();

        assertNotNull(updatedBook);
        assertBookInCache(savedBook.getId(), updatedBook);
        verify(spyBookRepository, times(2))
                .save(any(Book.class));
        verify(spyBookRepository)
                .findById(updatedBook.getId());
    }


    @ParameterizedTest
    @MethodSource("createBook")
    void shouldDeleteBookFromCache_whenDeleteBook(final BookDto dtoToBeSaved) {
        final var savedBook = addSavedEntityToCache(dtoToBeSaved);

        bookService.deleteBook(savedBook.getId()).block();

        assertBookNotInCache(savedBook.getId());
        verify(spyBookRepository)
                .save(any(Book.class));
        verify(spyBookRepository)
                .findById(savedBook.getId());
        verify(spyBookRepository)
                .delete(any(Book.class));
    }


    private Cache assertCacheNotNull() {
        final var cache = cacheManager.getCache(BOOK_CACHE);

        assertNotNull(cache);

        return cache;
    }

    private void assertBookInCache(final Long id, final BookDto expectedBook) {
        final var cache = assertCacheNotNull();

        assertThat(cache.get(id, BookDto.class))
                .isNotNull()
                .extracting(BookDto::getId)
                .isEqualTo(expectedBook.getId());
    }

    private void assertBookNotInCache(final Long id) {
        final var cache = assertCacheNotNull();

        assertThat(cache.get(id))
                .isNull();
    }

    private static Stream<Arguments> createBook() {
        final var book = TestDataProvider
                .createBookDto()
                .build();

        return Stream.of(Arguments.arguments(book));
    }

    private BookDto addSavedEntityToCache(final BookDto dtoToSaved) {
        final var savedBook = bookService
                .createBook(dtoToSaved)
                .block();

        cacheManager.getCache(BOOK_CACHE).put(savedBook.getId(), savedBook);

        return savedBook;
    }
}
