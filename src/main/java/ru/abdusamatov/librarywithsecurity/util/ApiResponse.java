package ru.abdusamatov.librarywithsecurity.util;

public class ApiResponse {
    private final String message;
    private final Response response;

    public ApiResponse(String message, Response response) {
        this.message = message;
        this.response = response;
    }
}
