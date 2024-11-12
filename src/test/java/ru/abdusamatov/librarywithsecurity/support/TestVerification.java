package ru.abdusamatov.librarywithsecurity.support;

import ru.abdusamatov.librarywithsecurity.error.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class TestVerification {
    public static void assertFieldErrorForUser(final ErrorResponse response) {
        assertThat(response.getStatus())
                .isEqualTo(BAD_REQUEST);
        assertThat(response.getMessage())
                .isEqualTo("Validation field failed");
        assertThat(response.getErrors())
                .containsEntry("fullName", "Name should be between 2 to 30 characters long")
                .containsEntry("email", "Invalid email address")
                .containsEntry("dateOfBirth", "Date of birth must be in the past");
    }

    public static void assertFieldErrorForLibrarian(final ErrorResponse response) {
        assertThat(response.getStatus())
                .isEqualTo(BAD_REQUEST);
        assertThat(response.getMessage())
                .isEqualTo("Validation field failed");
        assertThat(response.getErrors())
                .containsEntry("fullName", "Name should be between 2 to 30 characters long")
                .containsEntry("email", "Invalid email address")
                .containsEntry("password", "Password should be equals or less than 100 characters long");
    }
}
