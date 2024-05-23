package ru.abdusamatov.librarywithsecurity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.services.BookService;
import ru.abdusamatov.librarywithsecurity.services.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final UserService userService;

    @GetMapping()
    public String bookList(Model model,
                           @RequestParam(value = "page", required = false) Integer page,
                           @RequestParam(value = " books_per_page", required = false) Integer booksPerPage,
                           @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {

        if (page == null || booksPerPage == null) {
            model.addAttribute("books", bookService.bookList(sortByYear));
        } else {
            model.addAttribute("books", bookService.showWithPagination(page, booksPerPage, sortByYear));
        }
        return "books/listOfBooks";
    }

    @GetMapping("/{id}")
    public String showBookByID(@PathVariable("id") Long id, Model model, @ModelAttribute("user") User user) {
        model.addAttribute("book", bookService.showBook(id));

        User userWithThatBookId = bookService.getBookOwner(id);

        if (userWithThatBookId != null) {
            model.addAttribute("owner", userWithThatBookId);
        } else {
            model.addAttribute("users", userService.getUserList());
        }
        return "books/showBook";
    }

    @GetMapping("/createBook")
    public String addNewBook(@ModelAttribute("book") Book book) {
        return "books/createBook";
    }

    @PostMapping()
    public String createBook(@ModelAttribute("book") @Valid Book book, BindingResult result) {
        if (result.hasErrors()) {
            return "books/createBook";
        }
        bookService.createBook(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/editBook")
    public String editBook(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", bookService.showBook(id));
        return "books/editBook";
    }

    @PatchMapping("/{id}")
    public String updateBook(@PathVariable("id") Long id,
                             @ModelAttribute("book") @Valid Book book,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/editBook";
        }
        bookService.editBook(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }

    @PatchMapping("{id}/release")
    public String releaseBook(@PathVariable("id") Long id) {
        bookService.releaseBook(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/assign")
    public String assignBook(@PathVariable("id") Long id, @ModelAttribute("user") User selectedUser) {
        bookService.assignBook(id, selectedUser);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchBook() {
        return "books/search";
    }

    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query) {
        model.addAttribute("books", bookService.searchByTitle(query));
        return "books/search";
    }
}
