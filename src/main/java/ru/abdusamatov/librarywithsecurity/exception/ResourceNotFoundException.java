package ru.abdusamatov.librarywithsecurity.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(
            final String resourceName,
            final String fieldName,
            final Long fieldValue
    ) {
        super(String.format("%s with %s: %s, not found", resourceName, fieldName, fieldValue));
    }
}
