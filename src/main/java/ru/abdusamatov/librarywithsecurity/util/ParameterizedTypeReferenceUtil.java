package ru.abdusamatov.librarywithsecurity.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.core.ParameterizedTypeReference;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;

@UtilityClass
public class ParameterizedTypeReferenceUtil {

    public static ParameterizedTypeReference<Response<Void>> getResponseReference() {
        return getResponseReference(Void.class);
    }

    public static <T> ParameterizedTypeReference<Response<T>> getResponseReference(final Class<T> type) {
        return ParameterizedTypeReference.forType(TypeUtils.parameterize(Response.class, type));
    }

}
