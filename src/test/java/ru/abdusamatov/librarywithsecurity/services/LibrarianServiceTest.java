package ru.abdusamatov.librarywithsecurity.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LibrarianServiceTest {

    @Mock
    private LibrarianRepository librarianRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LibrarianService librarianService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLibrarian_LibrarianExists() {
        Librarian librarian = new Librarian();
        librarian.setEmail("test@example.com");
        when(librarianRepository.findByEmail(anyString())).thenReturn(librarian);

        boolean result = librarianService.createLibrarian(librarian);
        assertFalse(result);
        verify(librarianRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(librarianRepository, never()).save(librarian);
    }

    @Test
    void testCreateLibrarian_LibrarianDoesNotExist() {
        Librarian librarian = new Librarian();
        librarian.setEmail("test@example.com");
        librarian.setPassword("password");
        when(librarianRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        boolean result = librarianService.createLibrarian(librarian);
        assertTrue(result);
        verify(librarianRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("password");
        verify(librarianRepository, times(1)).save(librarian);
        assertEquals("encodedPassword", librarian.getPassword());
    }
}
