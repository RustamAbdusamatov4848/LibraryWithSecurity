package ru.abdusamatov.librarywithsecurity.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.models.Librarian;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibrarianEntityTest extends BaseEntityTest<Librarian> {
    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testFullNameValidation() {
        Librarian librarian = createLibrarianWithInvalidFullName();
        Set<String> message = getValidationMessage(librarian);


        assertValidationMessage(message, "Name should not be empty");
        assertValidationMessage(message, "Name should be between 2 to 30 characters long");
        assertValidationSize(message, 2);
    }


    @Test
    public void testEmailValidation() {
        Librarian librarian = createLibrarianWithInvalidEmail();
        Set<String> message = getValidationMessage(librarian);

        assertValidationSize(message, 1);
        assertValidationMessage(message, "Invalid email address");
    }

    @Test
    public void testPasswordValidation() {
        Librarian librarian = createLibrarianWithInvalidPassword();
        Set<String> message = getValidationMessage(librarian);

        assertValidationSize(message, 1);
        assertValidationMessage(message, "Password should be les then 1000 length");
    }

    @Test
    public void testValidLibrarian() {
        Librarian librarian = createValidEntity();
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

    private Librarian createLibrarianWithInvalidEmail() {
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

    @Override
    protected Librarian createValidEntity() {
        Librarian librarian = new Librarian();
        librarian.setFullName("Valid Name");
        librarian.setEmail("valid.email@example.com");
        librarian.setPassword("validPassword");
        return librarian;
    }
}
