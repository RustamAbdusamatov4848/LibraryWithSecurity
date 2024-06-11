package ru.abdusamatov.librarywithsecurity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.services.UserService;
import ru.abdusamatov.librarywithsecurity.util.UserValidator;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @GetMapping
    public String userList(Model model){
        model.addAttribute("users",userService.getUserList());
        return "users/crud/userList";
    }
    @GetMapping("/{id}")
    public String getUserByID(@PathVariable ("id") Long ID, Model model){
        model.addAttribute("user",userService.getUserByID(ID));
        model.addAttribute("books",userService.getBooksByPersonID(ID));
        return "users/crud/showUser";
    }
    @GetMapping("/createUser")
    public String registration(@ModelAttribute("user") User user){
        return "users/crud/createUser";
    }
    @PostMapping("/registration")
    public String createUser(@ModelAttribute ("user")  User user, Model model){
        if (!userService.createUser(user)){
            model.addAttribute("errorEmail",
                    "User with email: " + user.getEmail() + " already exists!");
            return "users/crud/createUser";
        }
        return "redirect:/user";
    }
    @GetMapping("/{id}/editUser")
    public String editUserByID(Model model, @PathVariable("id") Long ID) {
        model.addAttribute("user", userService.getUserByID(ID));
        return "users/crud/editUser";
    }
    @PatchMapping("/{id}")
    public String updateUserByID(@ModelAttribute("user") @Valid User person, @PathVariable("id") Long ID, BindingResult result){
        userValidator.validate(person,result);
        if(result.hasErrors()){
            return "users/crud/editUser";
        }
        userService.editPerson(person,ID);
        return "redirect:/user";
    }
    @DeleteMapping("/{id}/deleteUser")
    public String deleteUserByID(@PathVariable("id") Long ID) {
        userService.deleteUserByID(ID);
        return "redirect:/user";
    }
}
