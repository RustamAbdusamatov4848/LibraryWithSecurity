package ru.abdusamatov.librarywithsecurity.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.abdusamatov.librarywithsecurity.dto.FileDto;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;
import ru.abdusamatov.librarywithsecurity.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReaderHandler {
    private final UserService userService;
    private final DocumentHandler documentHandler;

    public Mono<List<UserDto>> getUserList(final Integer page, final Integer size) {
        return Mono.fromCallable(() -> userService.getUserList(page, size))
                .subscribeOn(Schedulers.boundedElastic())
                .map(userDtoList -> userDtoList.isEmpty() ? Collections.emptyList() : userDtoList);
    }


    public Mono<UserDto> getUserById(final Long id) {
        return Mono.fromCallable(() -> userService.getUserById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess((userDto) -> log.info("Find user with ID: {}", userDto.getId()));
    }

    public Mono<FileDto> getDocument(final long userId) {
        return documentHandler.getDocument(userId);
    }

    public Mono<UserDto> createUser(final MultipartFile file, final UserDto dto) {
        return Mono.fromCallable(() -> userService.createUser(dto))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(userDto -> documentHandler
                        .saveUserDocument(file, userDto.getId())
                        .thenReturn(userDto));
    }

    public Mono<UserDto> updateUser(final UserDto dtoToBeUpdated) {
        return Mono.fromCallable(() -> userService.updateUser(dtoToBeUpdated))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteUserById(final Long userId) {
        return Mono.fromRunnable(() -> userService.deleteUserById(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .then(documentHandler.deleteUserDocument(userId));
    }
}
