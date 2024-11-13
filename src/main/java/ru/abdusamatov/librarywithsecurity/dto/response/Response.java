package ru.abdusamatov.librarywithsecurity.dto.response;

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

    public static <T> Response<T> buildResponse(final Result result, final T data) {
        return Response.<T>builder()
                .data(data)
                .result(result)
                .build();
    }

    public static <T> Response<T> buildResponse(final Result result) {
        return Response.<T>builder()
                .data(null)
                .result(result)
                .build();
    }
}
