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
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
import ru.abdusamatov.librarywithsecurity.util.ApiResponse;
import ru.abdusamatov.librarywithsecurity.util.Response;
import ru.abdusamatov.librarywithsecurity.util.mappers.BookMapper;
import ru.abdusamatov.librarywithsecurity.util.mappers.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static ru.abdusamatov.librarywithsecurity.util.ApiResponseStatus.SUCCESS;

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

        return buildApiResponse("List of books", bookDtoList);
    }

    @Transactional(readOnly = true)
    public ApiResponse<BookDto> getBookById(Long id) {
        BookDto foundBook = bookRepository.findById(id)
                .map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        return buildApiResponse("Book successfully found", foundBook);
    }

    @Transactional
    public ApiResponse<BookDto> createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        book.setOwner(null);

        Book savedBook = bookRepository.save(book);
        log.info("Save book with ID: {}", savedBook.getId());

        return buildApiResponse("Book successfully saved", bookMapper.bookToBookDto(savedBook));
    }

    @Transactional
    public ApiResponse<BookDto> updateBook(BookDto bookDto) {
        Book updatedBook = bookRepository.findById(bookDto.getId())
                .map(book -> bookMapper.updateBookFromDto(bookDto, book))
                .map(bookRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", bookDto.getId()));

        log.info("Updated book with ID: {}", updatedBook.getId());
        return buildApiResponse("Book successfully updated", bookMapper.bookToBookDto(updatedBook));
    }

    @Transactional
    public ApiResponse<String> deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        bookRepository.delete(book);
        log.info("Deleted book with ID: {}", id);
        return buildApiResponse("Book successfully deleted", "Book successfully deleted");
    }

    @Transactional
    public ApiResponse<String> releaseBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "ID", id));

        book.setOwner(null);
        book.setTakenAt(null);

        bookRepository.save(book);

        return buildApiResponse("Successfully released", "Book successfully released");
    }

    @Transactional
    public ApiResponse<String> assignBook(Long bookId, UserDto userDto) {
        Book book = bookRepository
                .findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "ID", bookId));

        book.setOwner(userMapper.dtoToUser(userDto));
        book.setTakenAt(LocalDateTime.now());
        bookRepository.save(book);

        log.info("Book with id {},has new owner with id {}", book.getId(), userDto.getId());
        return buildApiResponse("Successfully assigned", "Book successfully assigned");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<BookDto>> searchByTitle(String title) {
        List<BookDto> foundBookDtoList = bookRepository
                .findByTitleStartingWith(title)
                .stream()
                .map(bookMapper::bookToBookDto)
                .toList();

        return buildApiResponse(String.format("Found books with title %s", title), foundBookDtoList);
    }

    private <T> ApiResponse<T> buildApiResponse(String message, T data) {
        return new ApiResponse<>(message, new Response<>(SUCCESS, data));
    }
}
