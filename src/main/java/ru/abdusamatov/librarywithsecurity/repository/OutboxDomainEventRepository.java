package ru.abdusamatov.librarywithsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.abdusamatov.librarywithsecurity.model.OutboxDomainEvent;

public interface OutboxDomainEventRepository extends JpaRepository<OutboxDomainEvent, Long> {
}