package ru.abdusamatov.librarywithsecurity.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.dto.response.Result;
import ru.abdusamatov.librarywithsecurity.support.AssertTestStatusUtil;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.util.ParameterizedTypeReferenceTestUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class ReaderControllerTest extends TestBase {

    public static final String BASE_URL = "users";

    @Test
    void shouldGetAllUsers() {
        final var userListSize = 10;
        final var userList = TestDataProvider.createListUser(userListSize);
        userRepository.saveAll(userList);

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
        final var id = userRepository
                .save(TestDataProvider.createUser())
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
    void shouldReturnDocument_whenUserExist() {
        final var savedUser = userRepository.save(TestDataProvider.createUser());
        final var userDocument = savedUser.getDocument();

        when(client.getDocument(userDocument.getBucketName(), userDocument.getFileName()))
                .thenReturn(getMonoResponseByteArray(userDocument.getFileName()));

        final var response = executeGetUserDocument(OK, savedUser.getId());

        AssertTestStatusUtil
                .assertSuccess(OK, "User document successfully found", response);
        verify(client, times(1))
                .getDocument(userDocument.getBucketName(), userDocument.getFileName());
    }

    @Test
    void shouldCreateUser_whenValidDataProvided() {
        final var validUserDto = TestDataProvider
                .createUserDto()
                .build();

        when(client.uploadFile(
                TestDataProvider.getMultipartFile(),
                validUserDto.getDocumentDto().getBucketName()))
                .thenReturn(getMonoResponseVoid(
                        "File " + validUserDto.getDocumentDto().getFileName() + " successfully uploaded"));

        final var response = executeCreateUser(
                OK,
                validUserDto,
                UserDto.class,
                TestDataProvider.getMultipartFile());

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

        final var response = executeCreateUser(
                BAD_REQUEST,
                invalidUserDto,
                Void.class,
                TestDataProvider.getMultipartFile());

        assertFieldErrorForUser(response);
    }

    @Test
    void shouldUpdateUser_whenValidUserDtoProvided() {
        final var userToBeUpdated = userMapper
                .userToDto(userRepository.save(TestDataProvider.createUser()));

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
        final var updateUserDto = TestDataProvider
                .updateUserDto(TestDataProvider.createUserDto().build())
                .id(notExistingId)
                .build();

        final var response = executeUpdateUser(NOT_FOUND, updateUserDto, Void.class);

        assertUserNotFound(response);
    }

    @Test
    void shouldReturnBadRequest_whenUpdateUserWithInvalidFields() {
        final var updateUserDto = TestDataProvider
                .updateUserDtoWithInvalidFields(TestDataProvider.createUserDto().build())
                .build();

        final var response = executeUpdateUser(BAD_REQUEST, updateUserDto, Void.class);

        assertFieldErrorForUser(response);
    }

    @Test
    void shouldReturnNoContent_whenUserDeletedSuccessfully() {
        final var savedUser = userRepository.save(TestDataProvider.createUser());
        final var bucketName = savedUser.getDocument().getBucketName();

        when(client.deleteDocument(bucketName))
                .thenReturn(getMonoResponseVoid("Bucket " + bucketName + " deleted"));

        final var response = executeDeleteUserById(OK, savedUser.getId());

        AssertTestStatusUtil
                .assertSuccess(NO_CONTENT, "Successfully deleted", response);
        verify(client, times(1))
                .deleteDocument(bucketName);
    }

    @Test
    void shouldReturnNotFound_whenBookToDeleteDoesNotExist() {
        final var notExistingId = 10000L;

        final var response = executeDeleteUserById(NOT_FOUND, notExistingId);

        assertUserNotFound(response);
    }

    private Response<List<UserDto>> executeGetAllUsers(final HttpStatus httpStatus) {
        final var response = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
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
        final var response = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
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

    private Response<MultiValueMap<String, Object>> executeGetUserDocument(
            final HttpStatus status,
            final long id
    ) {
        final var response = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id), "document")
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getMultiValueMapResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    @SneakyThrows
    private <T> Response<T> executeCreateUser(
            final HttpStatus status,
            final UserDto userDto,
            final Class<T> responseType,
            final MultipartFile file
    ) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("userDto", userDto);
        builder.part("file", file);

        final var response = webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
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
        final var response = webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
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
        final var response = webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder
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

    private Mono<Response<Void>> getMonoResponseVoid(String description) {
        return Mono.just(Response.buildResponse(
                Result.success(NO_CONTENT, description)));
    }

    private Mono<Response<byte[]>> getMonoResponseByteArray(String fileName) {
        return Mono.just(Response.buildResponse(
                Result.success(OK, "File " + fileName + " successfully loaded"),
                TestDataProvider.getImageBytes("passport.jpg")));
    }

    static void assertUserNotFound(final Response<Void> response) {
        AssertTestStatusUtil.assertError(NOT_FOUND, "Failed entity search", response);
    }

    static void assertFieldErrorForUser(final Response<Void> response) {
        AssertTestStatusUtil.assertError(BAD_REQUEST, "Validation field failed", response);
    }

    @Override
    protected void clearDatabase() {
        userRepository.deleteAll();
    }

}
