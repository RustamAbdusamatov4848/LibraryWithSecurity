package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.errors.ErrorResponse;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;
import ru.abdusamatov.librarywithsecurity.services.UserService;
import ru.abdusamatov.librarywithsecurity.support.ParameterizedTypeReferenceUtil;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.util.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static ru.abdusamatov.librarywithsecurity.util.ResponseStatus.SUCCESS;

public class UserControllerTest extends TestBase {

    public static final String BASE_URL = "users";

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserService service;

    @Override
    protected void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldGetAllUsers() {
        int userListSize = 10;
        List<UserDto> userDtoList = TestDataProvider.createListUserDto(userListSize);
        userDtoList.forEach(userDto -> service.createUser(userDto));

        final var response = webTestClient.get().uri(uriBuilder ->
                        uriBuilder
                                .pathSegment(BASE_URL)
                                .queryParam("page", 0)
                                .queryParam("size", 20)
                                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getListResponseReference(UserDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertSuccess(OK, "List of users", response);
        assertThat(response.getData())
                .asList()
                .isNotNull()
                .isNotEmpty()
                .hasSize(userListSize);
    }

    @Test
    void shouldReturnUser_whenExistingUserIdProvided() {
        long id = service.createUser(TestDataProvider.createUserDto()).getId();

        final var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference(UserDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertSuccess(OK, "User successfully found", response);
        assertThat(response.getData().getId()).isEqualTo(id);
    }

    @Test
    void shouldReturnNotFound_whenNonExistingUserIdProvided() {
        long id = 10000L;

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
        assertNotFound(id, response);
    }

    @Test
    void shouldCreateUser_whenValidDataProvided() {
        UserDto validUserDto = TestDataProvider.createUserDto();

        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUserDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference(UserDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertSuccess(CREATED, "User successfully saved", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "books")
                .isEqualTo(validUserDto);
        assertThat(response.getData().getId()).isNotNull();
        assertThat(response.getData().getBooks()).isNull();
    }

    @Test
    void shouldReturnBadRequest_whenUserWithInvalidFields() {
        UserDto invalidUserDto = TestDataProvider.createUserDtoWithInvalidFields();

        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUserDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertFieldError(response);
    }

    @Test
    void shouldUpdateUser_whenValidUserDtoProvided() {
        UserDto userToBeUpdated = service.createUser(TestDataProvider.createUserDto());
        UserDto updateUserDto = TestDataProvider.updateUserDto(userToBeUpdated);


        final var response = webTestClient.put().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateUserDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParameterizedTypeReferenceUtil.getResponseReference(UserDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertSuccess(OK, "User successfully updated", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateUserDto);
    }

    @Test
    void shouldReturnNotFound_whenUserToUpdateDoesNotExist() {
        long notExistingId = 10000L;

        UserDto userToBeUpdated = service.createUser(TestDataProvider.createUserDto());
        UserDto updateUserDto = TestDataProvider.updateUserDto(userToBeUpdated);
        updateUserDto.setId(notExistingId);

        final var response = webTestClient.put().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateUserDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertNotFound(notExistingId, response);
    }

    @Test
    void shouldReturnBadRequest_whenUpdateUserWithInvalidFields() {
        UserDto userToBeUpdated = service.createUser(TestDataProvider.createUserDto());
        UserDto updateUserDto = TestDataProvider.updateUserDtoWithInvalidFields(userToBeUpdated);

        final var response = webTestClient.put().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateUserDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertFieldError(response);
    }

    @Test
    void shouldReturnNoContent_whenUserDeletedSuccessfully() {
        long id = service.createUser(TestDataProvider.createUserDto()).getId();

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
        assertSuccess(NO_CONTENT, "Successfully deleted", response);
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
        assertNotFound(notExistingId, response);
    }

    private <T> void assertSuccess(HttpStatus httpStatusCode, String description, Response<T> response) {
        assertThat(response.getResult())
                .extracting("httpStatusCode", "status", "description")
                .containsExactly(httpStatusCode, SUCCESS, description);
    }

    private void assertNotFound(long notExistingId, ErrorResponse response) {
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
        assertThat(response.getMessage())
                .isEqualTo("Failed entity search");
        assertThat(response.getErrors().get("cause"))
                .isEqualTo("User with ID: " + notExistingId + ", not found");

    }

    private void assertFieldError(ErrorResponse response) {
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(response.getMessage())
                .isEqualTo("Validation field failed");
        assertThat(response.getErrors())
                .containsEntry("fullName", "Name should be between 2 to 30 characters long")
                .containsEntry("email", "Invalid email address")
                .containsEntry("dateOfBirth", "Date of birth must be in the past");
    }
}
