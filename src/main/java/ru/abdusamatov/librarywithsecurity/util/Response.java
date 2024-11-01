package ru.abdusamatov.librarywithsecurity.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private Result result;
    private T data;

    public static <T> Response<T> buildResponse(Result result, T data) {
        return Response.<T>builder()
                .data(data)
                .result(result)
                .build();
    }
}
