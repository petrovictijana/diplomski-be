/* Create PermissionGrant table - a single granted right (grant-only, no DENY) */
CREATE TABLE public."PermissionGrant" (
    id UUID PRIMARY KEY,

    /* Subject - exactly one of userId / groupId is set */
    "userId" UUID,
    "groupId" UUID,

    /* What may be done */
    action VARCHAR(30) NOT NULL,

    /* Scope - resourceType always set; resourceId NULL means "every resource of this type" */
    "resourceType" VARCHAR(20) NOT NULL,
    "resourceId" UUID,

    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "createdBy" UUID,

    /* Revoked instead of deleted, so the audit trail survives */
    "revokedAt" TIMESTAMPTZ,
    "revokedBy" UUID,

    /* Subject is a user XOR a group - exactly one is present */
    CONSTRAINT "ck_PermissionGrant_subject"
        CHECK ((("userId" IS NOT NULL)::int + ("groupId" IS NOT NULL)::int) = 1),

    CONSTRAINT "ck_PermissionGrant_action"
        CHECK (action IN ('READ', 'ADD_LABEL', 'ADD_USER', 'CREATE_GROUP')),

    CONSTRAINT "ck_PermissionGrant_resourceType"
        CHECK ("resourceType" IN ('DOCUMENT', 'LABEL', 'GROUP', 'SYSTEM')),

    /* SYSTEM-scoped rights have no concrete target */
    CONSTRAINT "ck_PermissionGrant_system_resourceId"
        CHECK ("resourceType" <> 'SYSTEM' OR "resourceId" IS NULL),

    CONSTRAINT "fk_PermissionGrant_userId"
        FOREIGN KEY ("userId")
        REFERENCES public."User"(id),

    CONSTRAINT "fk_PermissionGrant_groupId"
        FOREIGN KEY ("groupId")
        REFERENCES public."UserGroup"(id),

    CONSTRAINT "fk_PermissionGrant_createdBy"
        FOREIGN KEY ("createdBy")
        REFERENCES public."User"(id),

    CONSTRAINT "fk_PermissionGrant_revokedBy"
        FOREIGN KEY ("revokedBy")
        REFERENCES public."User"(id)
);

/*
 * The same right cannot be granted twice while still active.
 * NULLS NOT DISTINCT (PostgreSQL 15+) treats two "all resources" grants (resourceId NULL)
 * as equal, so the rule also holds for type-wide grants.
 */
CREATE UNIQUE INDEX "uq_PermissionGrant_active"
    ON public."PermissionGrant" ("userId", "groupId", action, "resourceType", "resourceId")
    NULLS NOT DISTINCT
    WHERE "revokedAt" IS NULL;

/* Effective-permission lookup walks active grants by subject */
CREATE INDEX "idx_PermissionGrant_userId_active"
    ON public."PermissionGrant" ("userId")
    WHERE "revokedAt" IS NULL;

CREATE INDEX "idx_PermissionGrant_groupId_active"
    ON public."PermissionGrant" ("groupId")
    WHERE "revokedAt" IS NULL;
