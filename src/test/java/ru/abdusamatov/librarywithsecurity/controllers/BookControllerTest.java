package ru.abdusamatov.librarywithsecurity.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.services.BookService;
import ru.abdusamatov.librarywithsecurity.services.UserService;

import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        initMocks();
        initMockMvc();
    }

    private void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    private void initMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testBookList() throws Exception {
        when(bookService.bookList(false)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/listOfBooks"))
                .andExpect(model().attributeExists("books"));

        verify(bookService, times(1)).bookList(false);
    }

    @Test
    public void testShowBookByIDWithOwner() throws Exception {
        Book book = new Book();
        User owner = new User();

        when(bookService.showBook(1L)).thenReturn(book);
        when(bookService.getBookOwner(1L)).thenReturn(owner);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/showBook"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attributeExists("owner"))
                .andExpect(model().attribute("owner", owner));

        verify(bookService, times(1)).showBook(1L);
        verify(bookService, times(1)).getBookOwner(1L);
    }

    @Test
    public void testShowBookByIDWithoutOwner() throws Exception {
        Book book = new Book();

        when(bookService.showBook(1L)).thenReturn(book);
        when(bookService.getBookOwner(1L)).thenReturn(null);
        when(userService.getUserList()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/showBook"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", Collections.emptyList()));

        verify(bookService, times(1)).showBook(1L);
        verify(bookService, times(1)).getBookOwner(1L);
        verify(userService, times(1)).getUserList();
    }

    @Test
    public void testAddNewBook() throws Exception {
        mockMvc.perform(get("/books/createBook"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/createBook"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    public void testCreateBookSuccessfully() throws Exception {
        mockMvc.perform(post("/books")
                        .param("title", "Valid Title")
                        .param("authorName", "John")
                        .param("authorSurname", "Doe")
                        .param("year", "2000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    public void testCreateBookWithValidationErrors() throws Exception {
        mockMvc.perform(post("/books")
                        .param("title", "")
                        .param("authorName", "John")
                        .param("authorSurname", "Doe")
                        .param("year", "2000"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/createBook"))
                .andExpect(model().attributeHasFieldErrors("book", "title"));

        verify(bookService, never()).createBook(any(Book.class));
    }

    @Test
    public void testEditBook() throws Exception {
        Book book = new Book();
        when(bookService.showBook(1L)).thenReturn(book);

        mockMvc.perform(get("/books/1/editBook"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/editBook"))
                .andExpect(model().attributeExists("book"));

        verify(bookService, times(1)).showBook(1L);
    }

    @Test
    public void testUpdateBookSuccessfully() throws Exception {
        mockMvc.perform(patch("/books/1")
                        .param("title", "Valid Title")
                        .param("authorName", "John")
                        .param("authorSurname", "Doe")
                        .param("year", "2000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).editBook(eq(1L), any(Book.class));
    }

    @Test
    public void testUpdateBookWithValidationErrors() throws Exception {
        mockMvc.perform(patch("/books/1")
                        .param("title", "")
                        .param("authorName", "John")
                        .param("authorSurname", "Doe")
                        .param("year", "2000"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/editBook"))
                .andExpect(model().attributeHasFieldErrors("book", "title"));

        verify(bookService, never()).editBook(anyLong(), any(Book.class));
    }

    @Test
    public void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    public void testReleaseBook() throws Exception {
        mockMvc.perform(patch("/books/1/release"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/1"));

        verify(bookService, times(1)).releaseBook(1L);
    }

    @Test
    public void testAssignBook() throws Exception {
        mockMvc.perform(patch("/books/1/assign")
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).assignBook(eq(1L), any(User.class));
    }

    @Test
    public void testSearchBook() throws Exception {
        mockMvc.perform(get("/books/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/search"));
    }

    @Test
    public void testMakeSearch() throws Exception {
        when(bookService.searchByTitle("query")).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/books/search")
                        .param("query", "query"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/search"))
                .andExpect(model().attributeExists("books"));

        verify(bookService, times(1)).searchByTitle("query");
    }
}
