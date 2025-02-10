package ru.abdusamatov.librarywithsecurity.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import ru.abdusamatov.librarywithsecurity.dto.FileDto;
import ru.abdusamatov.librarywithsecurity.dto.ReaderDto;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.dto.response.Result;
import ru.abdusamatov.librarywithsecurity.support.TestAssertUtil;
import ru.abdusamatov.librarywithsecurity.support.TestBase;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.util.ParameterizedTypeReferenceTestUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class ReaderControllerTest extends TestBase {

    public static final String BASE_URL = "readers";

    @Test
    void shouldGetAllReaders() {
        final var readerListSize = 10;
        final var readerList = TestDataProvider.createListReader(readerListSize);
        readerRepository.saveAll(readerList);

        final var response = executeGetAllReaders();

        TestAssertUtil
                .assertSuccess(OK, "List of readers", response);
        assertThat(response.getData())
                .asList()
                .isNotNull()
                .isNotEmpty()
                .hasSize(readerListSize);
    }

    @Test
    void shouldReturnEmptyList_whenReaderAreAbsent() {
        final var response = executeGetAllReaders();

        TestAssertUtil
                .assertSuccess(OK, "List of readers", response);
        assertThat(response.getData())
                .isEmpty();
    }

    @Test
    void shouldReturnReader_whenExistingReaderIdProvided() {
        final var id = readerRepository
                .save(TestDataProvider.createReader())
                .getId();

        final var response = executeGetReaderById(OK, id, ReaderDto.class);

        TestAssertUtil
                .assertSuccess(OK, "Reader successfully found", response);
        assertThat(response.getData().getId())
                .isEqualTo(id);
    }

    @Test
    void shouldReturnNotFound_whenNonExistingReaderIdProvided() {
        final var id = 10000L;

        final var response = executeGetReaderById(NOT_FOUND, id, Void.class);

        TestAssertUtil.assertEntityNotFound(response);
    }

    @Test
    void shouldReturnDocument_whenReaderExist() {
        final var savedReader = readerRepository.save(TestDataProvider.createReader());
        final var readerDocument = savedReader.getDocument();

        when(topPdfConverterClient.getDocument(readerDocument.getBucketName(), readerDocument.getFileName()))
                .thenReturn(getMonoResponseByteArray(readerDocument.getFileName()));

        final var response = executeGetReaderDocument(savedReader.getId());

        TestAssertUtil
                .assertSuccess(OK, "Reader document successfully found", response);
        verify(topPdfConverterClient, times(1))
                .getDocument(readerDocument.getBucketName(), readerDocument.getFileName());
        verifyNoMoreInteractions(topPdfConverterClient);
    }

    @Test
    void shouldCreateReader_whenValidDataProvided() {
        final var validReaderDto = TestDataProvider
                .createReaderDto()
                .build();

        when(topPdfConverterClient.addBucket(validReaderDto.getDocumentDto().getBucketName()))
                .thenReturn(getMonoResponseVoid(
                        "Bucket " + validReaderDto.getDocumentDto().getBucketName() + " successfully created"));
        when(topPdfConverterClient.uploadFile(
                any(MultipartFile.class),
                anyString()))
                .thenReturn(getMonoResponseVoid(
                        "File " + validReaderDto.getDocumentDto().getFileName() + " successfully uploaded"));

        final var response = executeCreateReader(
                OK,
                validReaderDto,
                ReaderDto.class,
                TestDataProvider.getMultipartFile());

        TestAssertUtil
                .assertSuccess(CREATED, "Reader successfully saved", response);
        assertThat(response.getData().getId())
                .isNotNull();
        assertThat(response.getData().getBooks())
                .isNull();
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields(
                        "id",
                        "books",
                        "documentDto.id",
                        "documentDto.readerId")
                .isEqualTo(validReaderDto);
        verify(topPdfConverterClient)
                .addBucket(validReaderDto.getDocumentDto().getBucketName());
        verify(topPdfConverterClient)
                .uploadFile(any(MultipartFile.class), anyString());
        verifyNoMoreInteractions(topPdfConverterClient);
    }

    @Test
    void shouldReturnBadRequest_whenReaderWithInvalidFields() {
        final var invalidReaderDto = TestDataProvider
                .createReaderDtoWithInvalidFields()
                .build();

        final var response = executeCreateReader(
                BAD_REQUEST,
                invalidReaderDto,
                Void.class,
                TestDataProvider.getMultipartFile());

        TestAssertUtil.assertFieldErrorForEntity(response);
    }

    @Test
    void shouldUpdateReader_whenValidReaderDtoProvided() {
        final var readerToBeUpdated = readerMapper
                .readerToDto(readerRepository.save(TestDataProvider.createReader()));

        final var updateReaderDto = TestDataProvider
                .updateReaderDto(readerToBeUpdated)
                .build();

        final var response = executeUpdateReader(OK, updateReaderDto, ReaderDto.class);

        TestAssertUtil
                .assertSuccess(OK, "Reader successfully updated", response);
        assertThat(response.getData())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateReaderDto);
    }

    @Test
    void shouldReturnNotFound_whenReaderToUpdateDoesNotExist() {
        final var notExistingId = 10000L;
        final var updateReaderDto = TestDataProvider
                .updateReaderDto(TestDataProvider.createReaderDto().build())
                .id(notExistingId)
                .build();

        final var response = executeUpdateReader(NOT_FOUND, updateReaderDto, Void.class);

        TestAssertUtil.assertEntityNotFound(response);
    }

    @Test
    void shouldReturnBadRequest_whenUpdateReaderWithInvalidFields() {
        final var updateReaderDto = TestDataProvider
                .updateReaderDtoWithInvalidFields(TestDataProvider.createReaderDto().build())
                .build();

        final var response = executeUpdateReader(BAD_REQUEST, updateReaderDto, Void.class);

        TestAssertUtil.assertFieldErrorForEntity(response);
    }

    @Test
    void shouldReturnNoContent_whenReaderDeletedSuccessfully() {
        final var savedReader = readerRepository.save(TestDataProvider.createReader());
        final var bucketName = savedReader.getDocument().getBucketName();

        when(topPdfConverterClient.deleteDocument(bucketName))
                .thenReturn(getMonoResponseVoid("Bucket " + bucketName + " deleted"));

        final var response = executeDeleteReaderById(OK, savedReader.getId());

        TestAssertUtil
                .assertSuccess(NO_CONTENT, "Successfully deleted", response);
        verify(topPdfConverterClient, times(1))
                .deleteDocument(bucketName);
        verifyNoMoreInteractions(topPdfConverterClient);
    }

    @Test
    void shouldReturnNotFound_whenBookToDeleteDoesNotExist() {
        final var notExistingId = 10000L;

        final var response = executeDeleteReaderById(NOT_FOUND, notExistingId);

        TestAssertUtil.assertEntityNotFound(response);
    }

    private Response<List<ReaderDto>> executeGetAllReaders() {
        final var response = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .queryParam("page", 0)
                        .queryParam("size", 20)
                        .build())
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectBody(ParameterizedTypeReferenceTestUtil.getListResponseReference(ReaderDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private <T> Response<T> executeGetReaderById(
            final HttpStatus status,
            final long id,
            final Class<T> responseType
    ) {
        final var response = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference(responseType))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private Response<FileDto> executeGetReaderDocument(final long id) {
        final var response = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id), "document")
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference(FileDto.class))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    @SneakyThrows
    private <T> Response<T> executeCreateReader(
            final HttpStatus status,
            final ReaderDto readerDto,
            final Class<T> responseType,
            final MultipartFile file
    ) {
        final var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", file.getResource())
                .contentType(MediaType.IMAGE_JPEG);
        multipartBodyBuilder.part("readerDto", toJson(readerDto))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference(responseType))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private <T> Response<T> executeUpdateReader(
            final HttpStatus status,
            final ReaderDto readerDto,
            final Class<T> responseType
    ) {
        final var response = webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(readerDto)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference(responseType))
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private Response<Void> executeDeleteReaderById(
            final HttpStatus status,
            final long id
    ) {
        final var response = webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(BASE_URL, String.valueOf(id))
                        .build()
                )
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(ParameterizedTypeReferenceTestUtil.getResponseReference())
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .isNotNull();

        return response;
    }

    private Mono<Response<Void>> getMonoResponseVoid(String description) {
        return Mono.just(Response.buildResponse(
                Result.success(NO_CONTENT, description)));
    }

    private Mono<Response<byte[]>> getMonoResponseByteArray(String fileName) {
        return Mono.just(Response.buildResponse(
                Result.success(OK, "File " + fileName + " successfully loaded"),
                TestDataProvider.getImageBytes("passport.jpg")));
    }

    private String toJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    @Override
    protected void clearDatabase() {
        readerRepository.deleteAll();
    }
}
