package com.tijana.petrovic.diplomski_be.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Classification a document carries, for example GENERAL, EMPLOYEE or HR.
 * A table rather than an enum, so a new label does not require a deployment.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Label", schema = "public")
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Uppercase by database constraint - labels are compared as identifiers. */
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Builder.Default
    @Column(name = "createdAt", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "createdBy", updatable = false)
    private UUID createdBy;

    @Column(name = "updatedAt")
    private OffsetDateTime updatedAt;

    @Column(name = "updatedBy")
    private UUID updatedBy;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Label other)) return false;

        return name != null && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
