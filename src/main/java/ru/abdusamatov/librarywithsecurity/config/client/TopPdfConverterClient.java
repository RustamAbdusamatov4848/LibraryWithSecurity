package ru.abdusamatov.librarywithsecurity.config.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.abdusamatov.librarywithsecurity.util.ParameterizedTypeReferenceUtil;
import ru.ilyam.dto.Response;

@RequiredArgsConstructor
public class TopPdfConverterClient {
    private final WebClient webClient;

    public Mono<Response<Void>> addBucket(final String bucketName) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/addBucket/" + bucketName)
                        .build())
                .retrieve()
                .bodyToMono(ParameterizedTypeReferenceUtil.getResponseReference());
    }

    public Mono<Response<byte[]>> getDocument(final String bucketName, final String fileName) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/file/download")
                        .queryParam("bucketName", bucketName)
                        .queryParam("fileName", fileName)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ParameterizedTypeReferenceUtil.getResponseReference(byte[].class));
    }

    public Mono<Response<Void>> uploadFile(final MultipartFile file, final String bucketName) {
        final var builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource());

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/upload")
                        .queryParam("bucketName", bucketName)
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ParameterizedTypeReferenceUtil.getResponseReference());
    }


    public Mono<Response<Void>> deleteDocument(final String bucketName) {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/bucket/delete")
                        .queryParam("bucketName", bucketName)
                        .build(bucketName))
                .retrieve()
                .bodyToMono(ParameterizedTypeReferenceUtil.getResponseReference());
    }
}
