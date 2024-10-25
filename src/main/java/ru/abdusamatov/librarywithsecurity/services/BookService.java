package ru.abdusamatov.librarywithsecurity.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.exceptions.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
import ru.abdusamatov.librarywithsecurity.util.mappers.BookMapper;
import ru.abdusamatov.librarywithsecurity.util.mappers.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    public final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<BookDto> getBookList(Pageable pageable, boolean sort) {
        Page<Book> books = sort ?
                bookRepository.findAllSorted(pageable) : bookRepository.findAll(pageable);
        return books.map(bookMapper::bookToBookDto);
    }

    @Transactional(readOnly = true)
    public Optional<BookDto> getBookById(Long id) {
        return bookRepository
                .findById(id)
                .map(bookMapper::bookToBookDto);
    }


    @Transactional
    public BookDto createBook(BookDto bookDto) {
        Book savedBook = bookRepository
                .save(bookMapper.bookDtoToBook(bookDto));
        log.info("Save book with ID: {}", savedBook.getId());
        return bookMapper.bookToBookDto(savedBook);
    }

    @Transactional
    public void editBook(BookDto bookDto) {
        bookRepository
                .findById(bookDto.getId())
                .map(book -> bookMapper.updateBookFromDto(bookDto, book))
                .map(bookRepository::save);
        log.info("Update book with ID: {}", bookDto.getId());
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
        log.info("Delete book with ID: {}", id);
    }

    @Transactional
    public void releaseBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));

        book.setOwner(null);
        book.setTakenAt(null);
        book.setExpired(false);

        bookRepository.save(book);
    }

    @Transactional
    public void assignBook(Long bookId, UserDto userDto) {
        Book book = bookRepository
                .findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", bookId));

        book.setOwner(userMapper.dtoToUser(userDto));
        book.setTakenAt(LocalDateTime.now());

        bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public List<BookDto> searchByTitle(String query) {
        List<Book> books = bookRepository.findByTitleStartingWith(query);

        if (books.isEmpty()) {
            log.info("No books were found");
            return Collections.emptyList();
        }
        log.info("It was found {} books",books.size());
        return books.stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }
}
