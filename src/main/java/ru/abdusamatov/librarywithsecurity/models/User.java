package ru.abdusamatov.librarywithsecurity.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotEmpty(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 to 30 characters long")
    @Column(name = "user_full_name")
    private String fullName;

    @Column(name = "user_email", unique = true)
    @Email(message = "Invalid email address")
    private String email;

    @Column(name = "user_date_of_birth")
    private LocalDate yearOfBirth;

    @OneToMany(mappedBy = "owner")
    private List<Book> books;

}
