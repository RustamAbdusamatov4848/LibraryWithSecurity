package ru.abdusamatov.librarywithsecurity.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class RabbitMQConfig {

    @Bean
    public Declarables rabbitDeclarables(final RabbitMQProperties properties) {
        var exchange = new DirectExchange(properties.getExchange(), true, false);
        var libraryQueue = new Queue(properties.getLibrary().getQueue(), true);
        var libraryBinding = BindingBuilder
                .bind(libraryQueue)
                .to(exchange)
                .with(properties.getLibrary().getRoutingKey());

        return new Declarables(exchange, libraryQueue, libraryBinding);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
