package ru.abdusamatov.librarywithsecurity.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.AuthenticationDto;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.repository.LibrarianRepository;
import ru.abdusamatov.librarywithsecurity.service.LibrarianService;
import ru.abdusamatov.librarywithsecurity.support.TestControllerBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.support.TestStatus;
import ru.abdusamatov.librarywithsecurity.util.ParameterizedTypeReferenceUtil;
import ru.abdusamatov.librarywithsecurity.util.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
        final var librarianToBeSaved = TestDataProvider
                .createLibrarianDto()
                .build();

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

        assertThat(response)
                .isNotNull();
        TestStatus
                .assertSuccess(HttpStatus.CREATED, "Librarian was created", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("password", "id")
                .isEqualTo(librarianToBeSaved);
    }

    @Test
    void shouldReturnBadRequest_whenBookDataProvidedWithInvalidFields() {
        final var invalidLibrarianDto = TestDataProvider
                .createLibrarianDtoWithInvalidFields()
                .build();

        final var response = executeCreateLibrarian(invalidLibrarianDto, BAD_REQUEST);

        TestStatus
                .assertError(BAD_REQUEST, "Validation field failed", response);
    }

    @Test
    void shouldReturnBadRequest_whenLibrarianWithEmailIsAlreadyExist() {
        final var emailThatAlreadyExist = service
                .createLibrarian(TestDataProvider.createLibrarianDto().build()).getEmail();

        var librarianDtoWithExistEmail = TestDataProvider
                .createLibrarianDto().email(emailThatAlreadyExist)
                .build();

        final var response = executeCreateLibrarian(librarianDtoWithExistEmail, BAD_REQUEST);

        TestStatus
                .assertError(BAD_REQUEST, "Failed email validation, already exist", response);
    }

    @Test
    void shouldReturnNoContent_whenAuthenticationWithValidFields() {
        final var librarianDto = TestDataProvider
                .createLibrarianDto()
                .build();

        service.createLibrarian(librarianDto);
        final var authenticationDto = TestDataProvider
                .createAuthenticationDto(librarianDto.getEmail(), librarianDto.getPassword())
                .build();

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

        assertThat(response)
                .isNotNull();

        TestStatus
                .assertSuccess(NO_CONTENT, "Successful validation", response);
    }

    @Test
    void shouldReturnUnauthorized_whenAuthenticationWithInvalidFields() {
        final var authenticationDto = TestDataProvider
                .createAuthenticationDto()
                .build();

        final var response = executeValidateLibrarian(authenticationDto, UNAUTHORIZED);

        TestStatus.assertError(UNAUTHORIZED, "Failed authorization", response);
    }

    private Response<Void> executeCreateLibrarian(
            final LibrarianDto librarianToBeSaved,
            final HttpStatus status
    ) {
        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "registration")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(librarianToBeSaved)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private Response<Void> executeValidateLibrarian(
            final AuthenticationDto authenticationDto,
            final HttpStatus httpStatus
    ) {
        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, "login")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDto)
                .exchange()
                .expectStatus().isEqualTo(httpStatus)
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }
}
