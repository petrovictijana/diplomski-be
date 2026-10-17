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

    /**
     * The assignment rows themselves, label fetched, so a relabel can diff against the current
     * set and delete only the rows that fall out of it - keeping the audit on the ones that stay.
     */
    @Query("select dl from DocumentLabel dl join fetch dl.label where dl.document.id = :documentId")
    List<DocumentLabel> findAssignmentsByDocumentId(@Param("documentId") UUID documentId);
}
