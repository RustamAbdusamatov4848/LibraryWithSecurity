package ru.abdusamatov.librarywithsecurity.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "Book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long bookId;

    @NotEmpty(message = "The book's title should not be empty")
    @Size(min = 2, max = 200, message = "Book title must be between 2 and 200 characters long")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Author name should not be empty")
    @Size(min = 2, max = 30, message = "Author name must be between 2 and 30 characters long")
    @Column(name = "author_name")
    private String authorName;

    @NotEmpty(message = "Author surname should not be empty")
    @Size(min = 2, max = 30, message = "Author surname must be between 2 and 30 characters long")
    @Column(name = "author_surname")
    private String authorSurname;

    @Min(value = 1500, message = "Year must be greater than 1500")
    @Column(name = "year")
    private int year;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User owner;

    @Column(name = "taken_at")
    private Date takenAt;

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
