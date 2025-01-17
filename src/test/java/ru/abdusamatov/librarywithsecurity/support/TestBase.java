package ru.abdusamatov.librarywithsecurity.support;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClient;
import ru.abdusamatov.librarywithsecurity.context.PostgreSQLInitializer;
import ru.abdusamatov.librarywithsecurity.context.RabbitMQInitializer;
import ru.abdusamatov.librarywithsecurity.context.RedisInitializer;
import ru.abdusamatov.librarywithsecurity.repository.BookRepository;
import ru.abdusamatov.librarywithsecurity.repository.UserRepository;
import ru.abdusamatov.librarywithsecurity.service.BookService;
import ru.abdusamatov.librarywithsecurity.service.UserService;
import ru.abdusamatov.librarywithsecurity.service.handler.ReaderHandler;
import ru.abdusamatov.librarywithsecurity.service.mapper.BookMapper;
import ru.abdusamatov.librarywithsecurity.service.mapper.UserMapper;

@ContextConfiguration(initializers = {
        PostgreSQLInitializer.class,
        RedisInitializer.class,
        RabbitMQInitializer.class
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public abstract class TestBase {

    @Autowired
    protected BookMapper bookMapper;

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected BookRepository bookRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected BookService bookService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ReaderHandler readerService;

    @Autowired
    protected CacheManager cacheManager;

    @MockBean
    protected TopPdfConverterClient topPdfConverterClient;

    @SpyBean
    protected BookRepository spyBookRepository;

    @SpyBean
    protected UserRepository spyUserRepository;

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    protected abstract void clearDatabase();
}
