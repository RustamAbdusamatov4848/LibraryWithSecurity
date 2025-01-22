package ru.abdusamatov.librarywithsecurity.event;

import lombok.Value;
import ru.abdusamatov.librarywithsecurity.model.OutboxDomainEvent;

@Value
public class OutboxApplicationEvent {
    OutboxDomainEvent outboxDomainEvent;
}
