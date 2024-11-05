package ru.abdusamatov.librarywithsecurity.controlles;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.abdusamatov.librarywithsecurity.repositories.BookRepository;
import ru.abdusamatov.librarywithsecurity.support.BookProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BookControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private BookRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String URL = "http://localhost:" + port + "/books";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pSqlContainer = new PostgreSQLContainer<>("postgres:latest");

    @BeforeAll
    static void beforeAll() {
        pSqlContainer.start();
    }

    @AfterAll
    static void afterAll() {
        pSqlContainer.stop();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldGetBookList() throws Exception {
        int listSize = 20;
        var bookDtoList = BookProvider.createListBookDto(listSize);
        repository.saveAll(bookDtoList);

        ResultActions result = mockMvc.perform(get(URL)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.httpStatusCode").value("OK"))
                .andExpect(jsonPath("$.result.status").value("SUCCESS"));

        var response = result.andReturn().getResponse();
        assertThat(response.getContentAsString()).isNotNull().isNotEmpty();
    }
}
