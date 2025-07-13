package ru.abdusamatov.librarywithsecurity.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.abdusamatov.commons.http.Response;
import ru.abdusamatov.commons.http.Result;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.exception.TopPdfConverterException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidationException(final MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage(), ex);

        var errors = new HashMap<String, String>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return exceptionHandler(BAD_REQUEST, "Validation field failed", errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response<Void>> handleEntityNotFoundException(final ResourceNotFoundException ex) {
        log.error("Failed entity search: {}", ex.getMessage(), ex);

        final var message = ex.getMessage();
        final var errors = Map.of("cause", message);

        return exceptionHandler(NOT_FOUND, "Failed entity search", errors);
    }

    @ExceptionHandler(TopPdfConverterException.class)
    public ResponseEntity<Response<Void>> handleTopPdfConverterException(final TopPdfConverterException ex) {
        log.error("TPDFConverter client error: {}", ex.getMessage(), ex);

        final var message = ex.getMessage();
        final var errors = Map.of("cause", message);

        return exceptionHandler(INTERNAL_SERVER_ERROR, "Top pdf converter client error", errors);
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
