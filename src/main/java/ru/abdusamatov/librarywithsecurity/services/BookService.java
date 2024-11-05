package ru.abdusamatov.librarywithsecurity.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.exceptions.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;
import ru.abdusamatov.librarywithsecurity.util.mappers.BookMapper;
import ru.abdusamatov.librarywithsecurity.util.mappers.UserMapper;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<BookDto> getBookList(Integer page, Integer size, boolean isSorted) {
        Sort sort = isSorted ? Sort.by("title").ascending() : Sort.unsorted();

        return bookRepository
                .findAll(PageRequest.of(page, size, sort))
                .map(bookMapper::bookToBookDto)
                .getContent();
    }

    @Transactional(readOnly = true)
    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));
    }

    @Transactional
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        book.setOwner(null);

        Book savedBook = bookRepository.save(book);
        log.info("Save book with ID: {}", savedBook.getId());

        return bookMapper.bookToBookDto(savedBook);
    }

    @Transactional
    public BookDto updateBook(BookDto bookDto) {
        Book updatedBook = bookRepository.findById(bookDto.getId())
                .map(book -> {
                    bookMapper.updateBookFromDto(bookDto, book);
                    if (bookDto.getUserId() != null) {
                        User owner = userRepository.findById(bookDto.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", bookDto.getUserId()));
                        book.setOwner(owner);
                    } else {
                        book.setOwner(null);
                    }
                    return book;
                })
                .map(bookRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", bookDto.getId()));

        log.info("Updated book with ID: {}", updatedBook.getId());
        return bookMapper.bookToBookDto(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        bookRepository.delete(book);
        log.info("Deleted book with ID: {}", id);
    }

    @Transactional
    public void assignBook(Long bookId, UserDto userDto) {
        Book book = bookRepository
                .findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "ID", bookId));

        book.setOwner(userMapper.dtoToUser(userDto));
        book.setTakenAt(LocalDateTime.now());
        bookRepository.save(book);

        log.info("Book with id {},has new owner with id {}", book.getId(), userDto.getId());
    }

    @Transactional
    public void releaseBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        book.setOwner(null);
        book.setTakenAt(null);
        bookRepository.save(book);

        log.info("Book with id {}, has been successfully released", id);
    }

    @Transactional(readOnly = true)
    public List<BookDto> searchByTitle(String title) {
        return bookRepository
                .findByTitleStartingWith(title)
                .stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }
}
