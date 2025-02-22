package ru.abdusamatov.librarywithsecurity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto implements Serializable {

    private Long id;

    @NotBlank(message = "The bucketName should not be empty")
    @Size(min = 3, max = 63, message = "Name should be between 3 and 63 characters long")
    private String bucketName;

    @NotBlank(message = "The file's title should not be empty")
    @Size(min = 5, max = 150, message = "File name must be between 5 and 150 characters long")
    private String fileName;

    private Long userId;
}
