package ru.abdusamatov.librarywithsecurity.support;

import org.springframework.http.HttpStatus;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ru.abdusamatov.librarywithsecurity.model.enums.ResponseStatus.ERROR;
import static ru.abdusamatov.librarywithsecurity.model.enums.ResponseStatus.SUCCESS;

public class TestAssertUtil {

    public static <T> void assertSuccess(
            final HttpStatus httpStatusCode,
            final String description,
            final Response<T> response) {
        assertThat(response.getResult())
                .extracting("httpStatusCode", "status", "description")
                .containsExactly(httpStatusCode, SUCCESS, description);
    }

    public static void assertEntityNotFound(final Response<Void> response) {
        assertError(NOT_FOUND, "Failed entity search", response);
    }

    public static void assertFieldErrorForEntity(final Response<Void> response) {
        assertError(BAD_REQUEST, "Validation field failed", response);
    }

    public static <T> void assertError(
            final HttpStatus httpStatusCode,
            final String description,
            final Response<T> response) {
        assertThat(response.getResult())
                .extracting("httpStatusCode", "status", "description", "errors")
                .containsExactly(httpStatusCode, ERROR, description, response.getResult().getErrors());
    }
}
