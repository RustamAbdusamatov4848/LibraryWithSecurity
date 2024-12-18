package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
import ru.abdusamatov.librarywithsecurity.dto.FileDto;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.exception.TopPdfConverterException;
import ru.abdusamatov.librarywithsecurity.model.Document;
import ru.abdusamatov.librarywithsecurity.model.enums.ResponseStatus;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopPdfConverterClientService {
    private final TopPdfConverterClient client;

    public Mono<Void> createBucket(final String bucketName) {
        return client.addBucket(bucketName)
                .flatMap(response -> checkResponseStatus(response,
                        String.format("Bucket %s successfully created", bucketName)))
                .then();
    }

    public Mono<FileDto> getDocument(final Document document) {
        return client.getDocument(document.getBucketName(), document.getFileName())
                .flatMap(response -> checkResponseStatus(response,
                        String.format("Document %s successfully downloaded", document.getFileName())))
                .map(response -> createDocumentResponse(document, response));
    }

    public Mono<Void> saveUserDocument(final MultipartFile file, final Document document) {
        return createBucket(document.getBucketName())
                .then(client.uploadFile(file, document.getBucketName())
                        .flatMap(response -> checkResponseStatus(response,
                                String.format("Document %s successfully saved", document.getFileName()))))
                .then();
    }

    public Mono<Void> deleteUserDocument(final Document document) {
        return client.deleteDocument(document.getBucketName())
                .flatMap(response -> checkResponseStatus(response,
                        String.format("Document %s successfully deleted", document.getFileName())))
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
            final Document document,
            final Response<byte[]> response
    ) {
        return FileDto.builder()
                .fileName(document.getFileName())
                .bucketName(document.getBucketName())
                .fileContent(Base64.getEncoder().encodeToString(response.getData()))
                .build();
    }
}
