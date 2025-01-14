package ru.abdusamatov.librarywithsecurity.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.ParameterizedTypeReference;
import org.testcontainers.shaded.org.apache.commons.lang3.reflect.TypeUtils;
import ru.ilyam.dto.Response;

import java.util.List;

@UtilityClass
public class ParameterizedTypeReferenceTestUtil {

    public static ParameterizedTypeReference<Response<Void>> getResponseReference() {
        return getResponseReference(Void.class);
    }

    public static <T> ParameterizedTypeReference<Response<T>> getResponseReference(final Class<T> type) {
        return ParameterizedTypeReference.forType(TypeUtils.parameterize(Response.class, type));
    }

    public static <T> ParameterizedTypeReference<Response<List<T>>> getListResponseReference(final Class<T> type) {
        return ParameterizedTypeReference.forType(
                TypeUtils.parameterize(Response.class,
                        TypeUtils.parameterize(List.class, type)));
    }
}
