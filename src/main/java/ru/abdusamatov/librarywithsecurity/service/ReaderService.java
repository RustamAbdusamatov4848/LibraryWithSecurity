package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
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

    public UserDto getUserById(final Long id) {
        return userService.getUserById(id);
    }

    public MultiValueMap<String, Object> getDocument(final long userId) {
        return documentService.getDocument(userId);
    }

    public UserDto createUser(final MultipartFile file, final UserDto dto) {
        documentService.saveUserDocument(file, dto.getDocumentId());
        return userService.createUser(dto);
    }


    public UserDto updateUser(final UserDto dtoToBeUpdated) {
        documentService.updateDocumentIfNeeded(dtoToBeUpdated.getId(), dtoToBeUpdated.getDocumentId());
        return userService.updateUser(dtoToBeUpdated);
    }

    public void deleteUserById(final Long userId) {
        documentService.deleteUserDocument(userId);
        userService.deleteUserById(userId);
    }
}
