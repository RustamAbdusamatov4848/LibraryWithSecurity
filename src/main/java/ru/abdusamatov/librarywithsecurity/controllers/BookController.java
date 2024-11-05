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
import ru.abdusamatov.librarywithsecurity.util.Result;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

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

        return Response.buildResponse(
                Result.success(OK, "List of books"),
                bookService.getBookList(page, size, isSorted)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books/{id}",
            produces = {"application/json"}
    )
    public Response<BookDto> showBookById(@PathVariable("id") Long id) {
        return Response.buildResponse(
                Result.success(OK, "Book successfully found"),
                bookService.getBookById(id)
        );
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/books",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Response<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        return Response.buildResponse(
                Result.success(CREATED, "Book successfully created"),
                bookService.createBook(bookDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/books",
            consumes = {"application/json"}
    )
    public Response<BookDto> updateBook(@Valid @RequestBody BookDto bookDto) {
        return Response.buildResponse(
                Result.success(OK, "Book successfully updated"),
                bookService.updateBook(bookDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/books/{id}"
    )
    public Response<Void> deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return Response.buildResponse(Result.success(NO_CONTENT, "Successfully deleted"), null);
    }

    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/books/{id}/assign",
            consumes = {"application/json"}
    )
    public Response<Void> assignBook(@PathVariable("id") Long id, @Valid @RequestBody UserDto newUser) {
        bookService.assignBook(id,newUser);
        return Response.buildResponse(Result.success(NO_CONTENT, "Book successfully assigned"), null);
    }

    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/books/{id}/release"
    )
    public Response<Void> releaseBook(@PathVariable("id") Long id) {
        bookService.releaseBook(id);
        return Response.buildResponse(Result.success(NO_CONTENT, "Book successfully released"), null);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books/search",
            produces = {"application/json"}
    )
    public Response<List<BookDto>> searchBooks(@RequestParam(value = "query") String query) {
        return Response.buildResponse(
                Result.success(OK, String.format("Found books with title %s", query)),
                bookService.searchByTitle(query)
        );
    }
}
