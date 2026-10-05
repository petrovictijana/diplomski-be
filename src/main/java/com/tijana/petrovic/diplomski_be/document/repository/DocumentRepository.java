package com.tijana.petrovic.diplomski_be.document.repository;

import com.tijana.petrovic.diplomski_be.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    /**
     * Soft-deleted documents are excluded here rather than filtered by the caller: a
     * deleted document keeps its row for the audit trail, but must behave as if it is gone.
     */
    Optional<Document> findByIdAndDeletedAtIsNull(UUID id);
}
