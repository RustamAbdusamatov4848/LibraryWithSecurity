package ru.abdusamatov.librarywithsecurity.client;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
import ru.abdusamatov.librarywithsecurity.support.AssertTestStatusUtil;
import ru.abdusamatov.librarywithsecurity.support.TestDataProvider;
import ru.abdusamatov.librarywithsecurity.support.WebClientTestBase;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

//TODO: изменить description
public class TopPdfConverterClientTest extends WebClientTestBase {
    public static final String BUCKET_NAME = "bucket-example";
    public static final String FILE_NAME = "passport.jpg";

    @Autowired
    private TopPdfConverterClient client;

    @Test
    void shouldCreateBucket_whenAddBucket() {
        stubFor(
                post(urlPathEqualTo("/addBucket/" + BUCKET_NAME))
                        .willReturn(jsonResponse("""
                                {
                                    "result": {
                                      "httpStatusCode": "CREATED",
                                      "status": "SUCCESS",
                                      "description": "Bucket successfully created"
                                    }
                                }
                                """, 201)));
        final var response = client.addBucket(BUCKET_NAME);

        AssertTestStatusUtil
                .assertSuccess(HttpStatus.CREATED, "Bucket successfully created", response);
        assertMethodAndPath(RequestMethod.POST, "/addBucket/" + BUCKET_NAME);
    }

    @Test
    void shouldReturnUserDocument_whenGetDocument() {
        final var mockFileContent = TestDataProvider.getImageBytes(FILE_NAME);

        stubFor(
                get(urlPathEqualTo("/file/download"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .withQueryParam("fileName", equalTo(FILE_NAME))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/octet-stream")
                                .withBody(mockFileContent)));

        final var response = client.getDocument(BUCKET_NAME, FILE_NAME);

        AssertTestStatusUtil
                .assertSuccess(HttpStatus.OK, "File " + FILE_NAME + " successfully uploaded", response);
        assertMethodAndPath(RequestMethod.GET, "/file/download");
    }

    @Test
    void shouldUploadDocument_whenUploadFile() {
        final var documentContent = TestDataProvider.getImageBytes(FILE_NAME);
        final var file = new MockMultipartFile(
                FILE_NAME,
                FILE_NAME,
                "application/octet-stream", documentContent);

        stubFor(
                post(urlPathEqualTo("/upload"))
                        .willReturn(jsonResponse("""
                                {
                                    "result": {
                                      "httpStatusCode": "OK",
                                      "status": "SUCCESS",
                                      "description": "File successfully uploaded"
                                    }
                                }
                                """, 200)));

        final var response = client.uploadFile(file, BUCKET_NAME);

        AssertTestStatusUtil
                .assertSuccess(HttpStatus.OK, "File " + file.getOriginalFilename() + " successfully uploaded", response);
        assertMethodAndPath(RequestMethod.POST, "/upload");
    }

    @Test
    void shouldUpdateDocument_whenUpdateFile() {
        stubFor(
                put(urlPathEqualTo("/file/update"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .withQueryParam("fileName", equalTo(FILE_NAME))
                        .willReturn(jsonResponse("""
                                {
                                    "result": {
                                      "httpStatusCode": "OK",
                                      "status": "SUCCESS",
                                      "description": "File successfully updated"
                                    }
                                }
                                """, 200)));

        final var response = client.updateDocument(BUCKET_NAME, FILE_NAME);

        AssertTestStatusUtil
                .assertSuccess(HttpStatus.OK, "File " + FILE_NAME + " successfully updated", response);
        assertMethodAndPath(RequestMethod.PUT, "/file/update");
    }

    @Test
    void testDeleteDocument() {
        stubFor(
                delete(urlPathEqualTo("/bucket/delete"))
                        .withQueryParam("bucketName", equalTo(BUCKET_NAME))
                        .willReturn(jsonResponse("""
                                {
                                    "result": {
                                      "httpStatusCode": "OK",
                                      "status": "SUCCESS",
                                      "description": "Bucket deleted"
                                    }
                                }
                                """, 204)));

        final var response = client.deleteDocument(BUCKET_NAME);

        AssertTestStatusUtil
                .assertSuccess(HttpStatus.NO_CONTENT, "Bucket " + BUCKET_NAME + " deleted", response);
        assertMethodAndPath(RequestMethod.DELETE, "/bucket/delete");
    }
}
