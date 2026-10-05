package com.tijana.petrovic.diplomski_be.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Assignment of a label to a document.
 * <p>
 * An explicit entity instead of a {@code @ManyToMany} join table, because labelling a
 * document grants access to everyone holding that label - so who did it, and when,
 * belongs in the audit trail.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DocumentLabel", schema = "public")
public class DocumentLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documentId", nullable = false, updatable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "labelId", nullable = false, updatable = false)
    private Label label;

    @Builder.Default
    @Column(name = "createdAt", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    /** The user who applied the label. */
    @Column(name = "createdBy", updatable = false)
    private UUID createdBy;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DocumentLabel other)) return false;

        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
