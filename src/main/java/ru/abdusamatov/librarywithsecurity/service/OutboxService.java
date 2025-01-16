package ru.abdusamatov.librarywithsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abdusamatov.librarywithsecurity.model.OutboxDomainEvent;
import ru.abdusamatov.librarywithsecurity.model.OutboxDomainEventRepository;
import ru.ilyam.dto.enums.ApplicationNameEnum;
import ru.ilyam.dto.enums.LibraryEventNameEnum;
import ru.ilyam.dto.library.LibraryEventDto;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final RabbitEventPublisher rabbitEventPublisher;
    private final OutboxDomainEventRepository repository;

    public void sendFromOutbox(OutboxDomainEvent outboxDomainEvent) {
        rabbitEventPublisher.publishEvent(
                LibraryEventDto.builder()
                        .userName(outboxDomainEvent.getUserName())
                        .bookName(outboxDomainEvent.getBookName())
                        .eventName(LibraryEventNameEnum.BOOK_ASSIGNED)
                        .applicationName(ApplicationNameEnum.LIBRARY)
                        .build()
        );
        repository.delete(outboxDomainEvent);
    }
}