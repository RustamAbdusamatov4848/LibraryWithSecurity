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
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.model.User;
import ru.abdusamatov.librarywithsecurity.repository.BookRepository;
import ru.abdusamatov.librarywithsecurity.repository.UserRepository;
import ru.abdusamatov.librarywithsecurity.service.mapper.BookMapper;
import ru.abdusamatov.librarywithsecurity.service.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;


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
    public List<BookDto> getBookList(final Integer page, final Integer size, final boolean isSorted) {
        final var sort = isSorted ? Sort.by("title").ascending() : Sort.unsorted();

        return bookRepository
                .findAll(PageRequest.of(page, size, sort))
                .map(bookMapper::bookToBookDto)
                .getContent();
    }

    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public BookDto getBookById(final Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));
    }

    @Transactional
    public BookDto createBook(final BookDto dto) {
        var book = bookMapper.bookDtoToBook(dto);
        book.setOwner(null);

        final var savedBook = bookRepository.save(book);
        log.info("Save book with ID: {}", savedBook.getId());

        return bookMapper.bookToBookDto(savedBook);
    }

    @CachePut(key = "#dto.id")
    @Transactional
    public BookDto updateBook(final BookDto dto) {
        final var updatedBook = bookRepository.findById(dto.getId())
                .map(book -> {
                    bookMapper.updateBookFromDto(dto, book);
                    if (dto.getUserId() != null) {
                        User owner = userRepository.findById(dto.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", dto.getUserId()));
                        book.setOwner(owner);
                    } else {
                        book.setOwner(null);
                    }
                    return book;
                })
                .map(bookRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", dto.getId()));

        log.info("Updated book with ID: {}", updatedBook.getId());
        return bookMapper.bookToBookDto(updatedBook);
    }

    @CacheEvict(key = "#id")
    @Transactional
    public void deleteBook(final Long id) {
        final var book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        bookRepository.delete(book);
        log.info("Deleted book with ID: {}", id);
    }

    @CachePut(key = "#id")
    @Transactional
    public void assignBook(final Long id, final UserDto userDto) {
        final var book = bookRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        book.setOwner(userMapper.dtoToUser(userDto));
        book.setTakenAt(LocalDateTime.now());
        bookRepository.save(book);

        log.info("Book with id {},has new owner with id {}", book.getId(), userDto.getId());
    }

    @CachePut(key = "#id")
    @Transactional
    public void releaseBook(final Long id) {
        final var book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        book.setOwner(null);
        book.setTakenAt(null);
        bookRepository.save(book);

        log.info("Book with id {}, has been successfully released", id);
    }

    @Transactional(readOnly = true)
    public List<BookDto> searchByTitle(final String query) {
        return bookRepository
                .findByTitleStartingWith(query)
                .stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }
}
