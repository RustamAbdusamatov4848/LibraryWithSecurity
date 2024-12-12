package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
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

    public void createBucket(final String bucketName) {
        executeWithStatusCheck(
                () -> client.addBucket(bucketName),
                String.format("Bucket %s successfully created", bucketName));
    }

    public Mono<MultiValueMap<String, Object>> getDocument(final Document document) {
        return client
                .getDocument(document.getBucketName(), document.getFileName())
                .filter(response -> checkResponseStatus(response, document.getFileName()))
                .map(response -> createDocumentResponse(document, response));
    }

    public void saveUserDocument(final MultipartFile file, final Document document) {
        createBucket(document.getBucketName());

        executeWithStatusCheck(
                () -> client.uploadFile(file, document.getBucketName()),
                String.format("Document %s successfully saved", document.getFileName()));
    }

    public void deleteUserDocument(final Document document) {
        executeWithStatusCheck(
                () -> client.deleteDocument(document.getBucketName()),
                String.format("Document %s successfully deleted", document.getFileName()));
    }

    private void executeWithStatusCheck(final RunnableWithResponse action, final String successLog) {
        final var response = action.execute();

        checkResponseStatus(response, successLog);
    }

    private boolean checkResponseStatus(final Response<?> response, final String successLog) {
        if (ResponseStatus.SUCCESS.equals(response.getResult().getStatus())) {
            log.info(successLog);
            return true;
        }
        throw new TopPdfConverterException(response.getResult().getDescription());
    }

    private MultiValueMap<String, Object> createDocumentResponse(
            final Document document,
            final Response<byte[]> response
    ) {
        final var body = new LinkedMultiValueMap<String, Object>();

        body.add("bucketName", document.getBucketName());
        body.add("fileName", document.getFileName());
        body.add("fileContent", Base64.getEncoder().encodeToString(response.getData()));

        return body;
    }
}
