package com.tijana.petrovic.diplomski_be.access.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A single granted right: a subject may perform an action over a resource.
 * <p>
 * <b>Subject</b> is exactly one of {@code userId} or {@code groupId} (a direct grant, or one
 * inherited through group membership). <b>Scope</b> is {@code resourceType} plus {@code resourceId};
 * a {@code null} resource id means every resource of that type (for example READ / DOCUMENT / null
 * reads all documents).
 * <p>
 * The model is grant-only - there is no DENY. A right is withdrawn by setting {@code revokedAt},
 * never by deleting the row, so the audit trail survives. Ids are stored as plain UUIDs rather than
 * relations: the subject is polymorphic and optional, the resource id is polymorphic and cannot
 * carry a foreign key, and access evaluation runs in SQL over these ids.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PermissionGrant", schema = "public")
public class PermissionGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Set when the grant is direct to a user; mutually exclusive with {@link #groupId}. */
    @Column(name = "userId", updatable = false)
    private UUID userId;

    /** Set when the grant is to a group; mutually exclusive with {@link #userId}. */
    @Column(name = "groupId", updatable = false)
    private UUID groupId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30, updatable = false)
    private Action action;

    @Enumerated(EnumType.STRING)
    @Column(name = "resourceType", nullable = false, length = 20, updatable = false)
    private ResourceType resourceType;

    /** A specific resource, or {@code null} for every resource of {@link #resourceType}. */
    @Column(name = "resourceId", updatable = false)
    private UUID resourceId;

    @Builder.Default
    @Column(name = "createdAt", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    /** The user who granted the right. */
    @Column(name = "createdBy", updatable = false)
    private UUID createdBy;

    @Column(name = "revokedAt")
    private OffsetDateTime revokedAt;

    @Column(name = "revokedBy")
    private UUID revokedBy;

    public boolean isRevoked() {
        return revokedAt != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PermissionGrant other)) return false;

        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
