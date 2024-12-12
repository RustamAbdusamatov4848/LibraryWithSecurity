package ru.abdusamatov.librarywithsecurity.controller;

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
public class BookController {
    private final BookService bookService;

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

    @GetMapping(value = "/{id}")
    public Mono<Response<BookDto>> showBookById(@PathVariable("id") final Long id) {
        return bookService
                .getBookById(id)
                .map(bookDto -> Response.buildResponse(
                        Result.success(OK, "Book successfully found"),
                        bookDto));
    }

    @PostMapping
    public Mono<Response<BookDto>> createBook(@Valid @RequestBody final BookDto bookDto) {
        return bookService
                .createBook(bookDto)
                .map(savedBook -> Response.buildResponse(
                        Result.success(CREATED, "Book successfully created"),
                        savedBook));
    }

    @PutMapping
    public Response<BookDto> updateBook(@Valid @RequestBody final BookDto bookDto) {
        return Response.buildResponse(
                Result.success(OK, "Book successfully updated"),
                bookService.updateBook(bookDto)
        );
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Response<Void>> deleteBook(@PathVariable("id") final Long id) {
        return bookService
                .deleteBook(id)
                .then(Mono.just(Response.buildResponse(
                        Result.success(NO_CONTENT, "Successfully deleted"),
                        null)));
    }

    @PatchMapping(value = "/{id}/assign")
    public Response<Void> assignBook(@PathVariable("id") final Long id, @Valid @RequestBody final UserDto newUser) {
        bookService.assignBook(id, newUser);
        return Response.buildResponse(Result.success(NO_CONTENT, "Book successfully assigned"), null);
    }

    @PatchMapping(value = "/{id}/release")
    public Response<Void> releaseBook(@PathVariable("id") final Long id) {
        bookService.releaseBook(id);
        return Response.buildResponse(Result.success(NO_CONTENT, "Book successfully released"), null);
    }

    @GetMapping(value = "/search")
    public Response<List<BookDto>> searchBooks(@RequestParam(value = "query") final String query) {
        return Response.buildResponse(
                Result.success(OK, String.format("Found books with title %s", query)),
                bookService.searchByTitle(query)
        );
    }
}
