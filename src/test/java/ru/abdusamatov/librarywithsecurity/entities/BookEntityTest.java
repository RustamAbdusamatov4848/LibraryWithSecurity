package ru.abdusamatov.librarywithsecurity.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.models.Book;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookEntityTest {
    private static Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testTitleValidation() {
        Book book = createInvalidBookWithEmptyTitle();
        Set<String> messages = getValidationMessages(book);

        assertTrue(messages.contains("The book's title should not be empty"));
        assertTrue(messages.contains("Book title must be between 2 and 200 characters long"));
        assertEquals(2, messages.size());
    }

    @Test
    public void testAuthorNameValidation() {
        Book book = createInvalidBookWithEmptyAuthorName();
        Set<String> messages = getValidationMessages(book);

        assertTrue(messages.contains("Author name should not be empty"));
        assertTrue(messages.contains("Author name must be between 2 and 30 characters long"));
        assertEquals(2, messages.size());
    }

    @Test
    public void testAuthorSurnameValidation() {
        Book book = createInvalidBookWithEmptyAuthorSurname();
        Set<String> messages = getValidationMessages(book);

        assertTrue(messages.contains("Author surname should not be empty"));
        assertTrue(messages.contains("Author surname must be between 2 and 30 characters long"));
        assertEquals(2, messages.size());
    }

    @Test
    public void testYearValidation() {
        Book book = createInvalidBookWithYear();
        Set<String> messages = getValidationMessages(book);

        assertTrue(messages.contains("Year must be greater than 1500"));
        assertEquals(1, messages.size());
    }

    @Test
    public void testValidBook() {
        Book book = createValidBook();
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertEquals(0, violations.size());
    }

    private Book createInvalidBookWithEmptyTitle() {
        Book book = new Book();
        book.setTitle("");
        book.setAuthorName("John");
        book.setAuthorSurname("Doe");
        book.setYear(2000);
        return book;
    }

    private Book createInvalidBookWithEmptyAuthorName() {
        Book book = new Book();
        book.setTitle("Valid Title");
        book.setAuthorName("");
        book.setAuthorSurname("Doe");
        book.setYear(2000);
        return book;
    }

    private Book createInvalidBookWithEmptyAuthorSurname() {
        Book book = new Book();
        book.setTitle("Valid Title");
        book.setAuthorName("John");
        book.setAuthorSurname("");
        book.setYear(2000);
        return book;
    }

    private Book createInvalidBookWithYear() {
        Random random = new Random();
        Book book = new Book();
        book.setTitle("Valid Title");
        book.setAuthorName("John");
        book.setAuthorSurname("Doe");
        int year = random.nextInt(1500);
        book.setYear(year);
        return book;
    }

    private Book createValidBook() {
        Book book = new Book();
        book.setTitle("Valid Title");
        book.setAuthorName("John");
        book.setAuthorSurname("Doe");
        book.setYear(2000);
        return book;
    }

    private Set<String> getValidationMessages(Book book) {
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}
