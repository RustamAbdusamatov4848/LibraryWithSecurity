package ru.abdusamatov.librarywithsecurity.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 to 30 characters long")
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", unique = true)
    @Email(message = "Invalid email address")
    private String email;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "year_of_birth")
    private Date yearOfBirth;

    private LocalDateTime dateOfCreated;

    @OneToMany(mappedBy = "owner")
    private List<Book> books;

    @PrePersist
    private void init() {
        dateOfCreated = LocalDateTime.now();
    }
}
