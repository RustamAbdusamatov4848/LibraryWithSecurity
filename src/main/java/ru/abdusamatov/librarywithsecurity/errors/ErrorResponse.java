package ru.abdusamatov.librarywithsecurity.errors;


import lombok.Data;

import java.util.Map;

@Data
public class ErrorResponse {
    private String message;
    private Map<String, String> errors;

    public ErrorResponse(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }
}
