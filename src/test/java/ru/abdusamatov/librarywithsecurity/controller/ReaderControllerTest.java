package ru.abdusamatov.librarywithsecurity.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.repository.UserRepository;
import ru.abdusamatov.librarywithsecurity.service.UserService;
import ru.abdusamatov.librarywithsecurity.support.AssertTestStatusUtil;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.util.ParameterizedTypeReferenceTestUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class ReaderControllerTest extends TestBase {

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

        final var response = executeGetAllUsers(OK);

        AssertTestStatusUtil
                .assertSuccess(OK, "List of users", response);
        assertThat(response.getData())
                .asList()
                .isNotNull()
                .isNotEmpty()
                .hasSize(userListSize);
    }

    @Test
    void shouldReturnEmptyList_whenUserAreAbsent() {
        final var response = executeGetAllUsers(OK);

        AssertTestStatusUtil
                .assertSuccess(OK, "List of users", response);
        assertThat(response.getData())
                .isEmpty();
    }

    @Test
    void shouldReturnUser_whenExistingUserIdProvided() {
        final var id = service
                .createUser(TestDataProvider.createUserDto().build())
                .getId();

        final var response = executeGetUserById(OK, id, UserDto.class);

        AssertTestStatusUtil
                .assertSuccess(OK, "User successfully found", response);
        assertThat(response.getData().getId())
                .isEqualTo(id);
    }

    @Test
    void shouldReturnNotFound_whenNonExistingUserIdProvided() {
        final var id = 10000L;

        final var response = executeGetUserById(NOT_FOUND, id, Void.class);

        assertUserNotFound(response);
    }

    @Test
    void shouldCreateUser_whenValidDataProvided() {
        final var validUserDto = TestDataProvider
                .createUserDto()
                .build();

        final var response = executeCreateUser(OK, validUserDto, UserDto.class);

        AssertTestStatusUtil
                .assertSuccess(CREATED, "User successfully saved", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "books")
                .isEqualTo(validUserDto);
        assertThat(response.getData().getId())
                .isNotNull();
        assertThat(response.getData().getBooks())
                .isNull();
    }

    @Test
    void shouldReturnBadRequest_whenUserWithInvalidFields() {
        final var invalidUserDto = TestDataProvider
                .createUserDtoWithInvalidFields()
                .build();

        final var response = executeCreateUser(BAD_REQUEST, invalidUserDto, Void.class);

        assertFieldErrorForUser(response);
    }

    @Test
    void shouldUpdateUser_whenValidUserDtoProvided() {
        final var userToBeUpdated = service
                .createUser(TestDataProvider.createUserDto().build());
        final var updateUserDto = TestDataProvider
                .updateUserDto(userToBeUpdated)
                .build();

        final var response = executeUpdateUser(OK, updateUserDto, UserDto.class);

        AssertTestStatusUtil
                .assertSuccess(OK, "User successfully updated", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateUserDto);
    }

    @Test
    void shouldReturnNotFound_whenUserToUpdateDoesNotExist() {
        final var notExistingId = 10000L;
        final var userToBeUpdated = service
                .createUser(TestDataProvider.createUserDto().build());
        final var updateUserDto = TestDataProvider
                .updateUserDto(userToBeUpdated)
                .id(notExistingId)
                .build();

        final var response = executeUpdateUser(NOT_FOUND, updateUserDto, Void.class);

        assertUserNotFound(response);
    }

    @Test
    void shouldReturnBadRequest_whenUpdateUserWithInvalidFields() {
        final var userToBeUpdated = service
                .createUser(TestDataProvider.createUserDto().build());
        final var updateUserDto = TestDataProvider
                .updateUserDtoWithInvalidFields(userToBeUpdated)
                .build();

        final var response = executeUpdateUser(BAD_REQUEST, updateUserDto, Void.class);

        assertFieldErrorForUser(response);
    }

    @Test
    void shouldReturnNoContent_whenUserDeletedSuccessfully() {
        final var id = service
                .createUser(TestDataProvider.createUserDto().build())
                .getId();

        final var response = executeDeleteUserById(OK, id);

        AssertTestStatusUtil
                .assertSuccess(NO_CONTENT, "Successfully deleted", response);
    }

    @Test
    void shouldReturnNotFound_whenBookToDeleteDoesNotExist() {
        final var notExistingId = 10000L;

        final var response = executeDeleteUserById(NOT_FOUND, notExistingId);

        assertUserNotFound(response);
    }

    private Response<List<UserDto>> executeGetAllUsers(final HttpStatus httpStatus) {
        final var response = webTestClient.get().uri(uriBuilder ->
                        uriBuilder
                                .pathSegment(BASE_URL)
                                .queryParam("page", 0)
                                .queryParam("size", 20)
                                .build())
                .exchange()
                .expectStatus().isEqualTo(httpStatus)
                .expectBody(ParameterizedTypeReferenceTestUtil.getListResponseReference(UserDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private <T> Response<T> executeGetUserById(
            final HttpStatus status,
            final long id,
            final Class<T> responseType
    ) {
        final var response = webTestClient.get().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference(responseType))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private <T> Response<T> executeCreateUser(
            final HttpStatus status,
            final UserDto userDto,
            final Class<T> responseType
    ) {
        final var response = webTestClient.post().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference(responseType))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private <T> Response<T> executeUpdateUser(
            final HttpStatus status,
            final UserDto userDto,
            final Class<T> responseType
    ) {
        final var response = webTestClient.put().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference(responseType))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private Response<Void> executeDeleteUserById(
            final HttpStatus status,
            final long id
    ) {
        final var response = webTestClient.delete().uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    static void assertUserNotFound(final Response<Void> response) {
        AssertTestStatusUtil.assertError(NOT_FOUND, "Failed entity search", response);
    }

    static void assertFieldErrorForUser(final Response<Void> response) {
        AssertTestStatusUtil.assertError(BAD_REQUEST, "Validation field failed", response);
    }

}
