/* Create User table */
CREATE TABLE public."User" (
    id UUID PRIMARY KEY,

    "firstName" VARCHAR(100) NOT NULL,
    "lastName" VARCHAR(100) NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,
    "passwordHash" VARCHAR(255),

    "isActive" BOOLEAN NOT NULL DEFAULT FALSE,

    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "createdBy" UUID,

    "updatedAt" TIMESTAMPTZ,
    "updatedBy" UUID,

    "lastLoginAt" TIMESTAMPTZ,

    CONSTRAINT "fk_User_createdBy"
        FOREIGN KEY ("createdBy")
        REFERENCES public."User"(id),

    CONSTRAINT "fk_User_updatedBy"
        FOREIGN KEY ("updatedBy")
        REFERENCES public."User"(id)
);

/* Create UserInvitation table */
CREATE TABLE public."UserInvitation" (
    id UUID PRIMARY KEY,

    "userId" UUID NOT NULL,
    "tokenHash" VARCHAR(64) NOT NULL UNIQUE,

    "expiresAt" TIMESTAMPTZ NOT NULL,
    "usedAt" TIMESTAMPTZ,

    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "createdBy" UUID,

    "revokedAt" TIMESTAMPTZ,
    "revokedBy" UUID,

    CONSTRAINT "fk_UserInvitation_userId"
        FOREIGN KEY ("userId")
        REFERENCES public."User"(id),

    CONSTRAINT "fk_UserInvitation_createdBy"
        FOREIGN KEY ("createdBy")
        REFERENCES public."User"(id),

    CONSTRAINT "fk_UserInvitation_revokedBy"
        FOREIGN KEY ("revokedBy")
        REFERENCES public."User"(id)
);

/* Indexes */
CREATE INDEX "idx_UserInvitation_userId"
    ON public."UserInvitation"("userId");

/* Make sure only one active invitation exists per user */
CREATE UNIQUE INDEX "uq_UserInvitation_active"
    ON public."UserInvitation"("userId")
    WHERE "usedAt" IS NULL AND "revokedAt" IS NULL;