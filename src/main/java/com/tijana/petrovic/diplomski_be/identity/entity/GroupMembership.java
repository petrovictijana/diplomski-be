package com.tijana.petrovic.diplomski_be.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Membership of a user in a group - the M:N link between {@link User} and {@link UserGroup}.
 * <p>
 * An explicit entity instead of a {@code @ManyToMany} join table, because joining a group
 * grants the user every permission held by that group - so who added the member, and when,
 * belongs in the audit trail.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "GroupMembership", schema = "public")
public class GroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "groupId", nullable = false, updatable = false)
    private UserGroup group;

    @Builder.Default
    @Column(name = "createdAt", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    /** The user who added the member to the group. */
    @Column(name = "createdBy", updatable = false)
    private UUID createdBy;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GroupMembership other)) return false;

        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
