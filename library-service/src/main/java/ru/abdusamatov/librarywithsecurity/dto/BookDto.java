package ru.abdusamatov.librarywithsecurity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BookDto implements Serializable {

    private Long id;

    @NotBlank(message = "The book's title should not be empty")
    @Size(min = 2, max = 200, message = "Book title must be between 2 and 200 characters long")
    private String title;

    @NotBlank(message = "Author name should not be empty")
    @Size(min = 2, max = 30, message = "Author name must be between 2 and 30 characters long")
    private String authorName;

    @NotBlank(message = "Author surname should not be empty")
    @Size(min = 2, max = 30, message = "Author surname must be between 2 and 30 characters long")
    private String authorSurname;

    @Min(value = 1500, message = "Year must be greater than 1500")
    private int yearOfPublication;

    private LocalDateTime takenAt;

    private Long userId;

}
