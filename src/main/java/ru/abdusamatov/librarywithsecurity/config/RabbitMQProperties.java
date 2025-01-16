package ru.abdusamatov.librarywithsecurity.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMQProperties {
    @NotBlank
    private String exchange;

    private LibraryApp library;

    @Data
    public static class LibraryApp {
        @NotBlank
        private String queue;
        @NotBlank
        private String routingKey;
    }
}
