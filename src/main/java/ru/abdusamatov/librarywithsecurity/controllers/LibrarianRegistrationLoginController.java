package ru.abdusamatov.librarywithsecurity.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.services.LibrarianService;

@Controller
@RequiredArgsConstructor
public class LibrarianRegistrationLoginController {
    private final LibrarianService librarianService;

    @GetMapping("/lib/registration")
    public String registration(@ModelAttribute("librarian") LibrarianDto librarianDto) {
        return "librarians/auth/registration";
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/lib/registration",
            consumes = {"application/json"}
    )
    public String createLibrarian(@ModelAttribute("librarian") LibrarianDto librarianDto, Model model) {
        librarianService.createLibrarian(librarianDto);
        return "redirect:/users";
    }

    @GetMapping("/login")
    public String login() {
        return "librarians/auth/login";
    }
}
