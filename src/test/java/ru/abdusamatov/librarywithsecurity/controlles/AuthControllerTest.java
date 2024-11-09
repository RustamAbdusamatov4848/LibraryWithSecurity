package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.AuthenticationDto;
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
        LibrarianDto librarianToBeSaved = TestDataProvider.createLibrarianDto();

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
        LibrarianDto invalidLibrarianDto = TestDataProvider.createLibrarianDtoWithInvalidFields();

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
        LibrarianDto librarianDto = service.createLibrarian(TestDataProvider.createLibrarianDto());
        String emailThatAlreadyExist = librarianDto.getEmail();

        LibrarianDto librarianDtoWithExistEmail = TestDataProvider.createLibrarianDto();
        librarianDtoWithExistEmail.setEmail(emailThatAlreadyExist);

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
        LibrarianDto librarianDto = TestDataProvider.createLibrarianDto();
        String email = librarianDto.getEmail();
        String password = librarianDto.getPassword();
        service.createLibrarian(librarianDto);

        AuthenticationDto authenticationDto = TestDataProvider.createAuthenticationDto(email, password);

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
        AuthenticationDto authenticationDto = TestDataProvider.createAuthenticationDto();

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
