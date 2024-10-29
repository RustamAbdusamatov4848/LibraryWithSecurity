package ru.abdusamatov.librarywithsecurity.util;

public class ApiResponse {
    private String message;
    private Response response;

    public ApiResponse(String message, Response response) {
        this.message = message;
        this.response = response;
    }
}
