package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.abdusamatov.librarywithsecurity.dto.UserDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReaderService {
    private final UserService userService;
    private final DocumentService documentService;

    public List<UserDto> getUserList(final Integer page, final Integer size) {
        return userService.getUserList(page, size);
    }

    public Mono<UserDto> getUserById(final Long id) {
        return userService.getUserById(id);
    }

    public Mono<MultiValueMap<String, Object>> getDocument(final long userId) {
        return documentService.getDocument(userId);
    }

    public Mono<UserDto> createUser(final MultipartFile file, final UserDto dto) {
        return documentService
                .saveUserDocument(file, dto.getDocumentId())
                .then(userService.createUser(dto));
    }

    public Mono<UserDto> updateUser(final UserDto dtoToBeUpdated) {
        return userService.updateUser(dtoToBeUpdated);
    }

    public void deleteUserById(final Long userId) {
        documentService.deleteUserDocument(userId);
        userService.deleteUserById(userId);
    }
}
