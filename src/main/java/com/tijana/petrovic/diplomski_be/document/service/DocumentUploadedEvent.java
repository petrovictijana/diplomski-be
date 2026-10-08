package com.tijana.petrovic.diplomski_be.document.service;

import com.tijana.petrovic.diplomski_be.document.dto.DocumentReadyMessage;

/**
 * Internal application event raised inside the upload-confirmation transaction.
 * <p>
 * It exists only to defer the outbound publish to after commit (see
 * {@link DocumentEventPublisher}): the "document ready" signal must never go out for a
 * transaction that then rolls back. The fully built message is carried here so the
 * after-commit listener needs no further database access, where entities would be detached.
 */
public record DocumentUploadedEvent(DocumentReadyMessage message) {
}
