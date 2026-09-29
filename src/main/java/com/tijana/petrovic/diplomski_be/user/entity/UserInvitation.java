package com.tijana.petrovic.diplomski_be.user.entity;

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
@Table(name = "UserInvitation", schema = "public")
public class UserInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "tokenHash")
    private String tokenHash;

    @Column(name = "expiresAt")
    private OffsetDateTime expiresAt;

    @Column(name = "usedAt")
    private OffsetDateTime usedAt;

    @Builder.Default
    @Column(name = "createdAt", insertable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "createdBy", nullable = false)
    private User createdBy;

    @Column(name = "revokedAt")
    private OffsetDateTime revokedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revokedBy")
    private User revokedBy;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserInvitation other)) return false;

        return tokenHash != null && tokenHash.equals(other.tokenHash);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
