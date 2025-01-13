package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.abdusamatov.librarywithsecurity.config.RabbitMQConfig;
import ru.ilyam.dto.MailMessage;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;

    public Mono<Void> sendBookAssignNotification(final String bookName, final String userName) {
        var message = (MailMessage) MailMessage.builder()
                .bodyMessage(String.format("The bool %s has been assigned to the user %s", bookName, userName))
                .build();
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(rabbitMQConfig.getExchangeName(),message))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
