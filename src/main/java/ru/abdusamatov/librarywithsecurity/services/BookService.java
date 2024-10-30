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
import ru.abdusamatov.librarywithsecurity.util.ApiResponse;
import ru.abdusamatov.librarywithsecurity.util.ApiResponseStatus;
import ru.abdusamatov.librarywithsecurity.util.Response;
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
    public ApiResponse<List<BookDto>> getBookList(Integer page, Integer size, boolean isSorted) {
        Sort sort = isSorted ? Sort.by("title").ascending() : Sort.unsorted();
        Pageable pageable = PageRequest.of(page, size, sort);
        List<BookDto> bookDtoList = bookRepository.findAll(pageable)
                .map(bookMapper::bookToBookDto)
                .getContent();
        Response<List<BookDto>> response = new Response<>(ApiResponseStatus.SUCCESS, bookDtoList);
        return new ApiResponse<>("List of books", response);
    }

    @Transactional(readOnly = true)
    public ApiResponse<BookDto> getBookById(Long id) {
        BookDto foundBook = bookRepository
                .findById(id)
                .map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        Response<BookDto> response = new Response<>(ApiResponseStatus.SUCCESS, foundBook);
        return new ApiResponse<>("Book successfully found", response);
    }

    @Transactional
    public ApiResponse<BookDto> createBook(BookDto bookDto) {
        Book savedBook = bookRepository
                .save(bookMapper.bookDtoToBook(bookDto));

        log.info("Save book with ID: {}", savedBook.getId());
        Response<BookDto> response = new Response<>(ApiResponseStatus.SUCCESS, bookMapper.bookToBookDto(savedBook));
        return new ApiResponse<>("Book successfully saved", response);
    }

    @Transactional
    public ApiResponse<String> editBook(BookDto bookDto) {
        Long id = bookDto.getId();
        if (!isBookExist(id)) {
            throw new ResourceNotFoundException("Book", "ID", id);
        }
        bookRepository
                .findById(bookDto.getId())
                .map(book -> bookMapper.updateBookFromDto(bookDto, book))
                .map(bookRepository::save);
        log.info("Update book with ID: {}", bookDto.getId());
        Response<String> response = new Response<>(ApiResponseStatus.SUCCESS, "Book successfully updated");
        return new ApiResponse<>(" Successfully updated", response);
    }

    @Transactional
    public ApiResponse<String> deleteBook(Long id) {
        if (!isBookExist(id)) {
            throw new ResourceNotFoundException("Book", "ID", id);
        }
        bookRepository.deleteById(id);
        log.info("Delete book with ID: {}", id);
        Response<String> response = new Response<>(ApiResponseStatus.SUCCESS, "Book successfully deleted");
        return new ApiResponse<>(" Successfully deleted", response);
    }

    @Transactional
    public ApiResponse<String> releaseBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        book.setOwner(null);
        book.setTakenAt(null);
        book.setExpired(false);

        bookRepository.save(book);

        Response<String> response = new Response<>(ApiResponseStatus.SUCCESS, "Book successfully released");
        return new ApiResponse<>(" Successfully released", response);
    }

    @Transactional
    public ApiResponse<String> assignBook(Long bookId, UserDto userDto) {
        Book book = bookRepository
                .findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "ID", bookId));

        User newOwner = userMapper.dtoToUser(userDto);
        book.setOwner(newOwner);
        book.setTakenAt(LocalDateTime.now());
        bookRepository.save(book);

        log.info("Book with id {},has new owner with id {}", book.getId(), newOwner.getId());

        Response<String> response = new Response<>(ApiResponseStatus.SUCCESS, "Book successfully assigned");
        return new ApiResponse<>(" Successfully assigned", response);
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<BookDto>> searchByTitle(String title) {
        List<BookDto> bookDtoList = bookRepository
                .findByTitleStartingWith(title)
                .stream()
                .map(bookMapper::bookToBookDto)
                .toList();

        Response<List<BookDto>> response = new Response<>(ApiResponseStatus.SUCCESS, bookDtoList);
        return new ApiResponse<>(String.format("Found books with title %s", title), response);
    }

    @Transactional(readOnly = true)
    public boolean isBookExist(Long id) {
        return bookRepository.existsById(id);
    }
}
