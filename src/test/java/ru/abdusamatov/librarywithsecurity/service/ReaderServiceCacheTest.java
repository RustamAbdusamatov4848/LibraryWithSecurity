package ru.abdusamatov.librarywithsecurity.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.cache.Cache;
import ru.abdusamatov.librarywithsecurity.dto.ReaderDto;
import ru.abdusamatov.librarywithsecurity.model.Reader;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ReaderServiceCacheTest extends TestBase {

    private static final String READER_CACHE = "reader";

    @Override
    protected void clearDatabase() {
        spyReaderRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(spyReaderRepository);
    }

    @ParameterizedTest
    @MethodSource("createReader")
    void shouldCallRepositoryOnce_whenGetReaderById(final ReaderDto dtoToSaved) {
        final var savedReader = readerService.createReader(dtoToSaved);
        assertReaderNotInCache(savedReader.getId());

        final var retrieveReader = readerService
                .getReaderById(savedReader.getId());
        assertReaderInCache(retrieveReader);

        readerService.getReaderById(savedReader.getId());

        verify(spyReaderRepository)
                .save(any(Reader.class));
        verify(spyReaderRepository)
                .findById(savedReader.getId());
    }

    @ParameterizedTest
    @MethodSource("createReader")
    void shouldUpdateCacheReader_whenUpdateReader(final ReaderDto dtoToSaved) {
        final var savedReader = addSavedEntityToCache(dtoToSaved);

        final var updatedReader = readerService
                .updateReader(TestDataProvider
                        .updateReaderDto(savedReader)
                        .build());

        assertReaderInCache(updatedReader);
        verify(spyReaderRepository, times(2))
                .save(any(Reader.class));
        verify(spyReaderRepository)
                .findById(updatedReader.getId());
    }

    @ParameterizedTest
    @MethodSource("createReader")
    void shouldDeleteReaderFromCache_whenDeleteReader(final ReaderDto dtoToSaved) {
        final var savedReader = addSavedEntityToCache(dtoToSaved);

        readerService.deleteReaderById(savedReader.getId());

        assertReaderNotInCache(savedReader.getId());
        verify(spyReaderRepository)
                .save(any(Reader.class));
        verify(spyReaderRepository)
                .findById(savedReader.getId());
        verify(spyReaderRepository)
                .delete(any(Reader.class));
    }

    private void assertReaderInCache(final ReaderDto expectedReader) {
        final var cache = assertCacheNotNull();

        assertThat(cache.get(expectedReader.getId(), ReaderDto.class))
                .isNotNull()
                .extracting(ReaderDto::getId)
                .isEqualTo(expectedReader.getId());
    }

    private void assertReaderNotInCache(final Long id) {
        final var cache = assertCacheNotNull();

        assertThat(cache.get(id))
                .isNull();
    }

    private Cache assertCacheNotNull() {
        final var cache = cacheManager.getCache(READER_CACHE);

        assertNotNull(cache);

        return cache;
    }

    private static Stream<Arguments> createReader() {
        final var reader = TestDataProvider
                .createReaderDto()
                .build();

        return Stream.of(Arguments.arguments(reader));
    }

    private ReaderDto addSavedEntityToCache(final ReaderDto dtoToSaved) {
        final var savedReader = readerService.createReader(dtoToSaved);

        cacheManager.getCache(READER_CACHE).put(savedReader.getId(), savedReader);

        return savedReader;
    }
}
