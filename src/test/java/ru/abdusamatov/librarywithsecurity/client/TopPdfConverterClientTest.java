package ru.abdusamatov.librarywithsecurity.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException.InternalServerError;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.dto.response.Result;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.support.WebClientTestBase;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

public class TopPdfConverterClientTest extends WebClientTestBase {
    public static final String BASE_PATH = "/api/v1/file-storage-management";
    public static final String BUCKET_NAME = "bucket-example";
    public static final String FILE_NAME = "passport.jpg";

    @Autowired
    private TopPdfConverterClient client;

    @Test
    void shouldCreateBucket_whenAddBucket() {
        stubFor(
                post(BASE_PATH + "/addBucket/" + BUCKET_NAME)
                        .willReturn(ok()));

        StepVerifier
                .create(client.addBucket(BUCKET_NAME))
                .verifyComplete();

        assertMethodAndPath(
                RequestMethod.POST,
                BASE_PATH + "/addBucket/" + BUCKET_NAME);
    }

    @Test
    void shouldReturnError_whenFailedToCreateBucket() {
        stubFor(
                post(BASE_PATH + "/addBucket/" + BUCKET_NAME)
                        .willReturn(serverError()));

        verifyError(client.addBucket(BUCKET_NAME));

        assertMethodAndPath(
                RequestMethod.POST,
                BASE_PATH + "/addBucket/" + BUCKET_NAME);
    }

    @Test
    void shouldReturnUserDocument_whenGetDocument() {
        final var document = TestDataProvider.getImageBytes(FILE_NAME);

        stubFor(
                get(urlPathEqualTo(BASE_PATH + "/file/download"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .withQueryParam("fileName", equalTo(FILE_NAME))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(getDocumentMockResponse(document))));

        StepVerifier
                .create(client.getDocument(BUCKET_NAME, FILE_NAME))
                .assertNext(bytes -> assertThat(bytes.getData())
                        .isEqualTo(document))
                .verifyComplete();

        assertMethodAndPath(
                RequestMethod.GET,
                BASE_PATH + "/file/download?bucketName=" + BUCKET_NAME + "&fileName=" + FILE_NAME);
    }

    @Test
    void shouldReturnError_whenErrorWhileDownloadingFile() {
        stubFor(
                get(urlPathEqualTo(BASE_PATH + "/file/download"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .withQueryParam("fileName", equalTo(FILE_NAME))
                        .willReturn(serverError()));

        verifyError(client.getDocument(BUCKET_NAME, FILE_NAME));

        assertMethodAndPath(
                RequestMethod.GET,
                BASE_PATH + "/file/download?bucketName=" + BUCKET_NAME + "&fileName=" + FILE_NAME);
    }

    @Test
    void shouldUploadDocument_whenUploadFile() {
        final var documentContent = TestDataProvider.getImageBytes(FILE_NAME);
        final var file = new MockMultipartFile(
                FILE_NAME,
                FILE_NAME,
                "application/octet-stream", documentContent);

        stubFor(
                post(urlPathEqualTo(BASE_PATH + "/upload"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .willReturn(ok()));

        StepVerifier
                .create(client.uploadFile(file, BUCKET_NAME))
                .verifyComplete();

        assertMethodAndPath(
                RequestMethod.POST,
                BASE_PATH + "/upload?bucketName=" + BUCKET_NAME);
    }

    @Test
    void shouldReturnError_whenErrorFileSaving() {
        final var documentContent = TestDataProvider.getImageBytes(FILE_NAME);
        final var file = new MockMultipartFile(
                FILE_NAME,
                FILE_NAME,
                "application/octet-stream", documentContent);

        stubFor(
                post(urlPathEqualTo(BASE_PATH + "/upload"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .willReturn(serverError()));

        verifyError(client.uploadFile(file, BUCKET_NAME));

        assertMethodAndPath(
                RequestMethod.POST,
                BASE_PATH + "/upload?bucketName=" + BUCKET_NAME);
    }

    @Test
    void shouldDeleteDocument_whenDeleteBucket() {
        stubFor(
                delete(urlPathEqualTo(BASE_PATH + "/bucket/delete"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .willReturn(ok()));

        StepVerifier
                .create(client.deleteDocument(BUCKET_NAME))
                .verifyComplete();

        assertMethodAndPath(
                RequestMethod.DELETE,
                BASE_PATH + "/bucket/delete?bucketName=" + BUCKET_NAME);
    }

    @Test
    void shouldReturnError_whenFailedToDeleteBucket() {
        stubFor(
                delete(urlPathEqualTo(BASE_PATH + "/bucket/delete"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .willReturn(serverError()));

        verifyError(client.deleteDocument(BUCKET_NAME));

        assertMethodAndPath(
                RequestMethod.DELETE,
                BASE_PATH + "/bucket/delete?bucketName=" + BUCKET_NAME);
    }

    @SneakyThrows
    private String getDocumentMockResponse(byte[] document) {
        ObjectMapper mapper = new ObjectMapper();
        final var response =
                Response.buildResponse(
                        Result.success(OK, "File " + FILE_NAME + " successfully loaded"),
                        document);
        return mapper.writeValueAsString(response);
    }

    private void verifyError(Mono<?> action) {
        StepVerifier
                .create(action)
                .verifyErrorSatisfies(ex -> assertThat(ex).isInstanceOf(InternalServerError.class));
    }

}
