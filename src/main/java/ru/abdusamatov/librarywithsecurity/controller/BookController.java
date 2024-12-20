package ru.abdusamatov.librarywithsecurity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.dto.response.Result;
import ru.abdusamatov.librarywithsecurity.service.BookService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
@Tag(
        name = "Book Management",
        description = "APIs for managing library books"
)
public class BookController {
    private final BookService bookService;

    @Operation(summary = "Method for getting all registered books")
    @GetMapping
    public Mono<Response<List<BookDto>>> getBookList(
            @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") final Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "true") final boolean isSorted) {

        return bookService
                .getBookList(page, size, isSorted)
                .map(list -> Response.buildResponse(
                        Result.success(OK, "List of books"),
                        list));
    }

    @Operation(summary = "Method for retrieving a book by its ID")
    @GetMapping(value = "/{id}")
    public Mono<Response<BookDto>> showBookById(@PathVariable("id") final Long id) {
        return bookService
                .getBookById(id)
                .map(bookDto -> Response.buildResponse(
                        Result.success(OK, "Book successfully found"),
                        bookDto));
    }

    @Operation(summary = "Method for creating a new book")
    @PostMapping
    public Mono<Response<BookDto>> createBook(@Valid @RequestBody final BookDto bookDto) {
        return bookService
                .createBook(bookDto)
                .map(savedBook -> Response.buildResponse(
                        Result.success(CREATED, "Book successfully created"),
                        savedBook));
    }

    @Operation(summary = "Method for updating an existing book")
    @PutMapping
    public Mono<Response<BookDto>> updateBook(@Valid @RequestBody final BookDto bookDto) {
        return bookService.updateBook(bookDto)
                .map(updatedBook -> Response.buildResponse(
                        Result.success(OK, "Book successfully updated"),
                        updatedBook));
    }

    @Operation(summary = "Method for deleting a book by its ID")
    @DeleteMapping(value = "/{id}")
    public Mono<Response<Void>> deleteBook(@PathVariable("id") final Long id) {
        return bookService
                .deleteBook(id)
                .then(Mono.just(Response.buildResponse(
                        Result.success(NO_CONTENT, "Successfully deleted"),
                        null)));
    }

    @Operation(summary = "Method for assigning a book to a user")
    @PatchMapping(value = "/{id}/assign")
    public Mono<Response<Void>> assignBook(@PathVariable("id") final Long id, @Valid @RequestBody final UserDto newUser) {
        return bookService
                .assignBook(id, newUser)
                .then(Mono.just(Response.buildResponse(
                        Result.success(NO_CONTENT, "Book successfully assigned"),
                        null)));
    }

    @Operation(summary = "Method for releasing a book from a user")
    @PatchMapping(value = "/{id}/release")
    public Mono<Response<Void>> releaseBook(@PathVariable("id") final Long id) {
        return bookService.releaseBook(id)
                .then(Mono.just(Response.buildResponse(
                        Result.success(NO_CONTENT, "Book successfully released"),
                        null)));
    }

    @Operation(summary = "Method for searching books by their title")
    @GetMapping(value = "/search")
    public Mono<Response<List<BookDto>>> searchBooks(@RequestParam(value = "query") final String query) {
        return bookService.searchByTitle(query)
                .map(boolList -> Response.buildResponse(
                        Result.success(OK, String.format("Found books with title %s", query)),
                        boolList));
    }
}
