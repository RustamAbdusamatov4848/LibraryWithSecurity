package ru.abdusamatov.librarywithsecurity.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.queues.notification-queue}")
    private String queueName;

    @Bean
    public Queue queue() {
        return new Queue(queueName, false);
    }
}
