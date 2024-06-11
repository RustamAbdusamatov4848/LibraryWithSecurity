package ru.abdusamatov.librarywithsecurity.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.services.UserService;

@Controller
@RequiredArgsConstructor
public class UserRegistrationLoginController {
    private final UserService userService;

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user){
        return "users/auth/registration";
    }

    @PostMapping("/registration")
    public String createUser(@ModelAttribute ("user")  User user, Model model){
        if (!userService.createUser(user)){
            model.addAttribute("errorEmail",
                    "User with email: " + user.getEmail() + " already exists!");
            return "users/auth/registration";
        }
        return "users/auth/login";
    }

    @GetMapping("/login")
    public String login(){
        return "users/auth/login";
    }
}
