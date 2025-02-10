package ru.abdusamatov.librarywithsecurity.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.abdusamatov.librarywithsecurity.dto.FileDto;
import ru.abdusamatov.librarywithsecurity.dto.ReaderDto;
import ru.abdusamatov.librarywithsecurity.service.ReaderService;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReaderHandler {
    private final ReaderService readerService;
    private final DocumentHandler documentHandler;

    public Mono<List<ReaderDto>> getReaderList(final Integer page, final Integer size) {
        return Mono.fromCallable(() -> readerService.getReaderList(page, size))
                .subscribeOn(Schedulers.boundedElastic())
                .map(readerDtoList -> readerDtoList.isEmpty() ? Collections.emptyList() : readerDtoList);
    }


    public Mono<ReaderDto> getReaderById(final Long id) {
        return Mono.fromCallable(() -> readerService.getReaderById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess((readerDto) -> log.info("Find reader with ID: {}", readerDto.getId()));
    }

    public Mono<FileDto> getDocument(final long readerId) {
        return documentHandler.getDocument(readerId);
    }

    public Mono<ReaderDto> createReader(final MultipartFile file, final ReaderDto dto) {
        return Mono.fromCallable(() -> readerService.createReader(dto))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(readerDto -> documentHandler
                        .saveReaderDocument(file, readerDto.getId())
                        .thenReturn(readerDto));
    }

    public Mono<ReaderDto> updateReader(final ReaderDto dtoToBeUpdated) {
        return Mono.fromCallable(() -> readerService.updateReader(dtoToBeUpdated))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteReaderById(final Long readerId) {
        return documentHandler.deleteReaderDocument(readerId)
                .then(Mono.fromRunnable(() -> readerService.deleteReaderById(readerId))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }

}
