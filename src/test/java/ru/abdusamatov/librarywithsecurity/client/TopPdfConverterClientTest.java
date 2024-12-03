package ru.abdusamatov.librarywithsecurity.client;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.support.AssertTestStatusUtil;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.support.WebClientTestBase;

import java.util.Base64;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class TopPdfConverterClientTest extends WebClientTestBase {
    public static final String BUCKET_NAME = "bucket-example";
    public static final String FILE_NAME = "passport.jpg";

    @Autowired
    private TopPdfConverterClient client;

    @Test
    void shouldCreateBucket_whenAddBucket() {
        final var response = executeCreateBucket(
                HttpStatus.CREATED,
                "Bucket" + BUCKET_NAME + " successfully created",
                201);

        AssertTestStatusUtil
                .assertSuccess(HttpStatus.CREATED, "Bucket successfully created", response);
        assertMethodAndPath(RequestMethod.POST, "/addBucket/" + BUCKET_NAME);
    }

    @Test
    void shouldReturnError_whenFailedToCreateBucket() {
        final var response = executeCreateBucket(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create Bucket",
                500);

        AssertTestStatusUtil
                .assertError(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create Bucket", response);
        assertMethodAndPath(RequestMethod.POST, "/addBucket/" + BUCKET_NAME);
    }

    @Test
    void shouldReturnError_whenBucketIsAlreadyExist() {
        final var response = executeCreateBucket(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Bucket with that name already exists",
                500);

        AssertTestStatusUtil
                .assertError(HttpStatus.INTERNAL_SERVER_ERROR, "Bucket with that name already exists", response);
        assertMethodAndPath(RequestMethod.POST, "/addBucket/" + BUCKET_NAME);
    }

    @Test
    void shouldReturnUserDocument_whenGetDocument() {
        final var document = TestDataProvider.getImageBytes(FILE_NAME);

        stubFor(
                get(urlPathEqualTo("/file/download"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .withQueryParam("fileName", equalTo(FILE_NAME))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(String.format("""
                                        {
                                          "result": {
                                            "httpStatusCode": "%s",
                                            "status": "SUCCESS",
                                            "description": "%s"
                                          },
                                          "data": "%s"
                                        }
                                        """, "OK", "File + " + FILE_NAME + " successfully loaded", Base64.getEncoder().encodeToString(document)))
                        ));

        final var response = client.getDocument(BUCKET_NAME, FILE_NAME);

        AssertTestStatusUtil
                .assertSuccess(
                        HttpStatus.OK,
                        "File " + FILE_NAME + " successfully loaded",
                        response);
        assertMethodAndPath(RequestMethod.GET, "/file/download");
    }

    @Test
    void shouldReturnNotFound_whenFileNoExist() {
        final var response = executeGetDocumentWithError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "The file with name " + FILE_NAME + " inside " + BUCKET_NAME + " already exists",
                500);

        AssertTestStatusUtil
                .assertError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "The file with name " + FILE_NAME + " inside " + BUCKET_NAME + " does not exist",
                        response);
        assertMethodAndPath(RequestMethod.GET, "/file/download");
    }

    @Test
    void shouldReturnError_whenErrorWhileDownloadingFile() {
        final var response = executeGetDocumentWithError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error during file saving",
                500);

        AssertTestStatusUtil
                .assertError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error during file saving",
                        response);
        assertMethodAndPath(RequestMethod.GET, "/file/download");
    }

    @Test
    void shouldUploadDocument_whenUploadFile() {
        final var documentContent = TestDataProvider.getImageBytes(FILE_NAME);
        final var file = new MockMultipartFile(
                FILE_NAME,
                FILE_NAME,
                "application/octet-stream", documentContent);

        final var response = executeUploadFile(
                file,
                HttpStatus.OK,
                "File" + FILE_NAME + " successfully uploaded",
                200);

        AssertTestStatusUtil
                .assertSuccess(
                        HttpStatus.OK,
                        "File " + file.getOriginalFilename() + " successfully uploaded",
                        response);
        assertMethodAndPath(RequestMethod.POST, "/upload");
    }

    @Test
    void shouldReturnError_whenErrorFileSaving() {
        final var documentContent = TestDataProvider.getImageBytes(FILE_NAME);
        final var file = new MockMultipartFile(
                FILE_NAME,
                FILE_NAME,
                "application/octet-stream", documentContent);

        final var response = executeUploadFile(
                file,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error during file saving",
                500);

        AssertTestStatusUtil
                .assertError(HttpStatus.INTERNAL_SERVER_ERROR, "Error during file saving", response);
        assertMethodAndPath(RequestMethod.POST, "/upload");
    }

    @Test
    void shouldDeleteDocument_whenDeleteBucket() {
        final var response = executeDeleteBucket(
                HttpStatus.NO_CONTENT,
                "Bucket " + BUCKET_NAME + " deleted",
                204);

        AssertTestStatusUtil
                .assertSuccess(HttpStatus.NO_CONTENT, "Bucket " + BUCKET_NAME + " deleted", response);
        assertMethodAndPath(RequestMethod.DELETE, "/bucket/delete");
    }

    @Test
    void shouldReturnError_whenFailedToVerifyBucket() {
        final var response = executeDeleteBucket(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to verify the existence of bucket",
                500);

        AssertTestStatusUtil
                .assertError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to verify the existence of bucket",
                        response);
        assertMethodAndPath(RequestMethod.DELETE, "/bucket/delete");
    }

    @Test
    void shouldReturnError_whenBucketNoFound() {
        final var response = executeDeleteBucket(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Bucket with name " + BUCKET_NAME + " not found",
                500);

        AssertTestStatusUtil
                .assertError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Bucket with name " + BUCKET_NAME + " not found",
                        response);
        assertMethodAndPath(RequestMethod.DELETE, "/bucket/delete");
    }

    @Test
    void shouldReturnError_whenFailedToDeleteBucket() {
        final var response = executeDeleteBucket(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to delete bucket " + BUCKET_NAME,
                500);

        AssertTestStatusUtil
                .assertError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to delete bucket " + BUCKET_NAME,
                        response);
        assertMethodAndPath(RequestMethod.DELETE, "/bucket/delete");
    }

    private Response<Void> executeCreateBucket(final HttpStatus statusCode, final String description, final int code) {
        stubFor(
                post(urlPathEqualTo("/addBucket/" + BUCKET_NAME))
                        .willReturn(getJsonSuccess(
                                statusCode,
                                description,
                                code
                        )));

        return client.addBucket(BUCKET_NAME);
    }

    private Response<byte[]> executeGetDocumentWithError(final HttpStatus statusCode, String description, int code) {
        stubFor(
                get(urlPathEqualTo("/file/download"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .withQueryParam("fileName", equalTo(FILE_NAME))
                        .willReturn(
                                getJsonError(
                                        statusCode,
                                        description,
                                        code
                                )));

        return client.getDocument(BUCKET_NAME, FILE_NAME);
    }

    private Response<Void> executeUploadFile(
            final MockMultipartFile file,
            final HttpStatus statusCode,
            final String description,
            final int code) {
        stubFor(
                post(urlPathEqualTo("/upload"))
                        .willReturn(getJsonSuccess(
                                statusCode,
                                description,
                                code)));

        return client.uploadFile(file, BUCKET_NAME);
    }

    private Response<Void> executeDeleteBucket(final HttpStatus statusCode, final String description, final int code) {

        stubFor(
                delete(urlPathEqualTo("/bucket/delete"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .willReturn(getJsonError(
                                statusCode,
                                description,
                                code
                        )));

        return client.deleteDocument(BUCKET_NAME);
    }

    private ResponseDefinitionBuilder getJsonSuccess(HttpStatus httpStatus, String description, int code) {
        String json = String.format("""
                {
                  "result": {
                    "httpStatusCode": "%s",
                    "status": "SUCCESS",
                    "description": "%s"
                  }
                }
                """, httpStatus, description);
        return jsonResponse(json, code);
    }

    private ResponseDefinitionBuilder getJsonError(HttpStatus httpStatus, String description, int code) {
        String json = String.format("""
                {
                  "result": {
                    "httpStatusCode": "%s",
                    "status": "ERROR",
                    "description": "%s"
                  }
                }
                """, httpStatus, description);
        return jsonResponse(json, code);
    }
}
