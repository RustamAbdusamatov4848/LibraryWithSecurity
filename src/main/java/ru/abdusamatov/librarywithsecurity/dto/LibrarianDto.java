package ru.abdusamatov.librarywithsecurity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.abdusamatov.librarywithsecurity.util.validators.ValidationRegex;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password")
@Builder
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

}
