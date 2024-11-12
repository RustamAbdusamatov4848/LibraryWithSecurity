package ru.abdusamatov.librarywithsecurity.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.error.ErrorResponse;
import ru.abdusamatov.librarywithsecurity.repository.UserRepository;
import ru.abdusamatov.librarywithsecurity.service.UserService;
import ru.abdusamatov.librarywithsecurity.support.TestControllerBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.support.TestUtils;
import ru.abdusamatov.librarywithsecurity.util.ParameterizedTypeReferenceUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class UserControllerTest extends TestControllerBase {

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
        final var userListSize = 10;
        final var userDtoList = TestDataProvider.createListUserDto(userListSize);
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
        TestUtils.assertSuccess(OK, "List of users", response);
        assertThat(response.getData())
                .asList()
                .isNotNull()
                .isNotEmpty()
                .hasSize(userListSize);
    }

    @Test
    void shouldReturnUser_whenExistingUserIdProvided() {
        final var id = service.createUser(TestDataProvider.createUserDto().build()).getId();

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
        TestUtils.assertSuccess(OK, "User successfully found", response);
        assertThat(response.getData().getId()).isEqualTo(id);
    }

    @Test
    void shouldReturnNotFound_whenNonExistingUserIdProvided() {
        final var id = 10000L;

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
        TestUtils.assertNotFoundUser(id, response);
    }

    @Test
    void shouldCreateUser_whenValidDataProvided() {
        final var validUserDto = TestDataProvider.createUserDto().build();

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
        TestUtils.assertSuccess(CREATED, "User successfully saved", response);
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
        final var invalidUserDto = TestDataProvider.createUserDtoWithInvalidFields().build();

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
        TestUtils.assertFieldErrorForUser(response);
    }

    @Test
    void shouldUpdateUser_whenValidUserDtoProvided() {
        final var userToBeUpdated = service.createUser(TestDataProvider.createUserDto().build());
        final var updateUserDto = TestDataProvider.updateUserDto(userToBeUpdated).build();


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
        TestUtils.assertSuccess(OK, "User successfully updated", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateUserDto);
    }

    @Test
    void shouldReturnNotFound_whenUserToUpdateDoesNotExist() {
        final var notExistingId = 10000L;
        final var userToBeUpdated = service.createUser(TestDataProvider.createUserDto().build());
        final var updateUserDto = TestDataProvider.updateUserDto(userToBeUpdated).id(notExistingId).build();

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
        TestUtils.assertNotFoundUser(notExistingId, response);
    }

    @Test
    void shouldReturnBadRequest_whenUpdateUserWithInvalidFields() {
        final var userToBeUpdated = service.createUser(TestDataProvider.createUserDto().build());
        final var updateUserDto = TestDataProvider.updateUserDtoWithInvalidFields(userToBeUpdated).build();

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
        TestUtils.assertFieldErrorForUser(response);
    }

    @Test
    void shouldReturnNoContent_whenUserDeletedSuccessfully() {
        final var id = service.createUser(TestDataProvider.createUserDto().build()).getId();

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
        final var notExistingId = 10000L;

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
        TestUtils.assertNotFoundUser(notExistingId, response);
    }
}
