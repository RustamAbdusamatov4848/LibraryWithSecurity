package ru.abdusamatov.librarywithsecurity.context;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.utility.DockerImageName;
import ru.abdusamatov.librarywithsecurity.container.RedisContainer;

public class RedisInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final DockerImageName IMAGE = DockerImageName.parse("redis:latest");
    public static final RedisContainer CONTAINER = new RedisContainer(IMAGE);

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        CONTAINER.start();
        TestPropertyValues.of(
                "spring.redis.url=" + CONTAINER.getUrl()
        ).applyTo(applicationContext.getEnvironment());
    }
}
