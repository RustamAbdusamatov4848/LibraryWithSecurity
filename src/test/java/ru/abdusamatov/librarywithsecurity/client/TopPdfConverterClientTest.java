package ru.abdusamatov.librarywithsecurity.client;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import reactor.test.StepVerifier;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.support.WebClientTestBase;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

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

        StepVerifier
                .create(client.addBucket(BUCKET_NAME))
                .verifyErrorSatisfies(ex ->
                        assertThat(ex)
                                .isInstanceOf(InternalServerError.class)
                                .hasMessage("Failed to create Bucket"));

        assertMethodAndPath(
                RequestMethod.POST,
                BASE_PATH + "/addBucket/" + BUCKET_NAME);
    }

    @Test
    void shouldReturnError_whenBucketIsAlreadyExist() {
        stubFor(
                post(BASE_PATH + "/addBucket/" + BUCKET_NAME)
                        .willReturn(serverError()));

        StepVerifier
                .create(client.addBucket(BUCKET_NAME))
                .verifyErrorSatisfies(ex ->
                        assertThat(ex)
                                .isInstanceOf(InternalServerError.class)
                                .hasMessage("Bucket with that name already exists"));

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
                        .willReturn(ok()));

        StepVerifier
                .create(client.getDocument(BUCKET_NAME, FILE_NAME))
                .assertNext(bytes -> assertThat(bytes.getData()).isEqualTo(document))
                .verifyComplete();

        assertMethodAndPath(
                RequestMethod.GET,
                BASE_PATH + "/file/download?bucketName=" + BUCKET_NAME + "&fileName=" + FILE_NAME);
    }

    @Test
    void shouldReturnNotFound_whenFileNoExist() {
        stubFor(
                get(urlPathEqualTo(BASE_PATH + "/file/download"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .withQueryParam("fileName", equalTo(FILE_NAME))
                        .willReturn(serverError()));

        StepVerifier
                .create(client.getDocument(BUCKET_NAME, FILE_NAME))
                .verifyErrorSatisfies(ex ->
                        assertThat(ex)
                                .isInstanceOf(InternalServerError.class)
                                .hasMessage("The file with name " + FILE_NAME + " inside " + BUCKET_NAME + " does not exist"));

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

        StepVerifier
                .create(client.getDocument(BUCKET_NAME, FILE_NAME))
                .verifyErrorSatisfies(ex ->
                        assertThat(ex)
                                .isInstanceOf(InternalServerError.class)
                                .hasMessage("Error during file saving"));

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
                post(BASE_PATH + "/upload")
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
                post(BASE_PATH + "/upload")
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .willReturn(serverError()));

        StepVerifier
                .create(client.uploadFile(file, BUCKET_NAME))
                .verifyErrorSatisfies(ex ->
                        assertThat(ex)
                                .isInstanceOf(InternalServerError.class)
                                .hasMessage("Error during file saving"));

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
    void shouldReturnError_whenFailedToVerifyBucket() {
        stubFor(
                delete(urlPathEqualTo(BASE_PATH + "/bucket/delete"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .willReturn(ok()));

        StepVerifier
                .create(client.deleteDocument(BUCKET_NAME))
                .verifyErrorSatisfies(ex ->
                        assertThat(ex)
                                .isInstanceOf(InternalServerError.class)
                                .hasMessage("Failed to verify the existence of bucket"));

        assertMethodAndPath(
                RequestMethod.DELETE,
                BASE_PATH + "/bucket/delete?bucketName=" + BUCKET_NAME);
    }

    @Test
    void shouldReturnError_whenBucketNoFound() {
        stubFor(
                delete(urlPathEqualTo(BASE_PATH + "/bucket/delete"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .willReturn(ok()));

        StepVerifier
                .create(client.deleteDocument(BUCKET_NAME))
                .verifyErrorSatisfies(ex ->
                        assertThat(ex)
                                .isInstanceOf(InternalServerError.class)
                                .hasMessage("Bucket with name " + BUCKET_NAME + " not found"));

        assertMethodAndPath(
                RequestMethod.DELETE,
                BASE_PATH + "/bucket/delete?bucketName=" + BUCKET_NAME);
    }

    @Test
    void shouldReturnError_whenFailedToDeleteBucket() {
        stubFor(
                delete(urlPathEqualTo(BASE_PATH + "/bucket/delete"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .willReturn(ok()));

        StepVerifier
                .create(client.deleteDocument(BUCKET_NAME))
                .verifyErrorSatisfies(ex ->
                        assertThat(ex)
                                .isInstanceOf(InternalServerError.class)
                                .hasMessage("Failed to delete bucket " + BUCKET_NAME));

        assertMethodAndPath(
                RequestMethod.DELETE,
                BASE_PATH + "/bucket/delete?bucketName=" + BUCKET_NAME);
    }
}
