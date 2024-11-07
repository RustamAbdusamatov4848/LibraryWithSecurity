package ru.abdusamatov.librarywithsecurity.controlles;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public abstract class TestBase {

    @ServiceConnection
    static final PostgreSQLContainer<?> pSqlContainer;

    static {
        pSqlContainer = new PostgreSQLContainer<>("postgres:latest");
        pSqlContainer.start();
    }

    @Autowired
    protected WebTestClient webTestClient;

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    protected abstract void clearDatabase();
}
