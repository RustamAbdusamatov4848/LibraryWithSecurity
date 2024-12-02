package ru.abdusamatov.librarywithsecurity.service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.abdusamatov.librarywithsecurity.dto.DocumentDto;
import ru.abdusamatov.librarywithsecurity.model.Document;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentMapperTest {

    private final DocumentMapper mapper = new DocumentMapperImpl();

    @ParameterizedTest
    @MethodSource("shouldMapDocumentToDocumentDto")
    void shouldMapDocumentToDocumentDto(final Document document, final DocumentDto expected) {
        final var actual = mapper.documentToDto(document);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("shouldMapDocumentDtoToDocument")
    void shouldMapDocumentDtoToDocument(final DocumentDto dtoToBeMapped, final Document expected) {
        final var actual = mapper.dtoToDocument(dtoToBeMapped);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("owner")
                .isEqualTo(expected);
    }

    @Test
    void shouldReturnNull_whenDocumentIsNullInDocumentToDto() {
        final var documentDto = mapper.documentToDto(null);

        assertThat(documentDto)
                .isNull();
    }

    @Test
    void shouldReturnNull_whenDtoIsNullInDtoToDocument() {
        final var document = mapper.dtoToDocument(null);

        assertThat(document)
                .isNull();
    }

    public static Stream<Arguments> shouldMapDocumentToDocumentDto() {
        final var user = TestDataProvider.createUser().build();

        final var document = TestDataProvider
                .createDocument()
                .owner(user)
                .build();

        final var expected = TestDataProvider
                .createDocumentDto()
                .id(document.getId())
                .bucketName(document.getBucketName())
                .fileName(document.getFileName())
                .userId(user.getId())
                .build();

        return Stream.of(Arguments.arguments(document, expected));
    }

    public static Stream<Arguments> shouldMapDocumentDtoToDocument() {
        final var user = TestDataProvider.createUser().build();

        final var dtoToBeMapped = TestDataProvider
                .createDocumentDto()
                .build();

        final var expected = TestDataProvider
                .createDocument()
                .id(dtoToBeMapped.getId())
                .bucketName(dtoToBeMapped.getBucketName())
                .fileName(dtoToBeMapped.getFileName())
                .owner(user)
                .build();

        return Stream.of(Arguments.arguments(dtoToBeMapped, expected));
    }
}
