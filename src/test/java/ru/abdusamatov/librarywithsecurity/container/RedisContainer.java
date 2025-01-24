package ru.abdusamatov.librarywithsecurity.container;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public class RedisContainer extends GenericContainer<RedisContainer> {
    public static final String FULL_IMAGE_NAME = "redis";
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(FULL_IMAGE_NAME);
    private static final int REDIS_PORT = 6379;
    private static final long TTL = 600000;

    public RedisContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);

        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
        setWaitStrategy(Wait
                .forLogMessage(".*Ready to accept connections.*", 1)
                .withStartupTimeout(Duration.ofSeconds(60)));
        addExposedPort(REDIS_PORT);
    }

    public String getUrl() {
        return String.format("redis://%s:%s", getHost(), getMappedPort(REDIS_PORT));
    }

    public String getTtl() {
        return String.valueOf(TTL);
    }
}
