package ru.abdusamatov.librarywithsecurity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "document", schema = "library")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bucket_name", unique = true)
    private String bucketName;

    @Column(name = "file_name", unique = true)
    private String fileName;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reader_id",
            nullable = false,
            unique = true,
            referencedColumnName = "id")
    private Reader owner;

}
