package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.abdusamatov.librarywithsecurity.dto.FileDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReaderService {
    private final UserService userService;
    private final DocumentService documentService;

    public Mono<List<UserDto>> getUserList(final Integer page, final Integer size) {
        return Mono.fromCallable(() -> userService.getUserList(page, size))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(list -> {
                    if (!list.isEmpty()) {
                        return Mono.just(list);
                    } else {
                        return Mono.just(Collections.emptyList());
                    }
                });
    }

    public Mono<UserDto> getUserById(final Long id) {
        return Mono.fromCallable(() -> userService.getUserById(id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<FileDto> getDocument(final long userId) {
        return Mono.fromCallable(() -> documentService.getDocument(userId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UserDto> createUser(final MultipartFile file, final UserDto dto) {
        return Mono.fromCallable(() -> userService.createUser(dto))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(userDto -> documentService
                        .saveUserDocument(file, userDto.getDocumentDto().getId())
                        .thenReturn(userDto));
    }

    public Mono<UserDto> updateUser(final UserDto dtoToBeUpdated) {
        return Mono.fromCallable(() -> userService.updateUser(dtoToBeUpdated))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteUserById(final Long userId) {
        return documentService
                .deleteUserDocument(userId)
                .then(Mono.fromRunnable(() -> userService.deleteUserById(userId))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }
}
