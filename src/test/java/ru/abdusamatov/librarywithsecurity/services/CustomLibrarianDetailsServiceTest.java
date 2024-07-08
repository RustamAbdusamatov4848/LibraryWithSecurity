package ru.abdusamatov.librarywithsecurity.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.repositories.LibrarianRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CustomLibrarianDetailsServiceTest {
    @Mock
    private LibrarianRepository librarianRepository;
    @InjectMocks
    private CustomLibrarianDetailsService customLibrarianDetailsService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        Librarian librarian = new Librarian();
        librarian.setEmail("test@example.com");
        when(librarianRepository.findByEmail(anyString())).thenReturn(librarian);

        var result = customLibrarianDetailsService.loadUserByUsername("test@example.com");
        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        verify(librarianRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(librarianRepository.findByEmail(anyString())).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customLibrarianDetailsService.loadUserByUsername("nonexistent@example.com"));

        String expectedMessage = "Librarian not found with email: nonexistent@example.com";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(librarianRepository, times(1)).findByEmail("nonexistent@example.com");
    }
}
