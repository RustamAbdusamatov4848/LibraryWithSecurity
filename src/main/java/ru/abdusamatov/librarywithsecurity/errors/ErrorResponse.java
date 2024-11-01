package ru.abdusamatov.librarywithsecurity.errors;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private Map<String, String> errors;

    public static ErrorResponse buildError(String message, Map<String, String> errors) {
        return ErrorResponse.builder()
                .message(message)
                .errors(errors)
                .build();
    }
}
