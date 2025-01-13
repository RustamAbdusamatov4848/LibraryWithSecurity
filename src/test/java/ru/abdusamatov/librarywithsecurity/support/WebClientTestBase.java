package ru.abdusamatov.librarywithsecurity.support;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.abdusamatov.librarywithsecurity.config.client.TopPdfConverterClientConfig;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {
        WebClientTestBase.WireMockConfiguration.class,
        TopPdfConverterClientConfig.class
})
@AutoConfigureWireMock(port = 0)
public abstract class WebClientTestBase {

    @Autowired
    protected WireMockServer wireMockServer;

    protected StubMapping stubFor(final MappingBuilder mappingBuilder) {
        return wireMockServer.stubFor(mappingBuilder);
    }

    protected void assertMethodAndPath(final RequestMethod method, final String path) {
        wireMockServer.verify(newRequestPattern(method, urlEqualTo(path)));
    }

    @TestConfiguration
    static class WireMockConfiguration {
        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }
}
