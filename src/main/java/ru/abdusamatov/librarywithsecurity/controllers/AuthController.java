package ru.abdusamatov.librarywithsecurity.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.abdusamatov.librarywithsecurity.dto.LibrarianDto;
import ru.abdusamatov.librarywithsecurity.services.LibrarianService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final LibrarianService librarianService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/lib/registration"
    )
    public String registration(@ModelAttribute("librarian") LibrarianDto librarianDto) {
        return "librarians/auth/registration";
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/lib/registration"
    )
    public String createLibrarian(@ModelAttribute("librarian") LibrarianDto librarianDto) {
        librarianService.createLibrarian(librarianDto);
        return "redirect:/login";
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/login"
    )
    public String login() {
        return "librarians/auth/login";
    }
}
