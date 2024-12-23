package ru.abdusamatov.librarywithsecurity.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.abdusamatov.librarywithsecurity.dto.FileDto;
import ru.abdusamatov.librarywithsecurity.service.DocumentService;
import ru.abdusamatov.librarywithsecurity.service.TopPdfConverterClientService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentHandler {
    private final TopPdfConverterClientService clientService;
    private final DocumentService documentService;

    public Mono<Void> saveUserDocument(MultipartFile file, Long id) {
        return Mono.fromCallable(() -> documentService.findDocument(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(documentDto -> clientService.saveUserDocument(file, documentDto));
    }

    public Mono<FileDto> getDocument(long userId) {
        return Mono.fromCallable(() -> documentService.findDocument(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(clientService::getDocument);
    }

    public Mono<Void> deleteUserDocument(Long userId) {
        return Mono.fromCallable(() -> documentService.findDocument(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(clientService::deleteUserDocument);
    }
}
