package ru.abdusamatov.librarywithsecurity.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.abdusamatov.librarywithsecurity.dto.OutboxApplicationEvent;
import ru.abdusamatov.librarywithsecurity.service.notification.OutboxService;

@Component
@RequiredArgsConstructor
public class OutboxApplicationEventListener {
    private final OutboxService outboxService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOutboxApplicationEvent(OutboxApplicationEvent appEvent) {
        outboxService.sendFromOutbox(appEvent.getOutboxDomainEvent());
    }
}
