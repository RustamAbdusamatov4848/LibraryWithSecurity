package ru.abdusamatov.librarywithsecurity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.abdusamatov.librarywithsecurity.util.validators.ValidationRegex;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "books")
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 to 30 characters long")
    private String fullName;

    @NotBlank(message = "Email should not be empty")
    @Email(regexp = ValidationRegex.EMAIL_REGEX, message = "Invalid email address")
    private String email;

    @NotNull(message = "Date of birth should not be null")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private List<BookDto> books;
}
