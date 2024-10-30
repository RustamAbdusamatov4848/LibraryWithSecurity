package ru.abdusamatov.librarywithsecurity.util;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private final String message;
    private final Response<T> response;

    public ApiResponse(String message, Response<T> response) {
        this.message = message;
        this.response = response;
    }
}
