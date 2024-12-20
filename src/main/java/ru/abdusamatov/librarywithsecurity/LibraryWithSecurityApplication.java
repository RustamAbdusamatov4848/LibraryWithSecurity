package ru.abdusamatov.librarywithsecurity;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Library API",
        version = "1.0",
        description = "API for managing library"
))
public class LibraryWithSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryWithSecurityApplication.class, args);
    }

}
