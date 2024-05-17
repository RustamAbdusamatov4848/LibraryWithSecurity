package ru.abdusamatov.librarywithsecurity.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.services.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user){
        return "users/registration";
    }

    @PostMapping("/registration")
    public String createUser(@ModelAttribute ("user")  User user){
        userService.createUser(user);
        return "index";
    }
}
