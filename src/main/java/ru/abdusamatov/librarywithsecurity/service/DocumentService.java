package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.repository.DocumentRepository;
import ru.abdusamatov.librarywithsecurity.service.mapper.DocumentMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    @Transactional
    public DocumentDto findDocument(final long readerId) {
        return documentRepository.findByOwnerId(readerId)
                .map(documentMapper::documentToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "reader ID", readerId));
    }
}
