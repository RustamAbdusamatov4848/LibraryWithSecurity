package ru.abdusamatov.librarywithsecurity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbox_events", schema = "library")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxDomainEvent extends BaseEntity {

    @Column(name = "user_name")
    private String userName;

    @Column(name = "book_name")
    private String bookName;
}
