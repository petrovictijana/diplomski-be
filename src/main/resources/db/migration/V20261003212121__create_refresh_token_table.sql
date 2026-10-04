/* Create RefreshToken table */
CREATE TABLE public."RefreshToken" (
    id UUID PRIMARY KEY,

    "userId" UUID NOT NULL,
    "familyId" UUID NOT NULL,
    "tokenHash" VARCHAR(64) NOT NULL UNIQUE,

    "expiresAt" TIMESTAMPTZ NOT NULL,
    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "revokedAt" TIMESTAMPTZ,

    CONSTRAINT "fk_RefreshToken_userId"
        FOREIGN KEY ("userId")
        REFERENCES public."User"(id)
        ON DELETE CASCADE
);

/* Indexes */
CREATE INDEX "idx_RefreshToken_userId"
    ON public."RefreshToken"("userId");

/* Used to revoke the whole rotation chain (one login session) at once */
CREATE INDEX "idx_RefreshToken_familyId"
    ON public."RefreshToken"("familyId");
