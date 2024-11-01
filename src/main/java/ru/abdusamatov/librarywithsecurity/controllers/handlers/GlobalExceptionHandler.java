package ru.abdusamatov.librarywithsecurity.controllers.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.abdusamatov.librarywithsecurity.errors.ErrorResponse;
import ru.abdusamatov.librarywithsecurity.exceptions.ExistEmailException;
import ru.abdusamatov.librarywithsecurity.exceptions.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.util.Response;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ru.abdusamatov.librarywithsecurity.errors.ErrorResponse.buildError;
import static ru.abdusamatov.librarywithsecurity.util.Response.buildResponse;
import static ru.abdusamatov.librarywithsecurity.util.Result.error;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage())
                );

        return buildResponse(error(BAD_REQUEST), buildError("Validation field failed", errors));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Response<ErrorResponse> handleEntityNotFoundException(ResourceNotFoundException ex) {
        log.error("Failed entity search: {}", ex.getMessage(), ex);

        String message = ex.getMessage();
        Map<String, String> errors = Map.of("cause", message);

        return buildResponse(error(NOT_FOUND), buildError("Failed entity search", errors));
    }

    @ExceptionHandler(AuthenticationException.class)
    public Response<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.error("Failed authorization: {}", ex.getMessage(), ex);

        String message = ex.getMessage();
        Map<String, String> errors = Map.of("cause", message);

        return buildResponse(error(UNAUTHORIZED), buildError("Failed authorization", errors));
    }

    @ExceptionHandler(ExistEmailException.class)
    public Response<ErrorResponse> handleExistEmailException(ExistEmailException ex) {
        log.error("Failed email validation: {}", ex.getMessage(), ex);

        String message = ex.getMessage();
        Map<String, String> errors = Map.of("cause", message);

        return buildResponse(error(BAD_REQUEST), buildError("Failed email validation, already exist", errors));
    }
}
