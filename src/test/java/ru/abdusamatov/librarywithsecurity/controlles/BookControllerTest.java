package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.abdusamatov.librarywithsecurity.dto.BookDto;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;
import ru.abdusamatov.librarywithsecurity.services.BookService;
import ru.abdusamatov.librarywithsecurity.services.UserService;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.util.Response;
import ru.abdusamatov.librarywithsecurity.util.ResponseStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        List<BookDto> bookDtoList = TestDataProvider.createSampleListBookDto(bookListSize);
        bookDtoList.forEach(bookDto -> bookService.createBook(bookDto));

        webTestClient.get().uri("/books?page=0&size=10").exchange().expectStatus().isOk().expectBody(Response.class).value(response -> {
            assertThat(response.getResult().getStatus()).isEqualTo(ResponseStatus.SUCCESS);
            assertThat(response.getResult().getHttpStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getResult().getDescription()).isEqualTo("List of books");
            assertThat(response.getData()).isNotNull();
        });
    }

    @Test
    void shouldReturnBook_whenExistingBookIdProvided() {
        long id = 1L;
        bookService.createBook(TestDataProvider.createSampleBookDto());

        webTestClient.get().uri("/books/" + id).exchange().expectStatus().isOk().expectBody(Response.class).value(response -> {
            assertThat(response.getResult().getStatus()).isEqualTo(ResponseStatus.SUCCESS);
            assertThat(response.getResult().getHttpStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getResult().getDescription()).isEqualTo("Book successfully found");
            assertThat(response.getData()).isNotNull();
        });
    }

    @Test
    void shouldReturnNotFound_whenNonExistingBookIdProvided() {
        long id = 1L;

        webTestClient.get().uri("/books/" + id).exchange().expectStatus().isNotFound();
    }
}
