package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.abdusamatov.librarywithsecurity.support.PostgreSQLInitializer;

@ContextConfiguration(initializers = PostgreSQLInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public abstract class TestBase {

    @Autowired
    protected WebTestClient webTestClient;

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    protected abstract void clearDatabase();
}
