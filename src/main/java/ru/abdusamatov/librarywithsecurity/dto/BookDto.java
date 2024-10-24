package ru.abdusamatov.librarywithsecurity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BookDto {

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

    private boolean expired;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDto bookDto = (BookDto) o;
        return Objects.equals(id, bookDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
