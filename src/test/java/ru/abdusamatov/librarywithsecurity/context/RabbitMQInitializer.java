package ru.abdusamatov.librarywithsecurity.context;

import jakarta.annotation.Nonnull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Set;

public class RabbitMQInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public static final String FULL_IMAGE_NAME = "rabbitmq:4.0.5-management-alpine";
    public static final String IMAGE_NAME = "rabbitmq";
    private static final String VHOST = "test-vhost";
    private static final String USER_NAME = "test-rabbitmq";
    private static final String USER_PASSWORD = "test-rabbitmq-pwd";
    private static final String PERMISSION_ANY = ".*";

    private static final DockerImageName IMAGE = DockerImageName.parse(FULL_IMAGE_NAME)
            .asCompatibleSubstituteFor(IMAGE_NAME);
    private static final RabbitMQContainer CONTAINER = new RabbitMQContainer(IMAGE)
            .withVhost(VHOST)
            .withUser(USER_NAME, USER_PASSWORD, Set.of("management"))
            .withPermission(VHOST, USER_NAME, PERMISSION_ANY, PERMISSION_ANY, PERMISSION_ANY);

    @Override
    public void initialize(@Nonnull final ConfigurableApplicationContext applicationContext) {
        CONTAINER.start();
        TestPropertyValues.of(
                "spring.rabbitmq.addresses=" + CONTAINER.getAmqpUrl() + "/" + VHOST,
                "spring.rabbitmq.username=" + USER_NAME,
                "spring.rabbitmq.password=" + USER_PASSWORD
        ).applyTo(applicationContext.getEnvironment());
    }
}