package ru.abdusamatov.librarywithsecurity.config.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.abdusamatov.librarywithsecurity.dto.response.Response;
import ru.abdusamatov.librarywithsecurity.util.ParameterizedTypeReferenceUtil;

@RequiredArgsConstructor
public class TopPdfConverterClient {
    private final WebClient webClient;

    public Response<Void> addBucket(final String bucketName) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/addBucket/${bucketName}")
                        .build(bucketName))
                .retrieve()
                .bodyToMono(ParameterizedTypeReferenceUtil.getResponseReference())
                .block();
    }

    public Response<byte[]> getDocument(final String bucketName, final String fileName) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/download")
                        .queryParam("bucketName", bucketName)
                        .queryParam("fileName", fileName)
                        .build())
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(ParameterizedTypeReferenceUtil.getResponseReference(byte[].class))
                .block();
    }

    public Response<Void> uploadFile(final MultipartFile file, final String bucketName) {
        final var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", new MultipartBodyBuilder().part("file", file.getResource()));
        body.add("bucketName", bucketName);

        return webClient
                .post()
                .uri("/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(ParameterizedTypeReferenceUtil.getResponseReference())
                .block();
    }

    public Response<Void> updateDocument(final String bucketName, final String fileName) {
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/update")
                        .queryParam("bucketName", bucketName)
                        .queryParam("fileName", fileName)
                        .build())
                .retrieve()
                .bodyToMono(ParameterizedTypeReferenceUtil.getResponseReference())
                .block();
    }

    public Response<Void> deleteDocument(final String bucketName) {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/bucket/delete")
                        .queryParam("bucketName", bucketName)
                        .build(bucketName))
                .retrieve()
                .bodyToMono(ParameterizedTypeReferenceUtil.getResponseReference())
                .block();
    }
}
