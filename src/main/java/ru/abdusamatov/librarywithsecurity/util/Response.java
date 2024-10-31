package ru.abdusamatov.librarywithsecurity.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
