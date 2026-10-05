package com.tijana.petrovic.diplomski_be.document.service;

import com.tijana.petrovic.diplomski_be.document.dto.CreateDocumentRequest;
import com.tijana.petrovic.diplomski_be.document.dto.DocumentUploadResponse;
import com.tijana.petrovic.diplomski_be.document.entity.Document;
import com.tijana.petrovic.diplomski_be.document.entity.DocumentLabel;
import com.tijana.petrovic.diplomski_be.document.entity.DocumentStatus;
import com.tijana.petrovic.diplomski_be.document.entity.Label;
import com.tijana.petrovic.diplomski_be.document.exception.DocumentAlreadyUploadedException;
import com.tijana.petrovic.diplomski_be.document.exception.DocumentContentNotAvailableException;
import com.tijana.petrovic.diplomski_be.document.exception.DocumentNotFoundException;
import com.tijana.petrovic.diplomski_be.document.repository.DocumentLabelRepository;
import com.tijana.petrovic.diplomski_be.document.repository.DocumentRepository;
import com.tijana.petrovic.diplomski_be.document.repository.LabelRepository;
import com.tijana.petrovic.diplomski_be.identity.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentLabelRepository documentLabelRepository;
    private final LabelRepository labelRepository;
    private final DocumentStorageService documentStorageService;
    private final CurrentUserProvider currentUserProvider;

    /**
     * Creates the metadata row and returns the URL its content must be uploaded to.
     * <p>
     * The document stays {@code PENDING} until the upload event for its key arrives, so a
     * caller that never follows through leaves behind a row that no listing or search
     * returns, rather than a document pointing at nothing.
     * <p>
     * Labels are applied here, in the same transaction: a document must never exist with
     * fewer labels than asked for, because a missing label is a document fewer people can
     * reach than intended - and, with a default label, one more than intended.
     */
    @Transactional
    public DocumentUploadResponse createDocument(CreateDocumentRequest request) {
        var currentUserId = currentUserProvider.currentUserId();
        var labels = labelRepository.findByNameIn(request.labels());

        var document = documentRepository.save(Document.builder()
                .filename(request.filename())
                .createdBy(currentUserId)
                .build());

        applyLabels(document, labels, currentUserId);

        var storageKey = documentStorageService.generateStorageKey(document.getId());
        var upload = documentStorageService.presignUpload(storageKey);

        log.info("[DocumentService] Created document {} ({}) with labels {} for user {}",
                document.getId(), document.getFilename(), labelNames(labels), currentUserId);

        return DocumentUploadResponse.of(document, labelNames(labels), upload);
    }

    /**
     * The URL the content of an existing document is downloaded from.
     * <p>
     * Resolved from the document id, not from a key the client passes: the storage key is
     * never exposed, so a URL can only be obtained for a document the caller names by id -
     * and, once access control is in place, only for one they are permitted to read.
     */
    @Transactional(readOnly = true)
    public PresignedDownload presignDownload(UUID documentId) {
        var document = activeDocument(documentId);

        if (document.getStatus() != DocumentStatus.UPLOADED) {
            throw new DocumentContentNotAvailableException(
                    "Document %s has no content yet - its upload was never completed.".formatted(documentId));
        }

        log.info("[DocumentService] Issued download URL for document {} to user {}",
                documentId, currentUserProvider.currentUserId());

        return documentStorageService.presignDownload(
                document.getFilePath(), document.getFilename(), document.getContentType());
    }

    /**
     * A fresh URL for uploading the content of a document that is still {@code PENDING}.
     * <p>
     * The one handed out at creation expires, and the row outlives it - without this, a
     * client that was too slow would have to create a second document and the first would
     * linger as an orphan. The key stays the same, so this reissues the URL, not the document.
     */
    @Transactional(readOnly = true)
    public PresignedUpload presignUpload(UUID documentId) {
        var document = activeDocument(documentId);

        if (document.getStatus() == DocumentStatus.UPLOADED) {
            throw new DocumentAlreadyUploadedException(
                    "Document %s already has content - uploading again would overwrite it.".formatted(documentId));
        }

        log.info("[DocumentService] Reissued upload URL for document {} to user {}",
                documentId, currentUserProvider.currentUserId());

        return documentStorageService.presignUpload(document.getFilePath());
    }

    private Document activeDocument(UUID documentId) {
        return documentRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "No document with id %s.".formatted(documentId)));
    }

    private void applyLabels(Document document, List<Label> labels, UUID currentUserId) {
        var assignments = labels.stream()
                .map(label -> DocumentLabel.builder()
                        .document(document)
                        .label(label)
                        .createdBy(currentUserId)
                        .build())
                .toList();

        documentLabelRepository.saveAll(assignments);
    }

    private List<String> labelNames(List<Label> labels) {
        return labels.stream().map(Label::getName).toList();
    }

}
