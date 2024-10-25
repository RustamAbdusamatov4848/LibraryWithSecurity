package ru.abdusamatov.librarywithsecurity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.abdusamatov.librarywithsecurity.exceptions.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.services.BookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books",
            produces = {"application/json"})
    public ResponseEntity<List<BookDto>> getBookList(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "true") boolean isSorted) {

        Sort sort = isSorted ? Sort.by("title").ascending() : Sort.unsorted();
        Pageable pageable = PageRequest.of(page, size, sort);
        List<BookDto> bookDtoList = bookService.getBookList(pageable).getContent();
        return ResponseEntity.ok(bookDtoList);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<BookDto> showBookById(@PathVariable("id") Long id) {
        return bookService.getBookById(id)
                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/books",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        BookDto createdBook = bookService.createBook(bookDto);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/books",
            consumes = {"application/json"}
    )
    public ResponseEntity<Void> updateBook(@Valid @RequestBody BookDto bookDto) {
        if (!bookService.isExistBook(bookDto.getId())) {
            throw new ResourceNotFoundException("Book", bookDto.getId());
        }
        bookService.editBook(bookDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/books/{id}"
    )
    public ResponseEntity<Void> deleteBook(@PathVariable("id") Long id) {
        if (!bookService.isExistBook(id)) {
            throw new ResourceNotFoundException("Book", id);
        }
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/books/{id}/release"
    )
    public ResponseEntity<Void> releaseBook(@PathVariable("id") Long id) {
        bookService.releaseBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/books/{id}/assign",
            consumes = {"application/json"}
    )
    public ResponseEntity<Void> assignBook(@PathVariable("id") Long id, @Valid @RequestBody UserDto newUser) {
        bookService.assignBook(id, newUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/books/search",
            produces = {"application/json"}
    )
    public ResponseEntity<List<BookDto>> searchBooks(@RequestParam(value = "query") String query) {
        List<BookDto> bookDtoList = bookService.searchByTitle(query);
        return ResponseEntity.ok(bookDtoList);
    }
}
