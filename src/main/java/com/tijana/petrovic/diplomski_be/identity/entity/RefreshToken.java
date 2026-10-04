package com.tijana.petrovic.diplomski_be.identity.entity;

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
@Table(name = "RefreshToken", schema = "public")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false, updatable = false)
    private User user;

    /**
     * All tokens created by rotation from a single login share the same family.
     * If an already rotated token is presented again, the whole family is revoked.
     */
    @Column(name = "familyId", nullable = false, updatable = false)
    private UUID familyId;

    @Column(name = "tokenHash", nullable = false, length = 64, unique = true, updatable = false)
    private String tokenHash;

    @Column(name = "expiresAt", nullable = false, updatable = false)
    private OffsetDateTime expiresAt;

    @Builder.Default
    @Column(name = "createdAt", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "revokedAt")
    private OffsetDateTime revokedAt;

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(OffsetDateTime.now());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RefreshToken other)) return false;

        return tokenHash != null && tokenHash.equals(other.tokenHash);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
