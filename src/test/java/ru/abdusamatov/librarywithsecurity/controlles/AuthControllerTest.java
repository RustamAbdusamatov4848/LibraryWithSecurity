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
import ru.abdusamatov.librarywithsecurity.dto.AuthenticationDto;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;
import ru.abdusamatov.librarywithsecurity.services.LibrarianService;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AuthControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pSqlContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private LibrarianRepository repository;

    @Autowired
    private LibrarianService service;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void shouldCreateLibrarian_whenValidDataProvided() {
        LibrarianDto librarianToBeSaved = TestDataProvider.createLibrarianDto();

        webTestClient.post().uri("/lib/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(librarianToBeSaved)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("CREATED")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("Librarian was created")
                .jsonPath("$.data.fullName").isEqualTo(librarianToBeSaved.getFullName())
                .jsonPath("$.data.email").isEqualTo(librarianToBeSaved.getEmail());
    }

    @Test
    void shouldReturnBadRequest_whenBookDataProvidedWithInvalidFields() {
        LibrarianDto invalidLibrarianDto = TestDataProvider.createLibrarianDtoWithInvalidFields();

        webTestClient.post().uri("/lib/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidLibrarianDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation field failed")
                .jsonPath("$.errors.fullName").isEqualTo("Name should be between 2 to 30 characters long")
                .jsonPath("$.errors.email").isEqualTo("Invalid email address")
                .jsonPath("$.errors.password").isEqualTo("Password should be equals or less than 100 characters long");
    }

    @Test
    void shouldReturnBadRequest_whenLibrarianWithEmailIsAlreadyExist() {
        LibrarianDto librarianDto = service.createLibrarian(TestDataProvider.createLibrarianDto());
        String emailThatAlreadyExist = librarianDto.getEmail();

        LibrarianDto librarianDtoWithExistEmail = TestDataProvider.createLibrarianDto();
        librarianDtoWithExistEmail.setEmail(emailThatAlreadyExist);

        webTestClient.post().uri("/lib/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(librarianDtoWithExistEmail)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed email validation, already exist")
                .jsonPath("$.errors.cause").isEqualTo(String.format("%s email is already exist, try another one", emailThatAlreadyExist));
    }

    @Test
    void shouldReturnNoContent_whenAuthenticationWithValidFields() {
        LibrarianDto librarianDto = TestDataProvider.createLibrarianDto();
        String email = librarianDto.getEmail();
        String password = librarianDto.getPassword();
        service.createLibrarian(librarianDto);

        AuthenticationDto authenticationDto = TestDataProvider.createAuthenticationDto(email, password);

        webTestClient.post().uri("/lib/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("NO_CONTENT")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("Successful validation");
    }

    @Test
    void shouldReturnUnauthorized_whenAuthenticationWithInvalidFields() {
        AuthenticationDto authenticationDto = TestDataProvider.createAuthenticationDto();

        webTestClient.post().uri("/lib/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDto)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed authorization");
    }
}
