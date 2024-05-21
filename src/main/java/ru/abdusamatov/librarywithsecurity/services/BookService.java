package ru.abdusamatov.librarywithsecurity.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    public final BookRepository bookRepository;

    public List<Book> bookList(boolean isSortedByYear){
        if (isSortedByYear){
            return bookRepository.findAll(Sort.by("year"));
        }
        return bookRepository.findAll();
    }
    public List<Book> showWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear)
            return bookRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        else
            return bookRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public Book showBook(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Transactional
    public User getBookOwner(Long bookID) {
        return bookRepository.findById(bookID).map(Book::getOwner).orElse(null);
    }

    @Transactional
    public void createBook(Book book) {
        bookRepository.save(book);
    }
}
