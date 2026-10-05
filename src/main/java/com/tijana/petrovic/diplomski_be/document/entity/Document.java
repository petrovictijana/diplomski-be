package com.tijana.petrovic.diplomski_be.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Document", schema = "public")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Original name as uploaded.
     * Deliberately not unique - two departments may both have "ugovor.pdf".
     */
    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "filePath", nullable = false, length = 512, unique = true, updatable = false)
    private String filePath;

    @Column(name = "contentType", length = 255)
    private String contentType;

    @Column(name = "fileSize")
    private Long fileSize;

    /**
     * SHA-256 of the file content as lowercase hex.
     */
    @Column(name = "checksum", length = 64)
    private String checksum;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DocumentStatus status = DocumentStatus.PENDING;

    @Builder.Default
    @Column(name = "createdAt", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    /** The user who uploaded the file. */
    @Column(name = "createdBy", nullable = false, updatable = false)
    private UUID createdBy;

    @Column(name = "updatedAt")
    private OffsetDateTime updatedAt;

    @Column(name = "updatedBy")
    private UUID updatedBy;

    @Column(name = "deletedAt")
    private OffsetDateTime deletedAt;

    @Column(name = "deletedBy")
    private UUID deletedBy;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Document other)) return false;

        return filePath != null && filePath.equals(other.filePath);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
