package ru.abdusamatov.librarywithsecurity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.services.BookService;
import ru.abdusamatov.librarywithsecurity.util.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books",
            produces = {"application/json"})
    public ResponseEntity<ApiResponse<List<BookDto>>> getBookList(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "true") boolean isSorted) {

        return ResponseEntity.ok(bookService.getBookList(page, size, isSorted));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<ApiResponse<BookDto>> showBookById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/books",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<ApiResponse<BookDto>> createBook(@Valid @RequestBody BookDto bookDto) {
        return new ResponseEntity<>(bookService.createBook(bookDto), HttpStatus.CREATED);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/books",
            consumes = {"application/json"}
    )
    public ResponseEntity<ApiResponse<String>> updateBook(@Valid @RequestBody BookDto bookDto) {
        return new ResponseEntity<>(bookService.editBook(bookDto), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/books/{id}"
    )
    public ResponseEntity<ApiResponse<String>> deleteBook(@PathVariable("id") Long id) {
        return new ResponseEntity<>(bookService.deleteBook(id), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/books/{id}/release"
    )
    public ResponseEntity<ApiResponse<String>> releaseBook(@PathVariable("id") Long id) {
        return new ResponseEntity<>(bookService.releaseBook(id), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/books/{id}/assign",
            consumes = {"application/json"}
    )
    public ResponseEntity<ApiResponse<String>> assignBook(@PathVariable("id") Long id, @Valid @RequestBody UserDto newUser) {
        return new ResponseEntity<>(bookService.assignBook(id, newUser), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books/search",
            produces = {"application/json"}
    )
    public ResponseEntity<ApiResponse<List<BookDto>>> searchBooks(@RequestParam(value = "query") String query) {
        return ResponseEntity.ok(bookService.searchByTitle(query));
    }
}
