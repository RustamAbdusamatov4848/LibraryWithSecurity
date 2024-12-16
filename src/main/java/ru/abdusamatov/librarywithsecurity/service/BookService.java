package ru.abdusamatov.librarywithsecurity.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.repository.BookRepository;
import ru.abdusamatov.librarywithsecurity.repository.UserRepository;
import ru.abdusamatov.librarywithsecurity.service.mapper.BookMapper;
import ru.abdusamatov.librarywithsecurity.service.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "book")
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Mono<List<BookDto>> getBookList(final Integer page, final Integer size, final boolean isSorted) {
        final var sort = isSorted ? Sort.by("title").ascending() : Sort.unsorted();

        return Mono.fromCallable(() ->
                        bookRepository.findAll(PageRequest.of(page, size, sort)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(pageResult -> {
                    if (pageResult.hasContent()) {
                        return Mono.just(pageResult.getContent()
                                .stream()
                                .map(bookMapper::bookToBookDto)
                                .collect(Collectors.toList()));
                    } else {
                        return Mono.just(Collections.emptyList());
                    }
                });
    }

    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public Mono<BookDto> getBookById(final Long id) {
        return Mono.fromCallable(() ->
                        bookRepository.findById(id)
                                .map(bookMapper::bookToBookDto)
                                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public Mono<BookDto> createBook(final BookDto dto) {
        var book = bookMapper.bookDtoToBook(dto);
        book.setOwner(null);

        return Mono.fromCallable(() -> bookRepository.save(book))
                .subscribeOn(Schedulers.boundedElastic())
                .map(bookMapper::bookToBookDto)
                .doOnNext(savedBook ->
                        log.info("Save book with ID: {}", savedBook.getId()));
    }

    @CachePut(key = "#dto.id")
    @Transactional
    public Mono<BookDto> updateBook(final BookDto dto) {
        return Mono.fromCallable(() -> bookRepository.findById(dto.getId()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalBook -> optionalBook
                        .map(book -> {
                            bookMapper.updateBookFromDto(dto, book);
                            if (dto.getUserId() != null) {
                                return Mono.fromCallable(() -> userRepository.findById(dto.getUserId()))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .flatMap(optionalUser -> optionalUser
                                                .map(owner -> {
                                                    book.setOwner(owner);
                                                    return Mono.just(book);
                                                })
                                                .orElseGet(() -> Mono.error(new ResourceNotFoundException("User", "ID", dto.getUserId()))));
                            } else {
                                book.setOwner(null);
                                return Mono.just(book);
                            }
                        })
                        .orElseGet(() -> Mono.error(new ResourceNotFoundException("Book", "ID", dto.getId()))))
                .flatMap(updatedBook -> Mono.fromCallable(() -> bookRepository.save(updatedBook))
                        .subscribeOn(Schedulers.boundedElastic()))
                .map(bookMapper::bookToBookDto)
                .doOnSuccess(savedBook -> log.info("Updated book with ID: {}", savedBook.getId()));
    }

    @CacheEvict(key = "#id")
    @Transactional
    public Mono<Void> deleteBook(final Long id) {
        return Mono.fromCallable(() ->
                        bookRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(foundBook -> foundBook
                        .map(book -> Mono.fromRunnable(() -> bookRepository.delete(book))
                                .subscribeOn(Schedulers.boundedElastic()))
                        .orElseGet(() -> Mono.error(new ResourceNotFoundException("Book", "ID", id))))
                .doOnSuccess(voidResponse -> log.info("Deleted book with ID: {}", id))
                .then();
    }

    @CachePut(key = "#id")
    @Transactional
    public Mono<Void> assignBook(final Long id, final UserDto userDto) {
        return Mono.fromCallable(() ->
                        bookRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalBook -> optionalBook
                        .map(book -> {
                            book.setOwner(userMapper.dtoToUser(userDto));
                            book.setTakenAt(LocalDateTime.now());
                            return Mono.fromCallable(() -> bookRepository.save(book))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .doOnNext(savedBook -> log.info("Book with id {},has new owner with id {}", savedBook.getId(), userDto.getId()));
                        }).orElseGet(() -> Mono.error(new ResourceNotFoundException("Book", "ID", id))))
                .then();
    }

    @CachePut(key = "#id")
    @Transactional
    public Mono<Void> releaseBook(final Long id) {
        return Mono.fromCallable(() -> bookRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalBook -> optionalBook
                        .map(book -> {
                            book.setOwner(null);
                            book.setTakenAt(null);
                            return Mono.fromCallable(() -> bookRepository.save(book))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .doOnNext(savedBook -> log.info("Book with id {}, has been successfully released", id));
                        }).orElseGet(() -> Mono.error(new ResourceNotFoundException("Book", "ID", id))))
                .then();
    }

    @Transactional(readOnly = true)
    public Mono<List<BookDto>> searchByTitle(final String query) {
        return Mono.fromCallable(() -> bookRepository.findByTitleStartingWith(query))
                .subscribeOn(Schedulers.boundedElastic())
                .map(bookList -> bookList
                        .stream()
                        .map(bookMapper::bookToBookDto)
                        .toList());
    }
}
