package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.errors.ErrorResponse;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;
import ru.abdusamatov.librarywithsecurity.services.BookService;
import ru.abdusamatov.librarywithsecurity.services.UserService;
import ru.abdusamatov.librarywithsecurity.support.ParameterizedTypeReferenceUtil;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.support.TestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class BookControllerTest extends TestBase {

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
        int bookListSize = 10;
        var bookDtoList = TestDataProvider.createListBookDto(bookListSize);
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

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(OK, "List of books", response);
        assertThat(response.getData())
                .isNotNull()
                .hasSize(bookListSize);
    }

    @Test
    void shouldReturnBook_whenExistingBookIdProvided() {
        long id = bookService.createBook(TestDataProvider.createBookDto()).getId();

        final var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference(BookDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(OK, "Book successfully found", response);
        assertThat(response.getData().getId()).isEqualTo(id);
    }

    @Test
    void shouldReturnNotFound_whenNonExistingBookIdProvided() {
        long id = 1L;

        final var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertNotFoundBook(id, response);
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldCreateBook_whenValidDataProvided() {
        var validBookDto = TestDataProvider.createBookDto();

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

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(CREATED, "Book successfully created", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("takenAt", "owner", "id")
                .isEqualTo(validBookDto);
        assertThat(response.getData().getId()).isNotNull();
        assertThat(response.getData())
                .extracting("takenAt", "userId")
                .containsOnlyNulls();
    }

    @Test
    void shouldReturnBadRequest_whenBookWithInvalidFields() {
        BookDto invalidBookDto = TestDataProvider.createBookDtoWithInvalidFields();


        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidBookDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertFieldErrorForBook(response);
    }

    @Test
    void shouldUpdateBook_whenValidBookDtoProvided() {
        BookDto bookToBeUpdated = bookService.createBook(TestDataProvider.createBookDto());
        BookDto updateBookDto = TestDataProvider.updateBookDto(bookToBeUpdated);

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

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(OK, "Book successfully updated", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateBookDto);
    }

    @Test
    void shouldReturnNotFound_whenBookToUpdateDoesNotExist() {
        long notExistingId = 10000L;

        BookDto bookToBeUpdated = bookService.createBook(TestDataProvider.createBookDto());
        BookDto updateBookDto = TestDataProvider.updateBookDto(bookToBeUpdated);
        updateBookDto.setId(notExistingId);

        final var response = webTestClient.put().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateBookDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertNotFoundBook(notExistingId, response);
    }


    @Test
    void shouldReturnBadRequest_whenUpdateBookWithInvalidFields() {
        BookDto bookToBeUpdated = bookService.createBook(TestDataProvider.createBookDto());
        BookDto invalidBookDto = TestDataProvider.updateBookDtoWithInvalidFields(bookToBeUpdated);

        final var response = webTestClient.put().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidBookDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertFieldErrorForBook(response);
    }

    @Test
    void shouldReturnNotFound_whenBookToUpdateWithUserIdDoesNotExist() {
        long notExistingUserId = 10000L;

        BookDto bookToBeUpdated = bookService.createBook(TestDataProvider.createBookDto());
        BookDto updateBookDto = TestDataProvider.updateBookDto(bookToBeUpdated);
        updateBookDto.setUserId(notExistingUserId);

        final var response = webTestClient.put().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateBookDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertNotFoundUser(notExistingUserId, response);
    }

    @Test
    void shouldReturnNoContent_whenBookDeletedSuccessfully() {
        long id = bookService.createBook(TestDataProvider.createBookDto()).getId();

        final var response = webTestClient.delete().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(NO_CONTENT, "Successfully deleted", response);
    }

    @Test
    void shouldReturnNotFound_whenBookToDeleteDoesNotExist() {
        long notExistingId = 10000L;

        final var response = webTestClient.delete().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(notExistingId))
                        .build()
                )
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertNotFoundBook(notExistingId, response);
    }

    @Test
    void shouldAssignBook_whenValidDataProvided() {
        long bookId = bookService.createBook(TestDataProvider.createBookDto()).getId();
        UserDto userDtoToBeAssigned = userService.createUser(TestDataProvider.createUserDto());

        var response = webTestClient.patch().uri(uriBuilder -> uriBuilder
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

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(NO_CONTENT, "Book successfully assigned", response);
    }

    @Test
    void shouldReturnBadRequest_whenAssignUserWithInvalidFields() {
        long bookId = bookService.createBook(TestDataProvider.createBookDto()).getId();
        UserDto userDtoToBeAssigned = TestDataProvider.createUserDtoWithInvalidFields();

        final var response = webTestClient.patch().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(bookId), "assign")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDtoToBeAssigned)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertFieldErrorForUser(response);
    }

    @Test
    void shouldReturnNotFound_whenBookToAssignDoesNotExist() {
        UserDto userDtoToBeAssigned = userService.createUser(TestDataProvider.createUserDto());
        long notExistingBookId = 1000L;

        final var response = webTestClient.patch().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(notExistingBookId), "assign")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDtoToBeAssigned)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertNotFoundBook(notExistingBookId, response);
    }

    @Test
    void shouldReleaseBook_whenValidDataProvided() {
        long bookId = bookService.createBook(TestDataProvider.createBookDto()).getId();

        final var response = webTestClient.patch().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(bookId), "release")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(NO_CONTENT, "Book successfully released", response);
    }

    @Test
    void shouldReturnNotFound_whenBookToReleaseDoesNotExist() {
        long notExistingBookId = 10000L;

        final var response = webTestClient.patch().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(notExistingBookId), "release")
                        .build()
                )
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertNotFoundBook(notExistingBookId, response);
    }

    @Test
    void shouldReturnBooks_whenValidQueryProvided() {
        int bookListSize = 10;

        List<BookDto> bookDtoList = TestDataProvider.createListBookDto(bookListSize);
        bookDtoList.forEach(bookDto -> bookService.createBook(bookDto));
        String query = bookDtoList.getFirst().getTitle();

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

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(OK, "Found books with title " + query, response);
        assertThat(response.getData()).hasSize(1);
    }

    @Test
    void shouldReturnEmptyBookList_whenQueryDoNotSatisfiesAnyBook() {
        String query = "Not existing title";

        final var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "search")
                        .queryParam("query", query)
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(OK, "Found books with title " + query, response);
        assertThat(response.getData()).isNull();
    }
}
