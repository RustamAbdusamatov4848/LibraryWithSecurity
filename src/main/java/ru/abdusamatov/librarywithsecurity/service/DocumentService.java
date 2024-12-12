package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.exception.TopPdfConverterException;
import ru.abdusamatov.librarywithsecurity.repository.DocumentRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository repository;
    private final TopPdfConverterClientService clientService;

    public Mono<Void> saveUserDocument(final MultipartFile file, final Long documentId) {
        return Mono.fromCallable(() -> repository.getReferenceById(documentId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(document -> clientService.saveUserDocument(file, document));
    }

    public Mono<MultiValueMap<String, Object>> getDocument(final long userId) {
        return Mono.fromCallable(() -> repository.findByOwnerId(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(document -> document
                        .map(clientService::getDocument)
                        .orElseGet(() -> Mono.error(new TopPdfConverterException("Document not found for user ID: " + userId)))
                );
    }

    public void deleteUserDocument(final long userId) {
        repository.findByOwnerId(userId)
                .ifPresentOrElse(
                        clientService::deleteUserDocument,
                        () -> {
                            throw new ResourceNotFoundException("Document", "user ID", userId);
                        }
                );
    }
}
