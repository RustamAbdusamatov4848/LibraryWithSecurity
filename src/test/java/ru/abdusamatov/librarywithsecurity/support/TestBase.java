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
import ru.abdusamatov.librarywithsecurity.context.RedisInitializer;
import ru.abdusamatov.librarywithsecurity.repository.BookRepository;
import ru.abdusamatov.librarywithsecurity.repository.ReaderRepository;
import ru.abdusamatov.librarywithsecurity.service.BookService;
import ru.abdusamatov.librarywithsecurity.service.ReaderService;
import ru.abdusamatov.librarywithsecurity.service.handler.ReaderHandler;
import ru.abdusamatov.librarywithsecurity.service.mapper.BookMapper;
import ru.abdusamatov.librarywithsecurity.service.mapper.ReaderMapper;

@ContextConfiguration(initializers = {
        PostgreSQLInitializer.class,
        RedisInitializer.class
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public abstract class TestBase {

    @Autowired
    protected BookMapper bookMapper;

    @Autowired
    protected ReaderMapper readerMapper;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected BookRepository bookRepository;

    @Autowired
    protected ReaderRepository readerRepository;

    @Autowired
    protected BookService bookService;

    @Autowired
    protected ReaderService readerService;

    @Autowired
    protected ReaderHandler readerHandler;

    @Autowired
    protected CacheManager cacheManager;

    @MockBean
    protected TopPdfConverterClient topPdfConverterClient;

    @SpyBean
    protected BookRepository spyBookRepository;

    @SpyBean
    protected ReaderRepository spyReaderRepository;

    @AfterEach
    public void tearDown() {
        clearDatabase();
    }

    protected abstract void clearDatabase();
}
