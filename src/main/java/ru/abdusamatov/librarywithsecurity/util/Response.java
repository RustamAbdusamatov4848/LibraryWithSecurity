package ru.abdusamatov.librarywithsecurity.util;

import lombok.Data;

@Data
public class Response<T> {
    private final ApiResponseStatus status;
    private T responseBody;

    public Response(ApiResponseStatus status) {
        this.status = status;
    }

    public Response(ApiResponseStatus status, T responseBody) {
        this.status = status;
        this.responseBody = responseBody;
    }
}
