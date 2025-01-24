package ru.abdusamatov.librarywithsecurity.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abdusamatov.librarywithsecurity.model.OutboxDomainEvent;
import ru.abdusamatov.librarywithsecurity.repository.OutboxDomainEventRepository;
import ru.ilyam.enums.LibraryEventNameEnum;
import ru.ilyam.event.LibraryEvent;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final RabbitEventPublisher rabbitEventPublisher;
    private final OutboxDomainEventRepository repository;

    public void sendFromOutbox(OutboxDomainEvent outboxDomainEvent) {
        rabbitEventPublisher.publishEvent(
                LibraryEvent.builder()
                        .userName(outboxDomainEvent.getUserName())
                        .bookName(outboxDomainEvent.getBookName())
                        .eventName(LibraryEventNameEnum.BOOK_ASSIGNED)
                        .build()
        );
        repository.delete(outboxDomainEvent);
    }
}