package com.tijana.petrovic.diplomski_be.document.repository;

import com.tijana.petrovic.diplomski_be.document.entity.DocumentLabel;
import com.tijana.petrovic.diplomski_be.document.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DocumentLabelRepository extends JpaRepository<DocumentLabel, UUID> {

    /** The labels currently applied to a document, resolved in one statement for the event payload. */
    @Query("select dl.label from DocumentLabel dl where dl.document.id = :documentId")
    List<Label> findLabelsByDocumentId(@Param("documentId") UUID documentId);
}
