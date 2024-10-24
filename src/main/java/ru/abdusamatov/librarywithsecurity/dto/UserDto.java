package ru.abdusamatov.librarywithsecurity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.abdusamatov.librarywithsecurity.models.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static ru.abdusamatov.librarywithsecurity.util.validators.ValidationRegex.EMAIL_REGEX;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long id;

    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 to 30 characters long")
    private String fullName;

    @NotBlank(message = "Email should not be empty")
    @Email(regexp = EMAIL_REGEX, message = "Invalid email address")
    private String email;

    @NotNull(message = "Date of birth should not be null")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private List<Book> books;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
