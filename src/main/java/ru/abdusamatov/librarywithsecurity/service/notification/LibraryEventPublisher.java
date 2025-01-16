package ru.abdusamatov.librarywithsecurity.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abdusamatov.librarywithsecurity.dto.OutboxApplicationEvent;
import ru.abdusamatov.librarywithsecurity.model.OutboxDomainEvent;
import ru.abdusamatov.librarywithsecurity.repository.OutboxDomainEventRepository;

@Service
@RequiredArgsConstructor
public class LibraryEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final OutboxDomainEventRepository repository;

    @Transactional
    public void publishEvent(final String userName, final String bookName) {
        OutboxDomainEvent outboxFileEvent = repository.save(
                OutboxDomainEvent.builder()
                        .userName(userName)
                        .bookName(bookName)
                        .build()
        );

        applicationEventPublisher.publishEvent(
                new OutboxApplicationEvent(outboxFileEvent));
    }
}
