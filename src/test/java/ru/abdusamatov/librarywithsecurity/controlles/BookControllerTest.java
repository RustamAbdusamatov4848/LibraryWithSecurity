package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import ru.abdusamatov.librarywithsecurity.util.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static ru.abdusamatov.librarywithsecurity.util.ResponseStatus.SUCCESS;

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

        var response = getResponseGetBookList();

        assertSuccess(OK, "List of books", response);
        assertThat(response.getData())
                .isNotNull()
                .hasSize(bookListSize);
    }

    @Test
    void shouldReturnBook_whenExistingBookIdProvided() {
        long id = bookService.createBook(TestDataProvider.createBookDto()).getId();

        var response = getResponseShowBookById(id);

        assertSuccess(OK, "Book successfully found", response);
        assertThat(response.getData().getId()).isEqualTo(id);
    }

    @Test
    void shouldReturnNotFound_whenNonExistingBookIdProvided() {
        long id = 1L;

        var response = webTestClient.get().uri("/books/" + id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
        assertThat(response.getMessage()).isEqualTo("Failed entity search");
        assertThat(response.getErrors()).containsEntry("cause", "Book with ID: " + id + ", not found");
    }

    @Test
    void shouldCreateBook_whenValidDataProvided() {
        var validBookDto = TestDataProvider.createBookDto();

        var response = getResponseCreateBook(validBookDto);

        assertSuccess(CREATED, "Book successfully created", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("takenAt", "owner", "id")
                .isEqualTo(validBookDto);
        assertThat(response.getData())
                .extracting("takenAt", "userId")
                .containsOnlyNulls();
    }

    @Test
    void shouldReturnBadRequest_whenBookWithInvalidFields() {
        BookDto invalidBookDto = TestDataProvider.createBookDtoWithInvalidFields();

        webTestClient.post().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidBookDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation field failed")
                .jsonPath("$.errors.authorName").isEqualTo("Author name must be between 2 and 30 characters long")
                .jsonPath("$.errors.authorSurname").isEqualTo("Author surname must be between 2 and 30 characters long")
                .jsonPath("$.errors.yearOfPublication").isEqualTo("Year must be greater than 1500");
    }

    @Test
    void shouldUpdateBook_whenValidBookDtoProvided() {
        BookDto bookToBeUpdated = bookService.createBook(TestDataProvider.createBookDto());
        BookDto updateBookDto = TestDataProvider.updateBookDto(bookToBeUpdated);

        var response = getResponseUpdateBook(updateBookDto);

        assertSuccess(OK, "Book successfully updated", response);
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

        webTestClient.put().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateBookDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed entity search")
                .jsonPath("$.errors.cause").isEqualTo("Book with ID: " + notExistingId + ", not found");
    }

    @Test
    void shouldReturnBadRequest_whenUpdateBookWithInvalidFields() {
        BookDto bookToBeUpdated = bookService.createBook(TestDataProvider.createBookDto());
        BookDto invalidBookDto = TestDataProvider.updateBookDtoWithInvalidFields(bookToBeUpdated);

        webTestClient.put().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidBookDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation field failed")
                .jsonPath("$.errors.authorName").isEqualTo("Author name must be between 2 and 30 characters long")
                .jsonPath("$.errors.authorSurname").isEqualTo("Author surname must be between 2 and 30 characters long")
                .jsonPath("$.errors.yearOfPublication").isEqualTo("Year must be greater than 1500");
    }

    @Test
    void shouldReturnNotFound_whenBookToUpdateWithUserIdDoesNotExist() {
        long notExistingUserId = 10000L;

        BookDto bookToBeUpdated = bookService.createBook(TestDataProvider.createBookDto());
        BookDto updateBookDto = TestDataProvider.updateBookDto(bookToBeUpdated);
        updateBookDto.setUserId(notExistingUserId);

        webTestClient.put().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateBookDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed entity search")
                .jsonPath("$.errors.cause").isEqualTo("User with ID: " + notExistingUserId + ", not found");
    }

    @Test
    void shouldReturnNoContent_whenBookDeletedSuccessfully() {
        long id = bookService.createBook(TestDataProvider.createBookDto()).getId();

        var response = getResponseDeleteBook(id);

        assertSuccess(NO_CONTENT, "Successfully deleted", response);
    }

    @Test
    void shouldReturnNotFound_whenBookToDeleteDoesNotExist() {
        long notExistingId = 10000L;

        webTestClient.delete().uri("/books/" + notExistingId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed entity search")
                .jsonPath("$.errors.cause").isEqualTo("Book with ID: " + notExistingId + ", not found");

    }

    @Test
    void shouldAssignBook_whenValidDataProvided() {
        long bookId = bookService.createBook(TestDataProvider.createBookDto()).getId();
        UserDto userDtoToBeAssigned = userService.createUser(TestDataProvider.createUserDto());

        var response = getResponseAssignBook(bookId, userDtoToBeAssigned);

        assertSuccess(NO_CONTENT, "Book successfully assigned", response);
    }

    @Test
    void shouldReturnBadRequest_whenAssignUserWithInvalidFields() {
        long bookId = bookService.createBook(TestDataProvider.createBookDto()).getId();
        UserDto userDtoToBeAssigned = TestDataProvider.createUserDtoWithInvalidFields();

        webTestClient.patch().uri("/books/" + bookId + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDtoToBeAssigned)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation field failed")
                .jsonPath("$.errors.fullName").isEqualTo("Name should be between 2 to 30 characters long")
                .jsonPath("$.errors.email").isEqualTo("Invalid email address")
                .jsonPath("$.errors.dateOfBirth").isEqualTo("Date of birth must be in the past");
    }

    @Test
    void shouldReturnNotFound_whenBookToAssignDoesNotExist() {
        UserDto userDtoToBeAssigned = userService.createUser(TestDataProvider.createUserDto());
        long notExistingBookId = 1000L;

        webTestClient.patch().uri("/books/" + notExistingBookId + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDtoToBeAssigned)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed entity search")
                .jsonPath("$.errors.cause").isEqualTo("Book with ID: " + notExistingBookId + ", not found");

    }

    @Test
    void shouldReleaseBook_whenValidDataProvided() {
        long bookId = bookService.createBook(TestDataProvider.createBookDto()).getId();

        var response = getResponseReleaseBook(bookId);

        assertSuccess(NO_CONTENT, "Book successfully released", response);
    }

    @Test
    void shouldReturnNotFound_whenBookToReleaseDoesNotExist() {
        long notExistingBookId = 10000L;

        webTestClient.patch().uri("/books/" + notExistingBookId + "/release")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed entity search")
                .jsonPath("$.errors.cause").isEqualTo("Book with ID: " + notExistingBookId + ", not found");
    }

    @Test
    void shouldReturnBooks_whenValidQueryProvided() {
        int bookListSize = 10;

        List<BookDto> bookDtoList = TestDataProvider.createListBookDto(bookListSize);
        bookDtoList.forEach(bookDto -> bookService.createBook(bookDto));
        String title = bookDtoList.getFirst().getTitle();

        var response = getResponseSearchBooks(title);

        assertSuccess(OK, "Found books with title " + title, response);
        assertThat(response.getData()).hasSize(1);
    }

    @Test
    void shouldReturnEmptyBookList_whenQueryDoNotSatisfiesAnyBook() {
        String title = "Not existing title";

        webTestClient.get().uri("/books/search?query=" + title)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("OK")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("Found books with title " + title)
                .jsonPath("$.data.length()").isEqualTo(0);
    }

    private <T> void assertSuccess(HttpStatus httpStatusCode, String description, Response<T> response) {
        assertThat(response.getResult())
                .extracting("httpStatusCode", "status", "description")
                .containsExactly(httpStatusCode, SUCCESS, description);
    }

    private Response<List<BookDto>> getResponseGetBookList() {
        var response = webTestClient.get().uri(uriBuilder -> uriBuilder
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
        return response;
    }

    private Response<BookDto> getResponseShowBookById(long id) {
        var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference(BookDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        return response;
    }

    private Response<BookDto> getResponseCreateBook(BookDto bookDto) {
        var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL).build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference(BookDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        return response;
    }

    private Response<BookDto> getResponseUpdateBook(BookDto updateBookDto) {
        var response = webTestClient.put().uri(uriBuilder -> uriBuilder
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
        return response;
    }

    private Response<Void> getResponseDeleteBook(long id) {
        return webTestClient.delete().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();
    }

    private Response<Void> getResponseAssignBook(long id, UserDto userDtoToBeAssigned) {
        return webTestClient.patch().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id), "assign")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDtoToBeAssigned)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();
    }

    private Response<Void> getResponseReleaseBook(long id) {
        return webTestClient.patch().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id), "release")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();
    }

    private Response<List<BookDto>> getResponseSearchBooks(String query) {
        var response = webTestClient.get().uri(uriBuilder -> uriBuilder
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
        return response;
    }
}
