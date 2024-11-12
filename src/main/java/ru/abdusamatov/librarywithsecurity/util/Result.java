package ru.abdusamatov.librarywithsecurity.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private HttpStatus httpStatusCode;
    private ResponseStatus status;
    private String description;
    private Map<String, String> errors;

    public static Result success(final HttpStatus httpStatus, final String description) {
        return Result.builder()
                .httpStatusCode(httpStatus)
                .status(ResponseStatus.SUCCESS)
                .description(description)
                .build();
    }

    public static Result error(
            final HttpStatus httpStatus,
            final String description,
            final Map<String, String> errors
    ) {
        return Result.builder()
                .httpStatusCode(httpStatus)
                .status(ResponseStatus.ERROR)
                .description(description)
                .errors(errors)
                .build();
    }
}
