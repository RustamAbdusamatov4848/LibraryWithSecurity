package ru.abdusamatov.librarywithsecurity.support;

import org.springframework.http.HttpStatus;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.abdusamatov.librarywithsecurity.util.ResponseStatus.ERROR;
import static ru.abdusamatov.librarywithsecurity.util.ResponseStatus.SUCCESS;

public class AssertTestStatusUtil {
    public static <T> void assertSuccess(
            final HttpStatus httpStatusCode,
            final String description,
            final Response<T> response) {
        assertThat(response.getResult())
                .extracting("httpStatusCode", "status", "description")
                .containsExactly(httpStatusCode, SUCCESS, description);
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
