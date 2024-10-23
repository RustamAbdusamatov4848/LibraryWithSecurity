package ru.abdusamatov.librarywithsecurity.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookListSortedByYear() {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(bookRepository.findAll(Sort.by("year"))).thenReturn(books);

        List<Book> result = bookService.bookList(true);
        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findAll(Sort.by("year"));
    }

    @Test
    void testBookListNotSorted() {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.bookList(false);
        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testShowWithPaginationSortedByYear() {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(bookRepository.findAll(PageRequest.of(0, 2, Sort.by("year"))))
                .thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.showWithPagination(0, 2, true);
        assertEquals(2, result.size());
        verify(bookRepository, times(1))
                .findAll(PageRequest.of(0, 2, Sort.by("year")));
    }

    @Test
    void testShowWithPaginationNotSorted() {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(bookRepository.findAll(PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.showWithPagination(0, 2, false);
        assertEquals(2, result.size());
        verify(bookRepository, times(1))
                .findAll(PageRequest.of(0, 2));
    }

    @Test
    void testShowBook() {
        Book book = new Book();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        Book result = bookService.showBook(1L);
        assertNotNull(result);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookOwner() {
        User user = new User();
        Book book = new Book();
        book.setOwner(user);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        User result = bookService.getBookOwner(1L);
        assertNotNull(result);
        assertEquals(user, result);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateBook() {
        Book book = new Book();
        bookService.createBook(book);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testEditBook() {
        Book book = new Book();
        bookService.editBook(1L, book);
        assertEquals(1L, book.getBookId());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testDeleteBook() {
        bookService.deleteBook(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testReleaseBook() {
        Book book = new Book();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        bookService.releaseBook(1L);
        assertNull(book.getOwner());
        assertNull(book.getTakenAt());
        assertFalse(book.isExpired());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testAssignBook() {
        User user = new User();
        Book book = new Book();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        bookService.assignBook(1L, user);
        assertEquals(user, book.getOwner());
        assertNotNull(book.getTakenAt());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testSearchByTitle() {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(bookRepository.findByTitleStartingWith(anyString())).thenReturn(books);

        List<Book> result = bookService.searchByTitle("Title");
        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findByTitleStartingWith("Title");
    }
}
