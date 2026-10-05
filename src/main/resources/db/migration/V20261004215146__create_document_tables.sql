/* Create Label table */
CREATE TABLE public."Label" (
    id UUID PRIMARY KEY,

    name VARCHAR(100) NOT NULL UNIQUE,

    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "createdBy" UUID,

    "updatedAt" TIMESTAMPTZ,
    "updatedBy" UUID,

    /* Labels are compared as identifiers, so the canonical form is uppercase */
    CONSTRAINT "ck_Label_name_uppercase"
        CHECK (name = upper(name)),

    CONSTRAINT "fk_Label_createdBy"
        FOREIGN KEY ("createdBy")
        REFERENCES public."User"(id),

    CONSTRAINT "fk_Label_updatedBy"
        FOREIGN KEY ("updatedBy")
        REFERENCES public."User"(id)
);

/* Create Document table */
CREATE TABLE public."Document" (
    id UUID PRIMARY KEY,

    /*
     * Original name as uploaded.
     * Intentionally NOT unique: two departments may both have "ugovor.pdf".
     */
    filename VARCHAR(255) NOT NULL,

    /* Generated storage key, never derived from filename */
    "filePath" VARCHAR(512) NOT NULL UNIQUE,

    "contentType" VARCHAR(255),
    "fileSize" BIGINT,
    checksum VARCHAR(64),

    /* PENDING until the upload event for "filePath" is consumed */
    status VARCHAR(20) NOT NULL,

    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "createdBy" UUID NOT NULL,

    "updatedAt" TIMESTAMPTZ,
    "updatedBy" UUID,

    /* Soft delete - permission grants and the audit trail must outlive the file */
    "deletedAt" TIMESTAMPTZ,
    "deletedBy" UUID,

    /* Both evaluate to NULL - and therefore pass - while the column is still unset */
    CONSTRAINT "ck_Document_fileSize"
        CHECK ("fileSize" > 0),

    CONSTRAINT "ck_Document_checksum"
        CHECK (checksum ~ '^[0-9a-f]{64}$'),

    CONSTRAINT "ck_Document_status"
        CHECK (status IN ('PENDING', 'UPLOADED')),

    CONSTRAINT "fk_Document_createdBy"
        FOREIGN KEY ("createdBy")
        REFERENCES public."User"(id),

    CONSTRAINT "fk_Document_updatedBy"
        FOREIGN KEY ("updatedBy")
        REFERENCES public."User"(id),

    CONSTRAINT "fk_Document_deletedBy"
        FOREIGN KEY ("deletedBy")
        REFERENCES public."User"(id)
);

/* Create DocumentLabel table */
CREATE TABLE public."DocumentLabel" (
    id UUID PRIMARY KEY,

    "documentId" UUID NOT NULL,
    "labelId" UUID NOT NULL,

    /* Labelling grants access to everyone holding that label - audit who did it */
    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "createdBy" UUID,

    CONSTRAINT "uq_DocumentLabel_documentId_labelId"
        UNIQUE ("documentId", "labelId"),

    CONSTRAINT "fk_DocumentLabel_documentId"
        FOREIGN KEY ("documentId")
        REFERENCES public."Document"(id)
        ON DELETE CASCADE,

    CONSTRAINT "fk_DocumentLabel_labelId"
        FOREIGN KEY ("labelId")
        REFERENCES public."Label"(id),

    CONSTRAINT "fk_DocumentLabel_createdBy"
        FOREIGN KEY ("createdBy")
        REFERENCES public."User"(id)
);

/* Indexes */
CREATE INDEX "idx_Document_createdBy"
    ON public."Document"("createdBy");

/* Duplicate content detection on upload */
CREATE INDEX "idx_Document_checksum"
    ON public."Document"(checksum);

/* Listing and search skip soft-deleted rows, so the index does too */
CREATE INDEX "idx_Document_createdAt_active"
    ON public."Document"("createdAt" DESC)
    WHERE "deletedAt" IS NULL;

/* Sweeping uploads that were created but never completed - a small, short-lived set */
CREATE INDEX "idx_Document_pending"
    ON public."Document"("createdAt")
    WHERE status = 'PENDING';

/* Resolving "which documents carry label X" - the hot path of access filtering */
CREATE INDEX "idx_DocumentLabel_labelId"
    ON public."DocumentLabel"("labelId");
