package ru.abdusamatov.librarywithsecurity.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.UserRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserList() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getUserList();
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByID() {
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User result = userService.getUserByID(1L);
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByFullname() {
        User user = new User();
        when(userRepository.findByFullName(anyString())).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByFullname("John Doe");
        assertTrue(result.isPresent());
        verify(userRepository, times(1)).findByFullName("John Doe");
    }

    @Test
    void testGetBooksByPersonID() {
        User user = new User();
        Book book = new Book();
        book.setTakenAt(new Date(System.currentTimeMillis() - 900000000L)); // Book taken for more than 10 days
        user.setBooks(List.of(book));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<Book> result = userService.getBooksByPersonID(1L);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isExpired());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBooksByPersonID_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        List<Book> result = userService.getBooksByPersonID(1L);
        assertEquals(Collections.emptyList(), result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateUser_UserExists() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        boolean result = userService.createUser(user);
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, never()).save(user);
    }

    @Test
    void testCreateUser_UserDoesNotExist() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        boolean result = userService.createUser(user);
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testEditPerson() {
        User user = new User();
        userService.editPerson(user, 1L);
        assertEquals(1L, user.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUserByID() {
        userService.deleteUserByID(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
