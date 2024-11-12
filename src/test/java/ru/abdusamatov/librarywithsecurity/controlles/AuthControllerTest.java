package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.errors.ErrorResponse;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;
import ru.abdusamatov.librarywithsecurity.services.LibrarianService;
import ru.abdusamatov.librarywithsecurity.support.ParameterizedTypeReferenceUtil;
import ru.abdusamatov.librarywithsecurity.support.TestControllerBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.support.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;

public class AuthControllerTest extends TestControllerBase {

    public static final String BASE_URL = "lib";

    @Autowired
    private LibrarianRepository repository;

    @Autowired
    private LibrarianService service;

    @Override
    protected void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    public void shouldCreateLibrarian_whenValidDataProvided() {
        final var librarianToBeSaved = TestDataProvider.createLibrarianDto().build();

        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "registration")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(librarianToBeSaved)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference(LibrarianDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(HttpStatus.CREATED, "Librarian was created", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("password", "id")
                .isEqualTo(librarianToBeSaved);
    }

    @Test
    void shouldReturnBadRequest_whenBookDataProvidedWithInvalidFields() {
        final var invalidLibrarianDto = TestDataProvider.createLibrarianDtoWithInvalidFields().build();

        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "registration")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidLibrarianDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertFieldErrorForLibrarian(response);
    }

    @Test
    void shouldReturnBadRequest_whenLibrarianWithEmailIsAlreadyExist() {
        final var librarianDto = service.createLibrarian(TestDataProvider.createLibrarianDto().build());
        final var emailThatAlreadyExist = librarianDto.getEmail();

        var librarianDtoWithExistEmail = TestDataProvider.createLibrarianDto().email(emailThatAlreadyExist).build();

        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "registration")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(librarianDtoWithExistEmail)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(response.getMessage()).isEqualTo("Failed email validation, already exist");
        assertThat(response.getErrors().get("cause"))
                .isEqualTo(String.format("%s email is already exist, try another one", emailThatAlreadyExist));
    }

    @Test
    void shouldReturnNoContent_whenAuthenticationWithValidFields() {
        final var librarianDto = TestDataProvider.createLibrarianDto().build();
        final var email = librarianDto.getEmail();
        final var password = librarianDto.getPassword();
        service.createLibrarian(librarianDto);

        final var authenticationDto = TestDataProvider.createAuthenticationDto(email, password).build();

        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "login")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        TestUtils.assertSuccess(NO_CONTENT, "Successful validation", response);
    }

    @Test
    void shouldReturnUnauthorized_whenAuthenticationWithInvalidFields() {
        final var authenticationDto = TestDataProvider.createAuthenticationDto().build();

        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "login")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDto)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Failed authorization");
    }
}
