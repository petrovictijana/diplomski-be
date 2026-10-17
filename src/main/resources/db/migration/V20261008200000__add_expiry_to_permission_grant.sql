/*
 * Time-bounded grants: a right may stop being effective on its own, without a revoke.
 * NULL means permanent. Expiry is evaluated at query time (revokedAt IS NULL AND
 * (expiresAt IS NULL OR expiresAt > now)), so no row is deleted and the audit trail survives.
 */
ALTER TABLE public."PermissionGrant"
    ADD COLUMN "expiresAt" TIMESTAMPTZ;

ALTER TABLE public."PermissionGrant"
    ADD CONSTRAINT "ck_PermissionGrant_expiry"
        CHECK ("expiresAt" IS NULL OR "expiresAt" > "createdAt");

/*
 * uq_PermissionGrant_active is intentionally left unchanged. A partial unique index predicate
 * must be immutable, so it cannot reference now(); an expired-but-not-revoked row therefore
 * still occupies the "active" slot. Re-granting such a right is handled in PermissionGrantService
 * (the expired row is revoked before the new one is inserted).
 */
