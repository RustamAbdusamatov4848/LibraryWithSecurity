package ru.abdusamatov.librarywithsecurity.exceptions;

public class ExistEmailException extends RuntimeException {
    public ExistEmailException(String cause, String resourceName) {
        super(String.format("%s email is already exist, try another one", resourceName));
    }
}
