package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.abdusamatov.librarywithsecurity.dto.FileDto;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.exception.TopPdfConverterException;
import ru.abdusamatov.librarywithsecurity.repository.DocumentRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository repository;
    private final TopPdfConverterClientService clientService;

    @Transactional(readOnly = true)
    public Mono<Void> saveUserDocument(final MultipartFile file, final Long documentId) {
        return Mono.fromCallable(() -> repository.getReferenceById(documentId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(document -> clientService.saveUserDocument(file, document))
                .doOnSuccess(document -> log.info("Successfully saved document with ID {}", documentId));
    }

    @Transactional
    public Mono<FileDto> getDocument(final long userId) {
        return Mono.fromCallable(() -> repository.findByOwnerId(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(document -> document
                        .map(clientService::getDocument)
                        .orElseGet(() -> Mono.error(new TopPdfConverterException("Document not found for user ID: " + userId)))
                );
    }

    @Transactional
    public Mono<Void> deleteUserDocument(final long userId) {
        return Mono.fromCallable(() -> repository.findByOwnerId(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalDocument -> optionalDocument
                        .map(document -> clientService.deleteUserDocument(document)
                                .then(Mono.empty())).orElseGet(() -> Mono.error(new ResourceNotFoundException("Document", "user ID", userId))))
                .doOnSuccess(result -> log.info("Successfully deleted document for user ID: {}", userId))
                .then();
    }

}
