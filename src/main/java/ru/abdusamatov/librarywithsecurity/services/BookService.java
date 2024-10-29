package ru.abdusamatov.librarywithsecurity.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.exceptions.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
import ru.abdusamatov.librarywithsecurity.util.mappers.BookMapper;
import ru.abdusamatov.librarywithsecurity.util.mappers.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    public final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<BookDto> getBookList(Integer page, Integer size, boolean isSorted) {
        Sort sort = isSorted ? Sort.by("title").ascending() : Sort.unsorted();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(bookMapper::bookToBookDto).getContent();
    }

    @Transactional(readOnly = true)
    public BookDto getBookById(Long id) {
        return bookRepository
                .findById(id)
                .map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));
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
        Long id = bookDto.getId();
        if (isBookExist(id)) {
            throw new ResourceNotFoundException("Book", "ID", id);
        }
        bookRepository
                .findById(bookDto.getId())
                .map(book -> bookMapper.updateBookFromDto(bookDto, book))
                .map(bookRepository::save);
        log.info("Update book with ID: {}", bookDto.getId());
    }

    @Transactional
    public void deleteBook(Long id) {
        if (isBookExist(id)) {
            throw new ResourceNotFoundException("Book", "ID", id);
        }
        bookRepository.deleteById(id);
        log.info("Delete book with ID: {}", id);
    }

    @Transactional
    public void releaseBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        book.setOwner(null);
        book.setTakenAt(null);
        book.setExpired(false);

        bookRepository.save(book);
    }

    @Transactional
    public void assignBook(Long bookId, UserDto userDto) {
        Book book = bookRepository
                .findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "ID", bookId));

        User newOwner = userMapper.dtoToUser(userDto);
        book.setOwner(newOwner);
        book.setTakenAt(LocalDateTime.now());

        bookRepository.save(book);
        log.info("Book with id {},has new owner with id {}", book.getId(), newOwner.getId());
    }

    @Transactional(readOnly = true)
    public List<BookDto> searchByTitle(String query) {
        return bookRepository
                .findByTitleStartingWith(query)
                .stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isBookExist(Long id) {
        return bookRepository.existsById(id);
    }
}
