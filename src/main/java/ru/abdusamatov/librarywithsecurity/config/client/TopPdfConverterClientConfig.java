package ru.abdusamatov.librarywithsecurity.config.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TopPdfConverterClientConfig {

    @Value("${top-pdf-converter.base-url}")
    private String baseUrl;

    @Bean
    public TopPdfConverterClient topPdfConverterClient(final WebClient.Builder webClientBuilder) {
        final var webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
        return new TopPdfConverterClient(webClient);
    }
}
