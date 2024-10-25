package ru.abdusamatov.librarywithsecurity.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String resourceName,Long id) {
        super(String.format("%s with ID: %s, not found",resourceName,id));
    }
}
