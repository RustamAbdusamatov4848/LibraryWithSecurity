package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.abdusamatov.librarywithsecurity.config.RabbitMQConfig;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;

}
