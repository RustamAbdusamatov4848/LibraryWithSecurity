package ru.abdusamatov.librarywithsecurity.entities;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.models.User;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserEntityTest extends BaseEntityTest<User> {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testNameValidation() {
        User user = createUserWithInvalidName();
        Set<String> messages = getValidationMessage(user);

        assertValidationMessage(messages, "Name should not be empty");
        assertValidationMessage(messages, "Name should be between 2 to 30 characters long");
        assertValidationSize(messages, 2);
    }

    @Test
    public void testEmailValidation() {
        User user = createUserWithInvalidEmail();
        Set<String> messages = getValidationMessage(user);

        assertValidationSize(messages, 1);
        assertValidationMessage(messages, "Invalid email address");
    }

    @Test
    public void testValidUser() {
        User user = createValidEntity();
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

    @Override
    protected User createValidEntity() {
        User user = new User();
        user.setFullName("Valid Name");
        user.setEmail("valid.email@example.com");
        user.setYearOfBirth(new Date());
        return user;
    }
}

