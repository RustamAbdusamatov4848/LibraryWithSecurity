package ru.abdusamatov.librarywithsecurity.exception;

public class ExistEmailException extends RuntimeException {
    public ExistEmailException(String resourceName) {
        super(String.format("%s email is already exist, try another one", resourceName));
    }
}
