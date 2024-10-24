package ru.abdusamatov.librarywithsecurity.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @NotEmpty(message = "The book's title should not be empty")
    @Size(min = 2, max = 200, message = "Book title must be between 2 and 200 characters long")
    @Column(name = "book_title")
    private String title;

    @NotEmpty(message = "Author name should not be empty")
    @Size(min = 2, max = 30, message = "Author name must be between 2 and 30 characters long")
    @Column(name = "book_author_name")
    private String authorName;

    @NotEmpty(message = "Author surname should not be empty")
    @Size(min = 2, max = 30, message = "Author surname must be between 2 and 30 characters long")
    @Column(name = "book_author_surname")
    private String authorSurname;

    @Min(value = 1500, message = "Year must be greater than 1500")
    @Column(name = "book_year")
    private int year;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;

    @Column(name = "book_taken_at")
    private LocalDateTime takenAt;

    @Transient
    private boolean expired;

    public Book() {
    }

    public Book(String title, String authorName, String authorSurname, int year) {
        this.title = title;
        this.authorName = authorName;
        this.authorSurname = authorSurname;
        this.year = year;
    }
}
