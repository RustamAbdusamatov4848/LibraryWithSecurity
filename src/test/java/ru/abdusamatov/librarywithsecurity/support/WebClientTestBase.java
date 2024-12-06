package ru.abdusamatov.librarywithsecurity.support;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import ru.abdusamatov.librarywithsecurity.context.PostgreSQLInitializer;
import ru.abdusamatov.librarywithsecurity.context.RedisInitializer;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;

//TODO: убрать инициализаторы в TRAIN-1900
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureWireMock(port = 0)
@ContextConfiguration(initializers = {
        PostgreSQLInitializer.class,
        RedisInitializer.class
})
public abstract class WebClientTestBase {


    @Autowired
    protected WireMockServer wireMockServer;

    protected StubMapping stubFor(final MappingBuilder mappingBuilder) {
        return wireMockServer.stubFor(mappingBuilder);
    }

    protected void assertMethodAndPath(final RequestMethod method, final String path) {
        wireMockServer.verify(newRequestPattern(method, urlEqualTo(path)));
    }

}
