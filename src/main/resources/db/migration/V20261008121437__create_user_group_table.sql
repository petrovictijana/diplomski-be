/* Create UserGroup table - "Group" is a reserved SQL keyword */
CREATE TABLE public."UserGroup" (
    id UUID PRIMARY KEY,

    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),

    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "createdBy" UUID,

    "updatedAt" TIMESTAMPTZ,
    "updatedBy" UUID,

    CONSTRAINT "fk_UserGroup_createdBy"
        FOREIGN KEY ("createdBy")
        REFERENCES public."User"(id),

    CONSTRAINT "fk_UserGroup_updatedBy"
        FOREIGN KEY ("updatedBy")
        REFERENCES public."User"(id)
);
