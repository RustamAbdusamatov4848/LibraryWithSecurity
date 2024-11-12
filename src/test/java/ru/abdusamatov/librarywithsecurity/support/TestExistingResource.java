package ru.abdusamatov.librarywithsecurity.support;

import ru.abdusamatov.librarywithsecurity.error.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class TestExistingResource {
    public static void assertNotFoundBook(final long id, final ErrorResponse response) {
        assertThat(response.getStatus())
                .isEqualTo(NOT_FOUND);
        assertThat(response.getMessage())
                .isEqualTo("Failed entity search");
        assertThat(response.getErrors().get("cause"))
                .isEqualTo("Book with ID: " + id + ", not found");
    }

    public static void assertNotFoundUser(final long id, final ErrorResponse response) {
        assertThat(response.getStatus())
                .isEqualTo(NOT_FOUND);
        assertThat(response.getMessage())
                .isEqualTo("Failed entity search");
        assertThat(response.getErrors().get("cause"))
                .isEqualTo("User with ID: " + id + ", not found");

    }

}
