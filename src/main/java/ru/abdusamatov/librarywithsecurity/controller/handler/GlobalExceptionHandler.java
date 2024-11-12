package ru.abdusamatov.librarywithsecurity.controller.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.abdusamatov.librarywithsecurity.error.ErrorResponse;
import ru.abdusamatov.librarywithsecurity.exception.ExistEmailException;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage())
                );

        ErrorResponse errorResponse = new ErrorResponse(BAD_REQUEST, "Validation field failed", errors);
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(ResourceNotFoundException ex) {
        log.error("Failed entity search: {}", ex.getMessage(), ex);

        String message = ex.getMessage();
        Map<String, String> errors = Map.of("cause", message);

        ErrorResponse errorResponse = new ErrorResponse(NOT_FOUND, "Failed entity search", errors);
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.error("Failed authorization: {}", ex.getMessage(), ex);

        String message = ex.getMessage();
        Map<String, String> errors = Map.of("cause", message);

        ErrorResponse errorResponse = new ErrorResponse(UNAUTHORIZED, "Failed authorization", errors);
        return new ResponseEntity<>(errorResponse, UNAUTHORIZED);
    }

    @ExceptionHandler(ExistEmailException.class)
    public ResponseEntity<ErrorResponse> handleExistEmailException(ExistEmailException ex) {
        log.error("Failed email validation: {}", ex.getMessage(), ex);

        String message = ex.getMessage();
        Map<String, String> errors = Map.of("cause", message);

        ErrorResponse errorResponse = new ErrorResponse(BAD_REQUEST, "Failed email validation, already exist", errors);
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

}
