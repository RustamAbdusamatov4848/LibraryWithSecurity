package ru.abdusamatov.librarywithsecurity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDto {
    private String fileName;
    private String bucketName;
    private String fileContent;
}
