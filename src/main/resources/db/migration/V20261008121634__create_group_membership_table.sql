/* Create GroupMembership table - M:N link between User and UserGroup */
CREATE TABLE public."GroupMembership" (
    id UUID PRIMARY KEY,

    "userId" UUID NOT NULL,
    "groupId" UUID NOT NULL,

    /* Joining a group grants its permissions - audit who added the member */
    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "createdBy" UUID,

    CONSTRAINT "uq_GroupMembership_userId_groupId"
        UNIQUE ("userId", "groupId"),

    CONSTRAINT "fk_GroupMembership_userId"
        FOREIGN KEY ("userId")
        REFERENCES public."User"(id)
        ON DELETE CASCADE,

    CONSTRAINT "fk_GroupMembership_groupId"
        FOREIGN KEY ("groupId")
        REFERENCES public."UserGroup"(id)
        ON DELETE CASCADE,

    CONSTRAINT "fk_GroupMembership_createdBy"
        FOREIGN KEY ("createdBy")
        REFERENCES public."User"(id)
);

/* Indexes */

/* Resolving "which groups does this user belong to" - the hot path of permission evaluation */
CREATE INDEX "idx_GroupMembership_userId"
    ON public."GroupMembership"("userId");

/* Resolving "who is in this group" */
CREATE INDEX "idx_GroupMembership_groupId"
    ON public."GroupMembership"("groupId");
