package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.exception.TopPdfConverterException;
import ru.abdusamatov.librarywithsecurity.repository.DocumentRepository;
import ru.abdusamatov.librarywithsecurity.service.mapper.DocumentMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository repository;
    private final DocumentMapper mapper;
    private final TopPdfConverterClientService clientService;

    public void saveUserDocument(final MultipartFile file, final Long documentId) {
        clientService.saveUserDocument(file, repository.getReferenceById(documentId));
    }

    public MultiValueMap<String, Object> getDocument(final long userId) {
        return repository.findByOwnerId(userId)
                .map(clientService::getDocument)
                .orElseThrow(() -> new TopPdfConverterException("Document not found for user ID: " + userId));
    }

    public void updateDocumentIfNeeded(final long userId, final Long documentId) {
        final var document = mapper.documentToDto(repository.getReferenceById(documentId));

        if (!isDocumentChanged(userId, document)) {
            clientService.updateDocumentIfNeeded(document);
        }
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

    private boolean isDocumentChanged(long userId, DocumentDto document) {
        return repository.findByOwnerId(userId)
                .map(existingDocument -> !existingDocument.equals(mapper.dtoToDocument(document)))
                .orElse(true);
    }
}
