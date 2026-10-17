/* Document upload is open to anonymous callers, so an uploaded document may have no
   authenticated owner. Relax the NOT NULL - the FK to "User"(id) still holds when set. */
ALTER TABLE public."Document"
    ALTER COLUMN "createdBy" DROP NOT NULL;
