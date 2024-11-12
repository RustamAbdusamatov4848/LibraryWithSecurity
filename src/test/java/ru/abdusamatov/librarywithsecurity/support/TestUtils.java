package ru.abdusamatov.librarywithsecurity.support;

import org.springframework.http.HttpStatus;
import ru.abdusamatov.librarywithsecurity.error.ErrorResponse;
import ru.abdusamatov.librarywithsecurity.util.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ru.abdusamatov.librarywithsecurity.util.ResponseStatus.SUCCESS;

public class TestUtils {
    public static <T> void assertSuccess(
            final HttpStatus httpStatusCode,
            final String description,
            final Response<T> response) {
        assertThat(response.getResult())
                .extracting("httpStatusCode", "status", "description")
                .containsExactly(httpStatusCode, SUCCESS, description);
    }

    public static void assertFieldErrorForBook(final ErrorResponse response) {
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(response.getMessage())
                .isEqualTo("Validation field failed");
        assertThat(response.getErrors())
                .containsEntry("authorName", "Author name must be between 2 and 30 characters long")
                .containsEntry("authorSurname", "Author surname must be between 2 and 30 characters long")
                .containsEntry("yearOfPublication", "Year must be greater than 1500");
    }

    public static void assertFieldErrorForUser(final ErrorResponse response) {
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(response.getMessage())
                .isEqualTo("Validation field failed");
        assertThat(response.getErrors())
                .containsEntry("fullName", "Name should be between 2 to 30 characters long")
                .containsEntry("email", "Invalid email address")
                .containsEntry("dateOfBirth", "Date of birth must be in the past");
    }

    public static void assertFieldErrorForLibrarian(final ErrorResponse response) {
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
        assertThat(response.getMessage())
                .isEqualTo("Validation field failed");
        assertThat(response.getErrors())
                .containsEntry("fullName", "Name should be between 2 to 30 characters long")
                .containsEntry("email", "Invalid email address")
                .containsEntry("password", "Password should be equals or less than 100 characters long");
    }

    public static void assertNotFoundBook(final long id, final ErrorResponse response) {
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
        assertThat(response.getMessage())
                .isEqualTo("Failed entity search");
        assertThat(response.getErrors().get("cause"))
                .isEqualTo("Book with ID: " + id + ", not found");
    }

    public static void assertNotFoundUser(final long id, final ErrorResponse response) {
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
        assertThat(response.getMessage())
                .isEqualTo("Failed entity search");
        assertThat(response.getErrors().get("cause"))
                .isEqualTo("User with ID: " + id + ", not found");

    }

}
