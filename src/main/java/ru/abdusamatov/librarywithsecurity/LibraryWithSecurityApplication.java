package ru.abdusamatov.librarywithsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LibraryWithSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryWithSecurityApplication.class, args);
    }

}
