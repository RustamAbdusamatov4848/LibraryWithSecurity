package ru.abdusamatov.librarywithsecurity.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.abdusamatov.librarywithsecurity.models.Librarian;
import ru.abdusamatov.librarywithsecurity.services.LibrarianService;

@Controller
@RequiredArgsConstructor
public class LibrarianRegistrationLoginController {
    private final LibrarianService librarianService;

    @GetMapping("/registration")
    public String registration(@ModelAttribute("librarian") Librarian librarian) {
        return "librarians/auth/registration";
    }

    @PostMapping("/registration")
    public String createLibrarian(@ModelAttribute("librarian") Librarian librarian, Model model) {
        if (!librarianService.createLibrarian(librarian)) {
            model.addAttribute("errorEmail",
                    "Librarian with email: " + librarian.getEmail() + " already exists!");
            return "librarians/auth/registration";
        }
        return "redirect:/user";
    }

    @GetMapping("/login")
    public String login() {
        return "librarians/auth/login";
    }
}
