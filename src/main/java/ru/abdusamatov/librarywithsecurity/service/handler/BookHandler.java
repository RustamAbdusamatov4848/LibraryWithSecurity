package ru.abdusamatov.librarywithsecurity.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.service.BookService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookHandler {
    private final BookService bookService;

    public Mono<List<BookDto>> getBookList(final Integer page, final Integer size, final boolean isSorted) {
        return Mono.fromCallable(() -> bookService.getBookList(page, size, isSorted))
                .subscribeOn(Schedulers.boundedElastic())
                .map(bookDtoList -> bookDtoList.isEmpty() ? Collections.emptyList() : bookDtoList);
    }

    public Mono<BookDto> getBookById(final Long id) {
        return Mono.fromCallable(() -> bookService.getBookById(id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<BookDto> createBook(final BookDto dto) {
        return Mono.fromCallable(() -> bookService.createBook(dto))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<BookDto> updateBook(final BookDto dto) {
        return Mono.fromCallable(() -> bookService.updateBook(dto))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteBook(final Long id) {
        return Mono.fromRunnable(() -> bookService.deleteBook(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> assignBook(final Long id, final UserDto userDto) {
        return Mono.fromRunnable(() -> bookService.assignBook(id, userDto))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> releaseBook(final Long id) {
        return Mono.fromRunnable(() -> bookService.releaseBook(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<List<BookDto>> searchByTitle(final String query) {
        return Mono.fromCallable(() -> bookService.searchByTitle(query))
                .subscribeOn(Schedulers.boundedElastic())
                .map(bookDtoList -> bookDtoList.isEmpty() ? Collections.emptyList() : bookDtoList);
    }
}
