package ru.abdusamatov.librarywithsecurity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.services.BookService;
import ru.abdusamatov.librarywithsecurity.util.Response;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books",
            produces = {"application/json"})
    public Response<List<BookDto>> getBookList(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "true") boolean isSorted) {

        return bookService.getBookList(page, size, isSorted);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books/{id}",
            produces = {"application/json"}
    )
    public Response<BookDto> showBookById(@PathVariable("id") Long id) {
        return bookService.getBookById(id);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/books",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Response<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        return bookService.createBook(bookDto);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/books",
            consumes = {"application/json"}
    )
    public Response<BookDto> updateBook(@Valid @RequestBody BookDto bookDto) {
        return bookService.updateBook(bookDto);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/books/{id}"
    )
    public Response<Void> deleteBook(@PathVariable("id") Long id) {
        return bookService.deleteBook(id);
    }

    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/books/{id}/assign",
            consumes = {"application/json"}
    )
    public Response<Void> assignBook(@PathVariable("id") Long id, @Valid @RequestBody UserDto newUser) {
        return bookService.assignBook(id, newUser);
    }

    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/books/{id}/release"
    )
    public Response<Void> releaseBook(@PathVariable("id") Long id) {
        return bookService.releaseBook(id);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books/search",
            produces = {"application/json"}
    )
    public Response<List<BookDto>> searchBooks(@RequestParam(value = "query") String query) {
        return bookService.searchByTitle(query);
    }
}
