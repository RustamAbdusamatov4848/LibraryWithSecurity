package ru.abdusamatov.librarywithsecurity.entities;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.abdusamatov.librarywithsecurity.models.Book;

import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookEntityTest extends BaseEntityTest<Book> {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testTitleValidation() {
        Book book = createInvalidBookWithEmptyTitle();
        Set<String> messages = getValidationMessage(book);

        assertValidationMessage(messages, "The book's title should not be empty");
        assertValidationMessage(messages, "Book title must be between 2 and 200 characters long");
        assertValidationSize(messages, 2);
    }

    @Test
    public void testAuthorNameValidation() {
        Book book = createInvalidBookWithEmptyAuthorName();
        Set<String> messages = getValidationMessage(book);

        assertTrue(messages.contains("Author name should not be empty"));
        assertTrue(messages.contains("Author name must be between 2 and 30 characters long"));
        assertEquals(2, messages.size());
    }

    @Test
    public void testAuthorSurnameValidation() {
        Book book = createInvalidBookWithEmptyAuthorSurname();
        Set<String> messages = getValidationMessage(book);

        assertTrue(messages.contains("Author surname should not be empty"));
        assertTrue(messages.contains("Author surname must be between 2 and 30 characters long"));
        assertEquals(2, messages.size());
    }

    @Test
    public void testYearValidation() {
        Book book = createInvalidBookWithYear();
        Set<String> messages = getValidationMessage(book);

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

    @Override
    protected Book createValidEntity() {
        Book book = new Book();
        book.setTitle("Valid Title");
        book.setAuthorName("John");
        book.setAuthorSurname("Doe");
        book.setYear(2000);
        return book;
    }
}
