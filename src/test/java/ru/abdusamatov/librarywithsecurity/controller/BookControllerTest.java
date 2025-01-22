package ru.abdusamatov.librarywithsecurity.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.support.TestAssertUtil;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.util.ParameterizedTypeReferenceTestUtil;
import ru.ilyam.http.Response;
import ru.ilyam.enums.LibraryEventNameEnum;
import ru.ilyam.event.LibraryEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class BookControllerTest extends TestBase {

    private static final String BASE_URL = "books";

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(publisher);
    }

    @Test
    void shouldGetAllBooks() {
        final var bookListSize = 10;
        final var bookList = TestDataProvider.createListBook(bookListSize);
        bookRepository.saveAll(bookList);


        final var response = executeGetAllBook();

        TestAssertUtil
                .assertSuccess(OK, "List of books", response);
        assertThat(response.getData())
                .asList()
                .isNotNull()
                .isNotEmpty()
                .hasSize(bookListSize);
    }

    @Test
    void shouldReturnEmptyList_whenBooksAreAbsent() {
        final var response = executeGetAllBook();

        TestAssertUtil
                .assertSuccess(OK, "List of books", response);
        assertThat(response.getData())
                .isEmpty();
    }

    @Test
    void shouldReturnBook_whenExistingBookIdProvided() {
        final var id = bookRepository
                .save(TestDataProvider
                        .createBook()
                        .build())
                .getId();

        final var response = executeGetBookById(OK, id, BookDto.class);

        TestAssertUtil
                .assertSuccess(OK, "Book successfully found", response);
        assertThat(response.getData().getId())
                .isEqualTo(id);
    }

    @Test
    void shouldReturnNotFound_whenNonExistingBookIdProvided() {
        final var id = 10000L;

        final var response = executeGetBookById(NOT_FOUND, id, Void.class);

        TestAssertUtil.assertEntityNotFound(response);
    }

    @Test
    void shouldCreateBook_whenValidDataProvided() {
        final var validBookDto = TestDataProvider
                .createBookDto()
                .build();

        final var response = executeCreateBook(OK, validBookDto, BookDto.class);

        TestAssertUtil
                .assertSuccess(CREATED, "Book successfully created", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("takenAt", "owner", "id")
                .isEqualTo(validBookDto);
        assertThat(response.getData().getId())
                .isNotNull();
        assertThat(response.getData())
                .extracting("takenAt", "userId")
                .containsOnlyNulls();
    }

    @Test
    void shouldReturnBadRequest_whenBookWithInvalidFields() {
        final var invalidBookDto = TestDataProvider
                .createBookDtoWithInvalidFields()
                .build();

        final var response = executeCreateBook(BAD_REQUEST, invalidBookDto, Void.class);

        TestAssertUtil.assertFieldErrorForEntity(response);
    }

    @Test
    void shouldUpdateBook_whenValidBookDtoProvided() {
        final var bookToBeUpdated = bookRepository
                .save(TestDataProvider
                        .createBook()
                        .build());

        final var updateBookDto = TestDataProvider
                .updateBookDto(bookMapper.bookToBookDto(bookToBeUpdated))
                .build();

        final var response = executeUpdateBook(OK, updateBookDto, BookDto.class);

        TestAssertUtil
                .assertSuccess(OK, "Book successfully updated", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateBookDto);
    }

    @Test
    void shouldReturnNotFound_whenBookToUpdateDoesNotExist() {
        final var notExistingId = 10000L;
        final var bookToBeUpdated = TestDataProvider
                .createBookDto()
                .build();

        final var updateBookDto = TestDataProvider
                .updateBookDto(bookToBeUpdated)
                .id(notExistingId)
                .build();

        final var response = executeUpdateBook(NOT_FOUND, updateBookDto, Void.class);

        TestAssertUtil.assertEntityNotFound(response);
    }

    @Test
    void shouldReturnBadRequest_whenUpdateBookWithInvalidFields() {
        final var bookToBeUpdated = TestDataProvider
                .createBookDto()
                .build();

        final var invalidBookDto = TestDataProvider
                .updateBookDtoWithInvalidFields(bookToBeUpdated)
                .build();

        final var response = executeUpdateBook(BAD_REQUEST, invalidBookDto, Void.class);

        TestAssertUtil.assertFieldErrorForEntity(response);
    }

    @Test
    void shouldReturnNotFound_whenBookToUpdateWithUserIdDoesNotExist() {
        final var notExistingUserId = 10000L;
        final var bookToBeUpdated = bookRepository.save(TestDataProvider.createBook().build());
        final var updateBookDto = TestDataProvider
                .updateBookDto(bookMapper.bookToBookDto(bookToBeUpdated))
                .userId(notExistingUserId)
                .build();

        final var response = executeUpdateBook(NOT_FOUND, updateBookDto, Void.class);

        TestAssertUtil.assertEntityNotFound(response);
    }

    @Test
    void shouldReturnNoContent_whenBookDeletedSuccessfully() {
        final var id = bookRepository
                .save(TestDataProvider
                        .createBook()
                        .build())
                .getId();

        final var response = executeDeleteBook(OK, id);

        TestAssertUtil
                .assertSuccess(NO_CONTENT, "Successfully deleted", response);
    }

    @Test
    void shouldReturnNotFound_whenBookToDeleteDoesNotExist() {
        final var notExistingId = 10000L;

        final var response = executeDeleteBook(NOT_FOUND, notExistingId);

        TestAssertUtil.assertEntityNotFound(response);
    }

    @Test
    void shouldAssignBook_whenValidDataProvided() {
        final var savedBook = bookRepository
                .save(TestDataProvider
                        .createBook()
                        .build());
        final var id = savedBook.getId();
        final var userDtoToBeAssigned = userMapper
                .userToDto(userRepository.save(TestDataProvider.createUser()));
        final var event = TestDataProvider
                .createLibraryEvent(LibraryEventNameEnum.BOOK_ASSIGNED)
                .userName(userDtoToBeAssigned.getFullName())
                .bookName(savedBook.getTitle())
                .build();

        final var response = executeAssignBook(OK, id, userDtoToBeAssigned);

        TestAssertUtil
                .assertSuccess(NO_CONTENT, "Book successfully assigned", response);

        final var captor = ArgumentCaptor.forClass(LibraryEvent.class);
        verify(publisher).publishEvent(captor.capture());
        assertThat(captor.getValue())
                .satisfies(eventMessage -> {
                    assertThat(eventMessage.getUserName()).isEqualTo(event.getUserName());
                    assertThat(eventMessage.getBookName()).isEqualTo(event.getBookName());
                    assertThat(eventMessage.getEventName()).isEqualTo(event.getEventName());
                    assertThat(eventMessage.getApplicationName()).isEqualTo(event.getApplicationName());
                });

        verify(publisher).publishEvent(any(LibraryEvent.class));
    }

    @Test
    void shouldReturnNotFound_whenBookToAssignDoesNotExist() {
        final var userDtoToBeAssigned = userMapper
                .userToDto(userRepository.save(TestDataProvider.createUser()));

        final var notExistingBookId = 1000L;

        final var response = executeAssignBook(NOT_FOUND, notExistingBookId, userDtoToBeAssigned);

        TestAssertUtil.assertEntityNotFound(response);
    }

    @Test
    void shouldReleaseBook_whenValidDataProvided() {
        final var id = bookRepository
                .save(TestDataProvider
                        .createBook()
                        .build())
                .getId();

        final var response = executeReleaseBook(OK, id);

        TestAssertUtil
                .assertSuccess(NO_CONTENT, "Book successfully released", response);
    }

    @Test
    void shouldReturnNotFound_whenBookToReleaseDoesNotExist() {
        final var notExistingBookId = 10000L;

        final var response = executeReleaseBook(NOT_FOUND, notExistingBookId);

        TestAssertUtil.assertEntityNotFound(response);
    }

    @Test
    void shouldReturnBooks_whenValidQueryProvided() {
        final var bookListSize = 10;
        final var bookList = TestDataProvider.createListBook(bookListSize);
        bookList.forEach(book -> {
            book.setOwner(null);
            bookRepository.save(book);
        });

        final var query = bookList
                .getFirst()
                .getTitle();

        final var response = executeSearchByTitle(query);

        TestAssertUtil
                .assertSuccess(OK, "Found books with title " + query, response);
        assertThat(response.getData())
                .hasSize(1);
    }

    @Test
    void shouldReturnEmptyBookList_whenQueryDoNotSatisfiesAnyBook() {
        final var query = "Not existing title";

        final var response = executeSearchByTitle(query);

        TestAssertUtil
                .assertSuccess(OK, "Found books with title " + query, response);
        assertThat(response.getData())
                .isEmpty();
    }

    private Response<List<BookDto>> executeGetAllBook() {

        final var response = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .queryParam("page", 0)
                        .queryParam("size", 20)
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectBody(ParameterizedTypeReferenceTestUtil.getListResponseReference(BookDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private <T> Response<T> executeGetBookById(
            final HttpStatus status,
            final Long id,
            final Class<T> responseType
    ) {
        final var response = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference(responseType))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private <T> Response<T> executeCreateBook(
            final HttpStatus status,
            final BookDto bookDto,
            final Class<T> responseType
    ) {
        final var response = webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookDto)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference(responseType))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private <T> Response<T> executeUpdateBook(
            final HttpStatus status,
            final BookDto bookDto,
            final Class<T> responseType
    ) {
        final var response = webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookDto)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference(responseType))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private Response<Void> executeDeleteBook(
            final HttpStatus status,
            final Long id
    ) {
        final var response = webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private Response<Void> executeAssignBook(
            final HttpStatus status,
            final Long bookId,
            final UserDto userDto
    ) {
        final var response = webTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(bookId), "assign")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private Response<Void> executeReleaseBook(
            final HttpStatus status,
            final Long id
    ) {
        final var response = webTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id), "release")
                        .build())
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private Response<List<BookDto>> executeSearchByTitle(final String query) {
        final var response = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "search")
                        .queryParam("query", query)
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceTestUtil.getListResponseReference(BookDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    @Override
    protected void clearDatabase() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }
}
