package ru.abdusamatov.librarywithsecurity.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldValue) {
        super(String.format("%s with %s: %s, not found", resourceName, fieldName, fieldValue));
    }
}
