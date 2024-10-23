package ru.abdusamatov.librarywithsecurity.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.abdusamatov.librarywithsecurity.models.Book;
import ru.abdusamatov.librarywithsecurity.models.User;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    public final BookRepository bookRepository;

    public List<Book> bookList(boolean isSortedByYear) {
        if (isSortedByYear) {
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

    public Book showBook(Long bookID) {
        return bookRepository.findById(bookID).orElse(null);
    }

    @Transactional
    public User getBookOwner(Long bookID) {
        return bookRepository.findById(bookID).map(Book::getOwner).orElse(null);
    }

    @Transactional
    public void createBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void editBook(Long bookID, Book editedBook) {
        editedBook.setBookId(bookID);
        bookRepository.save(editedBook);
    }

    @Transactional
    public void deleteBook(Long bookID) {
        bookRepository.deleteById(bookID);
    }

    @Transactional
    public void releaseBook(Long bookID) {
        bookRepository.findById(bookID).ifPresent(book -> {
            book.setOwner(null);
            book.setTakenAt(null);
            book.setExpired(false);
        });
    }

    @Transactional
    public void assignBook(Long bookID, User selectedUser) {
        bookRepository.findById(bookID).ifPresent(book -> {
            book.setOwner(selectedUser);
            book.setTakenAt(new Date());
        });
    }

    public List<Book> searchByTitle(String query) {
        return bookRepository.findByTitleStartingWith(query);
    }
}
