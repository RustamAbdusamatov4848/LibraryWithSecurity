package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.ReaderDto;
import ru.abdusamatov.librarywithsecurity.exception.ResourceNotFoundException;
import ru.abdusamatov.librarywithsecurity.repository.ReaderRepository;
import ru.abdusamatov.librarywithsecurity.service.mapper.DocumentMapper;
import ru.abdusamatov.librarywithsecurity.service.mapper.ReaderMapper;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "reader")
public class ReaderService {
    private final ReaderRepository readerRepository;
    private final ReaderMapper readerMapper;
    private final DocumentMapper documentMapper;

    @Transactional(readOnly = true)
    public List<ReaderDto> getReaderList(final Integer page, final Integer size) {
        return readerRepository
                .findAll(PageRequest.of(page, size, Sort.by("id").ascending()))
                .getContent()
                .stream()
                .map(readerMapper::readerToDto)
                .toList();
    }

    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public ReaderDto getReaderById(final Long id) {
        return readerRepository.findById(id)
                .map(readerMapper::readerToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reader", "ID", id));
    }

    @Transactional
    public ReaderDto createReader(final ReaderDto dto) {
        final var document = documentMapper.dtoToDocument(dto.getDocumentDto());
        final var reader = readerMapper.dtoToReader(dto);
        reader.setDocument(document);

        final var createdReader = readerRepository.save(reader);

        log.info("Saving new Reader with ID: {}", createdReader.getId());
        return readerMapper.readerToDto(createdReader);
    }

    @CachePut(key = "#dtoToBeUpdated.id")
    @Transactional
    public ReaderDto updateReader(final ReaderDto dtoToBeUpdated) {
        final var updatedReader = readerRepository.findById(dtoToBeUpdated.getId())
                .map(reader -> {
                    final var updatedReaderEntity = readerMapper.updateReaderFromDto(dtoToBeUpdated, reader);

                    return readerRepository.save(updatedReaderEntity);
                })
                .map(readerMapper::readerToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reader", "ID", dtoToBeUpdated.getId()));

        log.info("Updated reader with ID: {}", dtoToBeUpdated.getId());
        return updatedReader;
    }

    @CacheEvict(key = "#id")
    @Transactional
    public void deleteReaderById(final Long id) {
        final var reader = readerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reader", "ID", id));

        readerRepository.delete(reader);
        log.info("Deleted reader with ID: {}", id);
    }
}
