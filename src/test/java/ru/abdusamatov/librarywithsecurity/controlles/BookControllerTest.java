package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;
import ru.abdusamatov.librarywithsecurity.services.BookService;
import ru.abdusamatov.librarywithsecurity.services.UserService;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.List;

public class BookControllerTest extends TestBase {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Test
    void shouldGetAllBooks() {
        int bookListSize = 10;
        List<BookDto> bookDtoList = TestDataProvider.createListBookDto(bookListSize);
        bookDtoList.forEach(bookDto -> bookService.createBook(bookDto));

        webTestClient.get().uri(uriBuilder ->
                        uriBuilder
                                .path("/books")
                                .queryParam("page", 0)
                                .queryParam("size", 20)
                                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("OK")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("List of books")
                .jsonPath("$.data.length()").isEqualTo(bookListSize);
    }

    @Test
    void shouldReturnBook_whenExistingBookIdProvided() {
        long id = bookService.createBook(TestDataProvider.createBookDto()).getId();

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
    void shouldCreateBook_whenValidDataProvided() {
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
    void shouldAssignBook_whenValidDataProvided() {
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
                .jsonPath("$.result.description").isEqualTo("Book successfully assigned");
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

        webTestClient.patch().uri("/books/" + bookId + "/release")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("NO_CONTENT")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("Book successfully released");
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

        webTestClient.get().uri("/books/search?query=" + title)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("OK")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("Found books with title " + title)
                .jsonPath("$.data.length()").isEqualTo(1);
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

    @Override
    protected void clearDatabase() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }
}
