package ru.abdusamatov.librarywithsecurity.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.abdusamatov.commons.event.LibraryEvent;
import ru.abdusamatov.commons.util.WrapperUtil;
import ru.abdusamatov.librarywithsecurity.config.RabbitMQProperties;

@Component
@RequiredArgsConstructor
public class RabbitEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties config;

    public void publishEvent(final LibraryEvent libraryEvent) {
        WrapperUtil.produce(
                config.getExchange(),
                config.getLibrary().getRoutingKey(),
                libraryEvent,
                rabbitTemplate::convertAndSend
        );
    }
}
