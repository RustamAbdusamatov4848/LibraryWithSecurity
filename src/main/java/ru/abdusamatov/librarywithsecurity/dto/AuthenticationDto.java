package ru.abdusamatov.librarywithsecurity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.abdusamatov.librarywithsecurity.util.validators.ValidationRegex;

@Data
@Builder
public class AuthenticationDto {
    @NotBlank(message = "Email should not be empty")
    @Email(regexp = ValidationRegex.EMAIL_REGEX, message = "Invalid email address")
    private String email;

    @NotBlank
    @Size(max = 100, message = "Password should be equals or less than 100 characters long")
    private String password;
}
