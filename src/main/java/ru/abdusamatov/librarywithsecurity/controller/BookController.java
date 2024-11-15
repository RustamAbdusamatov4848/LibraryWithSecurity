package ru.abdusamatov.librarywithsecurity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "book")
public class BookController {
    private final BookService bookService;

    @GetMapping(
            produces = {"application/json"})
    public Response<List<BookDto>> getBookList(
            @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") final Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "true") final boolean isSorted) {

        return Response.buildResponse(
                Result.success(OK, "List of books"),
                bookService.getBookList(page, size, isSorted)
        );
    }

    @Cacheable(key = "#id")
    @GetMapping(
            value = "/{id}",
            produces = {"application/json"}
    )
    public Response<BookDto> showBookById(@PathVariable("id") final Long id) {
        return Response.buildResponse(
                Result.success(OK, "Book successfully found"),
                bookService.getBookById(id)
        );
    }

    @PostMapping(
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Response<BookDto> createBook(@Valid @RequestBody final BookDto bookDto) {
        return Response.buildResponse(
                Result.success(CREATED, "Book successfully created"),
                bookService.createBook(bookDto)
        );
    }

    @CachePut(key = "#bookDto.id")
    @PutMapping(
            consumes = {"application/json"}
    )
    public Response<BookDto> updateBook(@Valid @RequestBody final BookDto bookDto) {
        return Response.buildResponse(
                Result.success(OK, "Book successfully updated"),
                bookService.updateBook(bookDto)
        );
    }

    @CacheEvict(key = "#id")
    @DeleteMapping(
            value = "/{id}"
    )
    public Response<Void> deleteBook(@PathVariable("id") final Long id) {
        bookService.deleteBook(id);
        return Response.buildResponse(Result.success(NO_CONTENT, "Successfully deleted"), null);
    }

    @CachePut(key = "#id")
    @PatchMapping(
            value = "/{id}/assign",
            consumes = {"application/json"}
    )
    public Response<Void> assignBook(@PathVariable("id") final Long id, @Valid @RequestBody final UserDto newUser) {
        bookService.assignBook(id, newUser);
        return Response.buildResponse(Result.success(NO_CONTENT, "Book successfully assigned"), null);
    }

    @CachePut(key = "#id")
    @PatchMapping(
            value = "/{id}/release"
    )
    public Response<Void> releaseBook(@PathVariable("id") final Long id) {
        bookService.releaseBook(id);
        return Response.buildResponse(Result.success(NO_CONTENT, "Book successfully released"), null);
    }

    @Cacheable(value = "bookTitle", key = "#query")
    @GetMapping(
            value = "/search",
            produces = {"application/json"}
    )
    public Response<List<BookDto>> searchBooks(@RequestParam(value = "query") final String query) {
        return Response.buildResponse(
                Result.success(OK, String.format("Found books with title %s", query)),
                bookService.searchByTitle(query)
        );
    }
}
