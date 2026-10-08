package com.tijana.petrovic.diplomski_be.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A department or team - HR, MANAGERS, ADMINISTRATION.
 * Permissions can be granted to a group, and every member inherits them.
 * Named "UserGroup" because GROUP is a reserved SQL keyword.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "UserGroup", schema = "public")
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Builder.Default
    @Column(name = "createdAt", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "createdBy")
    private UUID createdBy;

    @Column(name = "updatedAt")
    private OffsetDateTime updatedAt;

    @Column(name = "updatedBy")
    private UUID updatedBy;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserGroup other)) return false;

        return name != null && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
