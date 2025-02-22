package ru.abdusamatov.librarywithsecurity.context;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.utility.DockerImageName;
import ru.abdusamatov.librarywithsecurity.container.RedisContainer;

public class RedisInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public static final String FULL_IMAGE_NAME = "redis:7.4.1-alpine3.20";
    private static final DockerImageName IMAGE = DockerImageName.parse(FULL_IMAGE_NAME);
    public static final RedisContainer CONTAINER = new RedisContainer(IMAGE);

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        CONTAINER.start();
        TestPropertyValues.of(
                "spring.data.redis.url=" + CONTAINER.getUrl(),
                "spring.cache.redis.time-to-live=" + CONTAINER.getTtl()
        ).applyTo(applicationContext.getEnvironment());
    }
}
