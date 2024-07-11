package ru.abdusamatov.librarywithsecurity.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseEntityTest<T> {
    protected Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    protected abstract T createValidEntity();

    protected Set<String> getValidationMessage(T entity) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        return constraintViolations
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

    }

    protected void assertValidationMessage(Set<String> messages, String expectedMessage) {
        assertTrue(messages.contains(expectedMessage));
    }

    protected void assertValidationSize(Set<String> messages, int expectedSize) {
        assertEquals(messages.size(), expectedSize);
    }
}
