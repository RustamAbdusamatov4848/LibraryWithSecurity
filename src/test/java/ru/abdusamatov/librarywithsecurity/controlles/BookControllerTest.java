package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;
import ru.abdusamatov.librarywithsecurity.services.BookService;
import ru.abdusamatov.librarywithsecurity.services.UserService;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class BookControllerTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebTestClient webTestClient;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pSqlContainer = new PostgreSQLContainer<>("postgres:latest");

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldGetBookList() {
        int bookListSize = 10;
        List<BookDto> bookDtoList = TestDataProvider.createListBookDto(bookListSize);
        bookDtoList.forEach(bookDto -> bookService.createBook(bookDto));

        webTestClient.get().uri("/books?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("OK")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("List of books");
    }

    @Test
    void shouldReturnBook_whenExistingBookIdProvided() {
        long id = 1L;
        bookService.createBook(TestDataProvider.createBookDto());

        webTestClient.get().uri("/books/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(id)
                .jsonPath("$.result.httpStatusCode").isEqualTo("OK")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("Book successfully found");
    }

    @Test
    void shouldReturnNotFound_whenNonExistingBookIdProvided() {
        long id = 1L;

        webTestClient.get().uri("/books/" + id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed entity search")
                .jsonPath("$.errors.cause").isEqualTo("Book with ID: " + id + ", not found");
    }

    @Test
    void shouldCreateBook_whenValidBookDtoProvided() {
        BookDto validBookDto = TestDataProvider.createBookDto();

        webTestClient.post().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validBookDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("CREATED")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("Book successfully created")
                .jsonPath("$.data.title").isEqualTo(validBookDto.getTitle())
                .jsonPath("$.data.authorName").isEqualTo(validBookDto.getAuthorName())
                .jsonPath("$.data.authorSurname").isEqualTo(validBookDto.getAuthorSurname())
                .jsonPath("$.data.yearOfPublication").isEqualTo(validBookDto.getYearOfPublication());
    }

    @Test
    void shouldReturnBadRequest_whenInvalidBookDataProvided() {
        BookDto invalidBookDto = TestDataProvider.createInvalidBookDto();

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
        BookDto updateBookDto = TestDataProvider.updatedBookDto(bookToBeUpdated);

        webTestClient.put().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateBookDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("OK")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("Book successfully updated")
                .jsonPath("$.data.id").isEqualTo(updateBookDto.getId())
                .jsonPath("$.data.title").isEqualTo(updateBookDto.getTitle())
                .jsonPath("$.data.authorName").isEqualTo(updateBookDto.getAuthorName())
                .jsonPath("$.data.authorSurname").isEqualTo(updateBookDto.getAuthorSurname())
                .jsonPath("$.data.yearOfPublication").isEqualTo(updateBookDto.getYearOfPublication())
                .jsonPath("$.data.takenAt").isEqualTo(updateBookDto.getTakenAt())
                .jsonPath("$.data.userId").isEqualTo(updateBookDto.getUserId());
    }

    @Test
    void shouldReturnNotFound_whenBookToUpdateDoesNotExist() {
        long notExistingId = 10000L;

        BookDto bookToBeUpdated = bookService.createBook(TestDataProvider.createBookDto());
        BookDto updateBookDto = TestDataProvider.updatedBookDto(bookToBeUpdated);
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
    void shouldReturnBadRequest_whenUpdateInvalidBookDataProvided() {
        BookDto bookToBeUpdated = bookService.createBook(TestDataProvider.createBookDto());
        BookDto invalidBookDto = TestDataProvider.updatedBookDtoWithInvalidField(bookToBeUpdated);

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
    void shouldReturnNoContent_whenBookDeletedSuccessfully() {
        BookDto bookDtoToBeDeleted = bookService.createBook(TestDataProvider.createBookDto());
        long id = bookDtoToBeDeleted.getId();

        webTestClient.delete().uri("/books/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("NO_CONTENT")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("Successfully deleted");

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
    void shouldAssignBook_whenValidBookIdAndUserProvided() {
        long bookId = bookService.createBook(TestDataProvider.createBookDto()).getId();
        UserDto userDtoToBeAssigned = userService.createUser(TestDataProvider.createUserDto());

        webTestClient.patch().uri("/books/" + bookId + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDtoToBeAssigned)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("NO_CONTENT")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("Successfully assigned");
    }

    @Test
    void shouldReturnNotFound_whenBookToAssignDoesNotExist() {

    }

    @Test
    void shouldReleaseBook_whenValidBookIdProvided() {

    }

    void shouldReturnNotFound_whenBookToReleaseDoesNotExist() {

    }

    void shouldReturnBooks_whenValidQueryProvided() {

    }

}
