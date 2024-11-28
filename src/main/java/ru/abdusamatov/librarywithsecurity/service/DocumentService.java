package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.exception.TopPdfConverterException;
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
        final var document = repository.findByOwnerId(userId);
        final var response =
                client.getDocument(document.getBucketName(), document.getFileName());

        if (response.getResult().getStatus().equals(ResponseStatus.SUCCESS)) {
            log.info("Document {} successfully found", document.getFileName());
        } else {
            throw new TopPdfConverterException(response.getResult().getDescription());
        }

        final var body = new LinkedMultiValueMap<String, Object>();

        body.add("bucketName", document.getBucketName());
        body.add("fileName", document.getFileName());
        body.add("fileContent", response);

        return body;
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

    private boolean isDocumentChanged(final long userId, final DocumentDto document) {
        return repository
                .findByOwnerId(userId)
                .equals(mapper.dtoToDocument(document));
    }

    private void executeWithStatusCheck(RunnableWithResponse action, String successLog) {
        final var response = action.execute();
        if (response.getResult().getStatus().equals(ResponseStatus.SUCCESS)) {
            log.info(successLog);
        } else {
            throw new TopPdfConverterException(response.getResult().getDescription());
        }
    }
}
