package ru.abdusamatov.librarywithsecurity.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.services.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        initMocks();
        initMockMvc();
    }

    private void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    private void initMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testUserList() throws Exception {
        when(userService.getUserList()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/crud/userList"));
    }

    @Test
    public void testGetUserByID() throws Exception {
        when(userService.getUserByID(anyLong())).thenReturn(new User());
        when(userService.getBooksByPersonID(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("users/crud/showUser"));
    }

    @Test
    public void testRegistration() throws Exception {
        mockMvc.perform(get("/user/createUser"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/crud/createUser"));
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");

        when(userService.createUser(user)).thenReturn(true);
        mockMvc.perform(post("/user/registration")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"));
    }

    @Test
    public void testCreateUserWithExistingEmail() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");

        when(userService.createUser(user)).thenReturn(false);
        mockMvc.perform(post("/user/registration")
                        .flashAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(view().name("users/crud/createUser"));
    }

    @Test
    public void testEditUserByID() throws Exception {
        when(userService.getUserByID(anyLong())).thenReturn(new User());

        mockMvc.perform(get("/user/{id}/editUser", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("users/crud/editUser"));
    }

    @Test
    public void testUpdateUserByID() throws Exception {
        User user = new User();
        user.setFullName("Gun Maximus");

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        mockMvc.perform(patch("/user/{id}", 1L)
                        .flashAttr("user", user)
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"));

        verify(userService, times(1))
                .editPerson(any(User.class), anyLong());

    }

    @Test
    public void testDeleteUserByID() throws Exception {
        doNothing().when(userService).deleteUserByID(anyLong());

        mockMvc.perform(delete("/user//{id}/deleteUser", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"));

        verify(userService, times(1)).deleteUserByID(anyLong());
    }
}
