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
@Table(name = "VerificationToken", schema = "public")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false, insertable = false, updatable = false)
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

    @Column(name = "createdBy", nullable = false, updatable = false)
    private UUID createdBy;

    @Column(name = "revokedAt")
    private OffsetDateTime revokedAt;

    @Column(name = "revokedBy", nullable = false, updatable = false)
    private UUID revokedBy;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof VerificationToken other)) return false;

        return tokenHash != null && tokenHash.equals(other.tokenHash);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
