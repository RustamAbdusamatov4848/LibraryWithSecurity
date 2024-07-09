package ru.abdusamatov.librarywithsecurity.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.models.Librarian;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarianEntityTest {
    public static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testFullNameValidation() {
        Librarian librarian = createLibrarianWithInvalidFullName();
        Set<String> message = getValidationMessage(librarian);

        assertEquals(2, message.size());
        assertContains(message, "Name should not be empty");
        assertContains(message, "Name should be between 2 to 30 characters long");
    }


    @Test
    public void testEmailValidation() {
        Librarian librarian = createLibratianWithInvalidEmail();
        Set<String> message = getValidationMessage(librarian);

        assertEquals(1, message.size());
        assertContains(message, "Invalid email address");
    }

    @Test
    public void testPasswordValidation() {
        Librarian librarian = createLibrarianWithInvalidPassword();
        Set<String> message = getValidationMessage(librarian);

        assertEquals(1, message.size());
        assertContains(message, "Password should be les then 1000 length");
    }

    @Test
    public void testValidLibrarian() {
        Librarian librarian = createValidLibrarian();
        Set<ConstraintViolation<Librarian>> violations = validator.validate(librarian);

        assertEquals(0, violations.size());
    }

    private Librarian createLibrarianWithInvalidFullName() {
        Librarian librarian = new Librarian();
        librarian.setFullName("");
        librarian.setEmail("valid@email.com");
        librarian.setPassword("validPassword");
        return librarian;
    }

    private Librarian createLibratianWithInvalidEmail() {
        Librarian librarian = new Librarian();
        librarian.setFullName("Valid Name");
        librarian.setEmail("invalid-name");
        librarian.setPassword("validPassword");
        return librarian;
    }

    private Librarian createLibrarianWithInvalidPassword() {
        Librarian librarian = new Librarian();
        librarian.setFullName("Valid Name");
        librarian.setEmail("valid@email.com");
        librarian.setPassword("a".repeat(1002));
        return librarian;
    }

    private Librarian createValidLibrarian() {
        Librarian librarian = new Librarian();
        librarian.setFullName("Valid Name");
        librarian.setEmail("valid.email@example.com");
        librarian.setPassword("validPassword");
        return librarian;
    }

    private Set<String> getValidationMessage(Librarian librarian) {
        Set<ConstraintViolation<Librarian>> violations = validator.validate(librarian);
        return violations
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }

    private void assertContains(Set<String> message, String expectedMessage) {
        assertTrue(message.contains(expectedMessage));
    }
}
