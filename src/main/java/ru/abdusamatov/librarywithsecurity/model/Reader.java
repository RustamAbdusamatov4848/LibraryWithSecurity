package ru.abdusamatov.librarywithsecurity.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "reader", schema = "library")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "books")
@Builder
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Book> books;

    @OneToOne(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Document document;

    public void setDocument(Document document) {
        this.document = document;
        document.setOwner(this);
    }
}
