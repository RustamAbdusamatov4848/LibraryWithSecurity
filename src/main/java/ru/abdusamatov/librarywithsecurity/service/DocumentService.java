package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.exception.TopPdfConverterException;
import ru.abdusamatov.librarywithsecurity.model.Document;
import ru.abdusamatov.librarywithsecurity.repository.DocumentRepository;
import ru.abdusamatov.librarywithsecurity.service.mapper.DocumentMapper;
import ru.abdusamatov.librarywithsecurity.util.ResponseStatus;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository repository;
    private final DocumentMapper mapper;
    private final TopPdfConverterClient client;

    public void createBucket(final String bucketName) {
        executeWithStatusCheck(
                () -> client.addBucket(bucketName),
                String.format("Bucket %s successfully created", bucketName));
    }

    public MultiValueMap<String, Object> getDocument(final long userId) {
        return repository.findByOwnerId(userId)
                .map(document -> {
                    var response = client.getDocument(document.getBucketName(), document.getFileName());
                    checkResponseStatus(response, document.getFileName());
                    return createDocumentResponse(document, response);
                })
                .orElseThrow(() -> new TopPdfConverterException("Document not found for user ID: " + userId));
    }

    public void saveUserDocument(final MultipartFile file, final DocumentDto document) {
        createBucket(document.getBucketName());
        executeWithStatusCheck(
                () -> client.uploadFile(file, document.getBucketName()),
                String.format("Document %s successfully saved", document.getFileName()));
    }

    public void updateDocumentIfNeeded(final long userId, final DocumentDto document) {
        if (isDocumentChanged(userId, document)) {
            executeWithStatusCheck(
                    () -> client.updateDocument(document.getBucketName(), document.getFileName()),
                    String.format("Document %s successfully updated", document.getFileName()));
        }
    }

    public void deleteUserDocument(final DocumentDto document) {
        executeWithStatusCheck(
                () -> client.deleteDocument(document.getBucketName()),
                String.format("Document %s successfully deleted", document.getFileName()));
    }

    public boolean isDocumentChanged(final long userId, final DocumentDto document) {
        return repository.findByOwnerId(userId)
                .map(existingDocument -> !existingDocument.equals(mapper.dtoToDocument(document)))
                .orElse(true);
    }

    private void executeWithStatusCheck(final RunnableWithResponse action, final String successLog) {
        final var response = action.execute();
        checkResponseStatus(response, successLog);
    }

    private void checkResponseStatus(final Response<?> response, final String successLog) {
        if (!ResponseStatus.SUCCESS.equals(response.getResult().getStatus())) {
            throw new TopPdfConverterException(response.getResult().getDescription());
        }
        log.info(successLog);
    }

    private MultiValueMap<String, Object> createDocumentResponse(
            final Document document,
            final Response<byte[]> response
    ) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("bucketName", document.getBucketName());
        body.add("fileName", document.getFileName());
        body.add("fileContent", response);
        return body;
    }
}
