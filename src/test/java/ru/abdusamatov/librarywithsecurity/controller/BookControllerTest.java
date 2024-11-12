package ru.abdusamatov.librarywithsecurity.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.repository.BookRepository;
import ru.abdusamatov.librarywithsecurity.repository.UserRepository;
import ru.abdusamatov.librarywithsecurity.service.BookService;
import ru.abdusamatov.librarywithsecurity.service.UserService;
import ru.abdusamatov.librarywithsecurity.support.TestControllerBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.support.TestStatus;
import ru.abdusamatov.librarywithsecurity.util.ParameterizedTypeReferenceUtil;
import ru.abdusamatov.librarywithsecurity.util.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class BookControllerTest extends TestControllerBase {

    private static final String BASE_URL = "books";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Override
    protected void clearDatabase() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldGetAllBooks() {
        final var bookListSize = 10;
        final var bookDtoList = TestDataProvider.createListBookDto(bookListSize);
        bookDtoList.forEach(bookDto -> bookService.createBook(bookDto));

        final var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .queryParam("page", 0)
                        .queryParam("size", 20)
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getListResponseReference(BookDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();
        TestStatus
                .assertSuccess(OK, "List of books", response);
        assertThat(response.getData())
                .isNotNull()
                .hasSize(bookListSize);
    }

    @Test
    void shouldReturnBook_whenExistingBookIdProvided() {
        final var id = bookService.createBook(TestDataProvider.createBookDto()
                        .build())
                .getId();

        final var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference(BookDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();
        TestStatus
                .assertSuccess(OK, "Book successfully found", response);
        assertThat(response.getData().getId())
                .isEqualTo(id);
    }

    @Test
    void shouldReturnNotFound_whenNonExistingBookIdProvided() {
        final var id = 1L;

        final var response = executeGetBookById(id, NOT_FOUND);

        assertBookNotFound(response);
    }

    @Test
    void shouldCreateBook_whenValidDataProvided() {
        final var validBookDto = TestDataProvider
                .createBookDto()
                .build();

        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL).build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validBookDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference(BookDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();
        TestStatus
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

        final var response = executeCreateBook(invalidBookDto, BAD_REQUEST);

        assertFieldErrorForBook(response);
    }

    @Test
    void shouldUpdateBook_whenValidBookDtoProvided() {
        final var bookToBeUpdated = bookService
                .createBook(TestDataProvider.createBookDto().build());
        final var updateBookDto = TestDataProvider
                .updateBookDto(bookToBeUpdated)
                .build();

        final var response = webTestClient.put().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL).build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateBookDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference(BookDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();
        TestStatus
                .assertSuccess(OK, "Book successfully updated", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateBookDto);
    }

    @Test
    void shouldReturnNotFound_whenBookToUpdateDoesNotExist() {
        final var notExistingId = 10000L;
        final var bookToBeUpdated = bookService
                .createBook(TestDataProvider.createBookDto().build());
        final var updateBookDto = TestDataProvider
                .updateBookDto(bookToBeUpdated)
                .id(notExistingId)
                .build();

        final var response = executeUpdateBook(updateBookDto, NOT_FOUND);

        assertBookNotFound(response);
    }


    @Test
    void shouldReturnBadRequest_whenUpdateBookWithInvalidFields() {
        final var bookToBeUpdated = bookService
                .createBook(TestDataProvider.createBookDto().build());
        final var invalidBookDto = TestDataProvider
                .updateBookDtoWithInvalidFields(bookToBeUpdated)
                .build();

        final var response = executeUpdateBook(invalidBookDto, BAD_REQUEST);

        assertFieldErrorForBook(response);
    }

    @Test
    void shouldReturnNotFound_whenBookToUpdateWithUserIdDoesNotExist() {
        final var notExistingUserId = 10000L;
        final var bookToBeUpdated = bookService
                .createBook(TestDataProvider.createBookDto().build());
        final var updateBookDto = TestDataProvider
                .updateBookDto(bookToBeUpdated)
                .userId(notExistingUserId)
                .build();

        final var response = executeUpdateBook(updateBookDto, NOT_FOUND);

        UserControllerTest.assertUserNotFound(response);
    }

    @Test
    void shouldReturnNoContent_whenBookDeletedSuccessfully() {
        final var id = bookService
                .createBook(TestDataProvider.createBookDto().build())
                .getId();

        final var response = webTestClient.delete().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();
        TestStatus
                .assertSuccess(NO_CONTENT, "Successfully deleted", response);
    }

    @Test
    void shouldReturnNotFound_whenBookToDeleteDoesNotExist() {
        final var notExistingId = 10000L;

        final var response = executeDeleteBook(notExistingId, NOT_FOUND);

        assertBookNotFound(response);
    }

    @Test
    void shouldAssignBook_whenValidDataProvided() {
        final var bookId = bookService
                .createBook(TestDataProvider.createBookDto().build())
                .getId();
        final var userDtoToBeAssigned = userService
                .createUser(TestDataProvider.createUserDto().build());

        final var response = webTestClient.patch().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(bookId), "assign")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDtoToBeAssigned)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();
        TestStatus
                .assertSuccess(NO_CONTENT, "Book successfully assigned", response);
    }

    @Test
    void shouldReturnBadRequest_whenAssignUserWithInvalidFields() {
        final var bookId = bookService
                .createBook(TestDataProvider.createBookDto().build())
                .getId();
        final var userDtoToBeAssigned = TestDataProvider
                .createUserDtoWithInvalidFields()
                .build();

        final var response = executeAssignBook(bookId, userDtoToBeAssigned, BAD_REQUEST);

        UserControllerTest.assertFieldErrorForUser(response);
    }

    @Test
    void shouldReturnNotFound_whenBookToAssignDoesNotExist() {
        final var userDtoToBeAssigned = userService
                .createUser(TestDataProvider.createUserDto().build());
        final var notExistingBookId = 1000L;

        final var response = executeAssignBook(notExistingBookId, userDtoToBeAssigned, NOT_FOUND);

        assertBookNotFound(response);
    }

    @Test
    void shouldReleaseBook_whenValidDataProvided() {
        final var bookId = bookService
                .createBook(TestDataProvider.createBookDto().build())
                .getId();

        final var response = webTestClient.patch().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(bookId), "release")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();
        TestStatus
                .assertSuccess(NO_CONTENT, "Book successfully released", response);
    }

    @Test
    void shouldReturnNotFound_whenBookToReleaseDoesNotExist() {
        final var notExistingBookId = 10000L;

        final var response = executeReleaseBook(notExistingBookId, NOT_FOUND);

        assertBookNotFound(response);
    }

    @Test
    void shouldReturnBooks_whenValidQueryProvided() {
        final var bookListSize = 10;
        final var bookDtoList = TestDataProvider.createListBookDto(bookListSize);
        bookDtoList.forEach(bookDto -> bookService.createBook(bookDto));

        final var query = bookDtoList
                .getFirst()
                .getTitle();

        final var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "search")
                        .queryParam("query", query)
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getListResponseReference(BookDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();
        TestStatus
                .assertSuccess(OK, "Found books with title " + query, response);
        assertThat(response.getData())
                .hasSize(1);
    }

    @Test
    void shouldReturnEmptyBookList_whenQueryDoNotSatisfiesAnyBook() {
        final var query = "Not existing title";

        final var response = executeSearchByTitle(query, OK);

        TestStatus
                .assertSuccess(OK, "Found books with title " + query, response);
        assertThat(response.getData())
                .isNull();
    }

    public Response<Void> executeGetBookById(
            final Long id,
            final HttpStatus status
    ) {
        final var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    public Response<Void> executeCreateBook(
            final BookDto bookDto,
            HttpStatus status
    ) {
        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL).build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookDto)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    public Response<Void> executeUpdateBook(
            final BookDto bookDto,
            final HttpStatus status
    ) {
        final var response = webTestClient.put().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL).build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookDto)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    public Response<Void> executeDeleteBook(
            final Long id,
            final HttpStatus status) {
        final var response = webTestClient.delete().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    public Response<Void> executeAssignBook(
            final Long bookId,
            final UserDto userDto,
            final HttpStatus status) {
        final var response = webTestClient.patch().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(bookId), "assign")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    public Response<Void> executeReleaseBook(
            final Long id,
            final HttpStatus status) {
        final var response = webTestClient.patch().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id), "release")
                        .build())
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    public Response<Void> executeSearchByTitle(
            final String query,
            final HttpStatus status) {
        final var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "search")
                        .queryParam("query", query)
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private static void assertBookNotFound(final Response<Void> response) {
        TestStatus.assertError(NOT_FOUND, "Failed entity search", response);
    }

    private static void assertFieldErrorForBook(final Response<Void> response) {
        TestStatus.assertError(BAD_REQUEST, "Validation field failed", response);
    }
}
