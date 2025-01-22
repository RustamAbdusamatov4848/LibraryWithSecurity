package ru.abdusamatov.librarywithsecurity.service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.dto.FileDto;
import ru.abdusamatov.librarywithsecurity.exception.TopPdfConverterException;
import ru.ilyam.http.Response;
import ru.ilyam.enums.ResponseStatus;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopPdfConverterClientService {
    private final TopPdfConverterClient converterClient;

    public Mono<Void> createBucket(final String bucketName) {
        return converterClient.addBucket(bucketName)
                .flatMap(response -> checkResponseStatus(response,
                        String.format("Bucket %s successfully created", bucketName)))
                .then();
    }

    public Mono<FileDto> getDocument(final DocumentDto documentDto) {
        return converterClient.getDocument(documentDto.getBucketName(), documentDto.getFileName())
                .flatMap(response -> checkResponseStatus(response,
                        String.format("Document %s successfully downloaded", documentDto.getFileName())))
                .map(response -> createDocumentResponse(documentDto, response));
    }

    public Mono<Void> saveUserDocument(final MultipartFile file, final DocumentDto documentDto) {
        return createBucket(documentDto.getBucketName())
                .then(converterClient.uploadFile(file, documentDto.getBucketName())
                        .flatMap(response -> checkResponseStatus(response,
                                String.format("Document %s successfully saved", documentDto.getFileName()))))
                .then();
    }

    public Mono<Void> deleteUserDocument(final DocumentDto documentDto) {
        return converterClient.deleteDocument(documentDto.getBucketName())
                .flatMap(response -> checkResponseStatus(response,
                        String.format("Document %s successfully deleted", documentDto.getFileName())))
                .then();
    }

    private <T> Mono<Response<T>> checkResponseStatus(final Response<T> response, final String successLog) {
        if (ResponseStatus.SUCCESS.equals(response.getResult().getStatus())) {
            log.info(successLog);
            return Mono.just(response);
        }
        return Mono.error(new TopPdfConverterException(response.getResult().getDescription()));
    }

    private FileDto createDocumentResponse(
            final DocumentDto documentDto,
            final Response<byte[]> response
    ) {
        return FileDto.builder()
                .fileName(documentDto.getFileName())
                .bucketName(documentDto.getBucketName())
                .fileContent(Base64.getEncoder().encodeToString(response.getData()))
                .build();
    }
}
