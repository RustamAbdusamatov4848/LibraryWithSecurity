package ru.abdusamatov.librarywithsecurity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.abdusamatov.librarywithsecurity.util.validators.ValidationRegex;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password")
public class LibrarianDto {

    private Long id;

    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 to 30 characters long")
    private String fullName;

    @NotBlank(message = "Email should not be empty")
    @Email(regexp = ValidationRegex.EMAIL_REGEX, message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password should not be empty")
    @Size(max = 100, message = "Password should be equals or less than 100 characters long")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibrarianDto librarianDto = (LibrarianDto) o;
        return id != null && id.equals(librarianDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
