package ru.abdusamatov.librarywithsecurity.dto;

import lombok.Value;
import ru.abdusamatov.librarywithsecurity.model.OutboxDomainEvent;

@Value
public class OutboxApplicationEvent {
    OutboxDomainEvent outboxDomainEvent;
}
