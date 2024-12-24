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
import ru.abdusamatov.librarywithsecurity.service.handler.BookHandler;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {
    private final BookHandler bookHandler;

    @GetMapping
    public Mono<Response<List<BookDto>>> getBookList(
            @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") final Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "true") final boolean isSorted) {

        return bookHandler
                .getBookList(page, size, isSorted)
                .map(list -> Response.buildResponse(
                        Result.success(OK, "List of books"),
                        list));
    }

    @GetMapping(value = "/{id}")
    public Mono<Response<BookDto>> showBookById(@PathVariable("id") final Long id) {
        return bookHandler
                .getBookById(id)
                .map(bookDto -> Response.buildResponse(
                        Result.success(OK, "Book successfully found"),
                        bookDto));
    }

    @PostMapping
    public Mono<Response<BookDto>> createBook(@Valid @RequestBody final BookDto bookDto) {
        return bookHandler
                .createBook(bookDto)
                .map(savedBook -> Response.buildResponse(
                        Result.success(CREATED, "Book successfully created"),
                        savedBook));
    }

    @PutMapping
    public Mono<Response<BookDto>> updateBook(@Valid @RequestBody final BookDto bookDto) {
        return bookHandler.updateBook(bookDto)
                .map(updatedBook -> Response.buildResponse(
                        Result.success(OK, "Book successfully updated"),
                        updatedBook));
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Response<Void>> deleteBook(@PathVariable("id") final Long id) {
        return bookHandler
                .deleteBook(id)
                .then(Mono.just(Response.buildResponse(
                        Result.success(NO_CONTENT, "Successfully deleted"))));
    }

    @PatchMapping(value = "/{id}/assign")
    public Mono<Response<Void>> assignBook(@PathVariable("id") final Long id, @Valid @RequestBody final UserDto newUser) {
        return bookHandler
                .assignBook(id, newUser)
                .then(Mono.just(Response.buildResponse(
                        Result.success(NO_CONTENT, "Book successfully assigned"))));
    }

    @PatchMapping(value = "/{id}/release")
    public Mono<Response<Void>> releaseBook(@PathVariable("id") final Long id) {
        return bookHandler.releaseBook(id)
                .then(Mono.just(Response.buildResponse(
                        Result.success(NO_CONTENT, "Book successfully released"))));
    }

    @GetMapping(value = "/search")
    public Mono<Response<List<BookDto>>> searchBooks(@RequestParam(value = "query") final String query) {
        return bookHandler.searchByTitle(query)
                .map(boolList -> Response.buildResponse(
                        Result.success(OK, String.format("Found books with title %s", query)),
                        boolList));
    }
}
