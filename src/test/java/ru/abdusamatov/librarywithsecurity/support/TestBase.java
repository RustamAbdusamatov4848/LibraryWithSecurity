package ru.abdusamatov.librarywithsecurity.support;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.abdusamatov.librarywithsecurity.context.PostgreSQLInitializer;
import ru.abdusamatov.librarywithsecurity.context.RedisInitializer;

@ContextConfiguration(initializers = {
        PostgreSQLInitializer.class,
        RedisInitializer.class
})
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
