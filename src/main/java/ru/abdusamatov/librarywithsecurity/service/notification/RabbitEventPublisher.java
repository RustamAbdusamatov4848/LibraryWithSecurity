package ru.abdusamatov.librarywithsecurity.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.abdusamatov.librarywithsecurity.config.RabbitMQProperties;
import ru.ilyam.dto.library.LibraryEventDto;
import ru.ilyam.dto.util.WrapperUtil;

@Component
@RequiredArgsConstructor
public class RabbitEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties config;

    public void publishEvent(final LibraryEventDto libraryEvent) {
        WrapperUtil.produce(
                config.getExchange(),
                config.getLibrary().getRoutingKey(),
                libraryEvent,
                rabbitTemplate::convertAndSend
        );
    }
}
