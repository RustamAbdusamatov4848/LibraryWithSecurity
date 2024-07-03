package ru.abdusamatov.librarywithsecurity.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.models.User;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserEntityTest {
    private static Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNameValidation() {
        User user = createUserWithInvalidName();
        Set<String> messages = getValidationMethod(user);

        assertEquals(2, messages.size());
        assertTrue(messages.contains("Name should not be empty"));
        assertTrue(messages.contains("Name should be between 2 to 30 characters long"));
    }

    @Test
    public void testEmailValidation() {
        User user = createUserWithInvalidEmail();
        Set<String> messages = getValidationMethod(user);

        assertEquals(1, messages.size());
        assertTrue(messages.contains("Invalid email address"));
    }

    @Test
    public void testValidUser() {
        User user = createValidUser();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
    }

    private User createUserWithInvalidName() {
        User user = new User();
        user.setFullName("");
        user.setEmail("valid.email@example.com");
        user.setYearOfBirth(new Date());
        return user;
    }

    private User createUserWithInvalidEmail() {
        User user = new User();
        user.setFullName("Valid Name");
        user.setEmail("invalid-email");
        user.setYearOfBirth(new Date());
        return user;
    }

    private User createValidUser() {
        User user = new User();
        user.setFullName("Valid Name");
        user.setEmail("valid.email@example.com");
        user.setYearOfBirth(new Date());
        return user;
    }

    private Set<String> getValidationMethod(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        return violations
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}

