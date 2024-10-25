package ru.abdusamatov.librarywithsecurity.controllers.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.abdusamatov.librarywithsecurity.errors.ErrorResponse;
import ru.abdusamatov.librarywithsecurity.exceptions.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage())
                );

        ErrorResponse errorResponse = new ErrorResponse("Validation failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(ResourceNotFoundException ex) {
        log.error("Failed entity search: {}", ex.getMessage(), ex);
        String message = ex.getMessage();
        Map<String, String> errors = new HashMap<>();
        errors.put("cause", message);

        ErrorResponse errorResponse = new ErrorResponse("Failed entity search", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
