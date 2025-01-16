package ru.abdusamatov.librarywithsecurity.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxDomainEventRepository extends JpaRepository<OutboxDomainEvent, Long> {
}