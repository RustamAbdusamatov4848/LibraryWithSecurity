package ru.abdusamatov.librarywithsecurity.controller.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.abdusamatov.librarywithsecurity.exception.ExistEmailException;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.util.Response;
import ru.abdusamatov.librarywithsecurity.util.Result;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage(), ex);

        var errors = new HashMap<String, String>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage())
                );

        return exceptionHandler(BAD_REQUEST, "Validation field failed", errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response<Void>> handleEntityNotFoundException(ResourceNotFoundException ex) {
        log.error("Failed entity search: {}", ex.getMessage(), ex);

        final var message = ex.getMessage();
        final var errors = Map.of("cause", message);

        return exceptionHandler(NOT_FOUND, "Failed entity search", errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Failed authorization: {}", ex.getMessage(), ex);

        final var message = ex.getMessage();
        final var errors = Map.of("cause", message);

        return exceptionHandler(UNAUTHORIZED, "Failed authorization", errors);
    }

    @ExceptionHandler(ExistEmailException.class)
    public ResponseEntity<Response<Void>> handleExistEmailException(ExistEmailException ex) {
        log.error("Failed email validation: {}", ex.getMessage(), ex);

        final var message = ex.getMessage();
        final var errors = Map.of("cause", message);

        return exceptionHandler(BAD_REQUEST, "Failed email validation, already exist", errors);
    }

    public ResponseEntity<Response<Void>> exceptionHandler(
            final HttpStatus status,
            final String description,
            final Map<String, String> errors
    ) {
        var result = Result.error(status, description, errors);
        return ResponseEntity
                .status(status)
                .body(Response.buildResponse(result));
    }
}
