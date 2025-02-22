package ru.abdusamatov.librarywithsecurity.context;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQLInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public static final String FULL_IMAGE_NAME = "postgres:17.1-alpine3.20";
    private static final DockerImageName IMAGE = DockerImageName.parse(FULL_IMAGE_NAME);
    private static final Network NETWORK = Network.newNetwork();
    private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>(IMAGE);

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        CONTAINER
                .withNetwork(NETWORK)
                .withUrlParam("prepareThreshold", "0")
                .start();

        TestPropertyValues.of(
                "spring.datasource.url=" + CONTAINER.getJdbcUrl(),
                "spring.datasource.username=" + CONTAINER.getUsername(),
                "spring.datasource.password=" + CONTAINER.getPassword()
        ).applyTo(applicationContext.getEnvironment());
    }
}
