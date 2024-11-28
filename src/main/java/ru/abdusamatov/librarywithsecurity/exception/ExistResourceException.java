package ru.abdusamatov.librarywithsecurity.exception;

public class ExistResourceException extends RuntimeException{
    public ExistResourceException(String resourceName) {
        super(String.format("%s is already exist, try another one", resourceName));
    }
}
