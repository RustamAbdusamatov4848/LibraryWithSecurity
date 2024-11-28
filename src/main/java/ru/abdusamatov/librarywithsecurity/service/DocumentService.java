package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
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

    //TODO: Добавить обработку ответа
    private void createBucketForUser(final String bucketName) {
        final var response = client.addBucket(bucketName);
        if (response.getResult().getStatus().equals(ResponseStatus.SUCCESS)) {
            log.info("Bucket {} created", bucketName);
        } else {

        }
    }

    //TODO: Добавить обработку ответа
    public void saveUserDocuments(final MultipartFile file, final String bucketName) {
        createBucketForUser(bucketName);
        final var response = client.uploadFile(file, bucketName);
    }

    //TODO: Добавить обработку ответа
    public void updateDocumentsIfNeeded(final long userId, final DocumentDto document) {
        final Response<Void> response;
        if (isDocumentChanged(userId, document)) {
            response = client.updateDocument(document.getBucketName(), document.getFileName());
        }
    }

    //TODO: Добавить обработку ответа
    public void deleteUserDocuments(final DocumentDto document) {
        final var response = client.deleteDocument(document.getBucketName());
    }

    private boolean isDocumentChanged(final long userId, final DocumentDto document) {
        final var foundDoc = repository.findByOwnerId(userId);
        return foundDoc.equals(mapper.dtoToDocument(document));
    }
}
