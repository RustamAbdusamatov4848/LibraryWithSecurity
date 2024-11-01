package ru.abdusamatov.librarywithsecurity.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;
import ru.abdusamatov.librarywithsecurity.util.Response;
import ru.abdusamatov.librarywithsecurity.util.Result;
import ru.abdusamatov.librarywithsecurity.util.mappers.BookMapper;
import ru.abdusamatov.librarywithsecurity.util.mappers.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Response<List<BookDto>> getBookList(Integer page, Integer size, boolean isSorted) {
        Sort sort = isSorted ? Sort.by("title").ascending() : Sort.unsorted();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<BookDto> bookDtoList = bookRepository.findAll(pageable)
                .map(bookMapper::bookToBookDto)
                .getContent();

        return Response.buildResponse(Result.success(OK, "List of books"), bookDtoList);
    }

    @Transactional(readOnly = true)
    public Response<BookDto> getBookById(Long id) {
        BookDto foundBook = bookRepository.findById(id)
                .map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        return Response.buildResponse(Result.success(OK, "Book successfully found"), foundBook);
    }

    @Transactional
    public Response<BookDto> createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        book.setOwner(null);

        Book savedBook = bookRepository.save(book);
        log.info("Save book with ID: {}", savedBook.getId());

        return Response.buildResponse(Result.success(CREATED, "Book successfully created"),
                bookMapper.bookToBookDto(savedBook));
    }

    @Transactional
    public Response<BookDto> updateBook(BookDto bookDto) {
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
        return Response.buildResponse(Result.success(OK, "Book successfully updated"),
                bookMapper.bookToBookDto(updatedBook));
    }

    @Transactional
    public Response<Void> deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        bookRepository.delete(book);

        log.info("Deleted book with ID: {}", id);
        return Response.buildResponse(Result.success(NO_CONTENT, "Successfully deleted"), null);
    }

    @Transactional
    public Response<Void> releaseBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        book.setOwner(null);
        book.setTakenAt(null);
        bookRepository.save(book);

        log.info("Book with id {}, has been successfully released", id);
        return Response.buildResponse(Result.success(NO_CONTENT, "Book successfully released"), null);
    }

    @Transactional
    public Response<Void> assignBook(Long bookId, UserDto userDto) {
        Book book = bookRepository
                .findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "ID", bookId));

        book.setOwner(userMapper.dtoToUser(userDto));
        book.setTakenAt(LocalDateTime.now());
        bookRepository.save(book);

        log.info("Book with id {},has new owner with id {}", book.getId(), userDto.getId());
        return Response.buildResponse(Result.success(NO_CONTENT, "Book successfully assigned"), null);
    }

    @Transactional(readOnly = true)
    public Response<List<BookDto>> searchByTitle(String title) {
        List<BookDto> foundBookDtoList = bookRepository
                .findByTitleStartingWith(title)
                .stream()
                .map(bookMapper::bookToBookDto)
                .toList();

        return Response.buildResponse(Result.success(OK, String.format("Found books with title %s", title)),
                foundBookDtoList);
    }
}
