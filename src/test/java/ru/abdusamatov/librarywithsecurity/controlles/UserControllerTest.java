package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;
import ru.abdusamatov.librarywithsecurity.services.UserService;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.List;

public class UserControllerTest extends TestBase {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserService service;

    @Test
    void shouldGetAllUsers() {
        int userListSize = 10;
        List<UserDto> userDtoList = TestDataProvider.createListUserDto(userListSize);
        userDtoList.forEach(userDto -> service.createUser(userDto));

        webTestClient.get().uri(uriBuilder ->
                        uriBuilder
                                .path("/users")
                                .queryParam("page", 0)
                                .queryParam("size", 20)
                                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("OK")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("List of users")
                .jsonPath("$.data.length()").isEqualTo(userListSize);
    }

    @Test
    void shouldReturnUser_whenExistingUserIdProvided() {
        long id = service.createUser(TestDataProvider.createUserDto()).getId();

        webTestClient.get().uri("/users/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(id)
                .jsonPath("$.result.httpStatusCode").isEqualTo("OK")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("User successfully found");
    }

    @Test
    void shouldReturnNotFound_whenNonExistingUserIdProvided() {
        long id = 10000L;

        webTestClient.get().uri("/users/" + id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed entity search")
                .jsonPath("$.errors.cause").isEqualTo("User with ID: " + id + ", not found");
    }

    @Test
    void shouldCreateUser_whenValidDataProvided() {
        UserDto validUserDto = TestDataProvider.createUserDto();

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUserDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("CREATED")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("User successfully saved")
                .jsonPath("$.data.fullName").isEqualTo(validUserDto.getFullName())
                .jsonPath("$.data.email").isEqualTo(validUserDto.getEmail())
                .jsonPath("$.data.dateOfBirth").isEqualTo(validUserDto.getDateOfBirth().toString());

    }

    @Test
    void shouldReturnBadRequest_whenUserWithInvalidFields() {
        UserDto invalidUserDto = TestDataProvider.createUserDtoWithInvalidFields();

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUserDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation field failed")
                .jsonPath("$.errors.fullName").isEqualTo("Name should be between 2 to 30 characters long")
                .jsonPath("$.errors.email").isEqualTo("Invalid email address")
                .jsonPath("$.errors.dateOfBirth").isEqualTo("Date of birth must be in the past");
    }

    @Test
    void shouldUpdateUser_whenValidUserDtoProvided() {
        UserDto userToBeUpdated = service.createUser(TestDataProvider.createUserDto());
        UserDto updateUserDto = TestDataProvider.updateUserDto(userToBeUpdated);


        webTestClient.put().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateUserDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result.httpStatusCode").isEqualTo("OK")
                .jsonPath("$.result.status").isEqualTo("SUCCESS")
                .jsonPath("$.result.description").isEqualTo("User successfully updated")
                .jsonPath("$.data.id").isEqualTo(updateUserDto.getId())
                .jsonPath("$.data.fullName").isEqualTo(updateUserDto.getFullName())
                .jsonPath("$.data.email").isEqualTo(updateUserDto.getEmail())
                .jsonPath("$.data.dateOfBirth").isEqualTo(updateUserDto.getDateOfBirth().toString())
                .jsonPath("$.data.books").isEqualTo(updateUserDto.getBooks());
    }

    @Test
    void shouldReturnNotFound_whenUserToUpdateDoesNotExist() {
        long notExistingId = 10000L;

        UserDto userToBeUpdated = service.createUser(TestDataProvider.createUserDto());
        UserDto updateUserDto = TestDataProvider.updateUserDto(userToBeUpdated);
        updateUserDto.setId(notExistingId);

        webTestClient.put().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateUserDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed entity search")
                .jsonPath("$.errors.cause").isEqualTo("User with ID: " + notExistingId + ", not found");
    }

    @Test
    void shouldReturnBadRequest_whenUpdateUserWithInvalidFields() {
        UserDto userToBeUpdated = service.createUser(TestDataProvider.createUserDto());
        UserDto updateUserDto = TestDataProvider.updateUserDtoWithInvalidFields(userToBeUpdated);

        webTestClient.put().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateUserDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation field failed")
                .jsonPath("$.errors.fullName").isEqualTo("Name should be between 2 to 30 characters long")
                .jsonPath("$.errors.email").isEqualTo("Invalid email address")
                .jsonPath("$.errors.dateOfBirth").isEqualTo("Date of birth must be in the past");

    }

    @Test
    void shouldReturnNoContent_whenUserDeletedSuccessfully() {
        long id = service.createUser(TestDataProvider.createUserDto()).getId();

        webTestClient.delete().uri("/users/" + id)
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

        webTestClient.delete().uri("/users/" + notExistingId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Failed entity search")
                .jsonPath("$.errors.cause").isEqualTo("User with ID: " + notExistingId + ", not found");
    }

    @Override
    protected void clearDatabase() {
        repository.deleteAll();
    }
}
