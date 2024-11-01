package ru.abdusamatov.librarywithsecurity.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private HttpStatus httpStatusCode;
    private ResponseStatus status;
    private String description;

    public static Result success(HttpStatus httpStatus, String description) {
        return Result.builder()
                .httpStatusCode(httpStatus)
                .status(ResponseStatus.SUCCESS)
                .description(description)
                .build();
    }

    public static Result error(HttpStatus httpStatus, String description) {
        return Result.builder()
                .httpStatusCode(httpStatus)
                .status(ResponseStatus.ERROR)
                .description(description)
                .build();
    }
}
