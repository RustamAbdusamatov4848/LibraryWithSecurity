package ru.abdusamatov.librarywithsecurity.util;

public class Response<T> {
    private ApiResponseStatus status;
    private T responseBody;

    public Response(ApiResponseStatus status) {
        this.status = status;
    }

    public Response(ApiResponseStatus status, T responseBody) {
        this.status = status;
        this.responseBody = responseBody;
    }
}
