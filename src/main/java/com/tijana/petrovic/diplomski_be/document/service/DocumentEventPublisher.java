package com.tijana.petrovic.diplomski_be.document.service;

import com.tijana.petrovic.diplomski_be.aws.SqsConfig;
import com.tijana.petrovic.diplomski_be.document.dto.DocumentEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;

/**
 * Sends a {@link DocumentEvent} to the document-events queue, after the raising transaction
 * commits.
 * <p>
 * A single SQS queue, not an SNS topic: there is exactly one consumer (the Databricks ingestion
 * job), so a topic would only add a hop and an envelope for no fan-out. If a second, independent
 * consumer ever appears, an SNS topic goes in front of this queue and the backend keeps
 * publishing the same way.
 * <p>
 * Binding to {@link TransactionPhase#AFTER_COMMIT} is what keeps the signal honest: a consumer is
 * told about a document only once the state behind the event is durably committed. The trade-off
 * is that a send failing here, after commit, is lost rather than retried - acceptable because the
 * confirmation is idempotent and a consumer deduplicates by document id; a transactional outbox
 * would be the next step if that gap ever mattered.
 */
@Log4j2
@RequiredArgsConstructor
@Component
public class DocumentEventPublisher {

    private final SqsTemplate sqsTemplate;
    private final SqsConfig sqsConfig;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDocumentEvent(DocumentEvent event) {
        try {
            // Serialized against the DocumentEvent type so @JsonTypeInfo writes the eventType
            // discriminator into the body; the queue carries plain, self-describing JSON.
            var body = objectMapper.writeValueAsString(event);

            sqsTemplate.send(to -> to
                    .queue(sqsConfig.getDocumentEventQueue())
                    .payload(body));

            log.info("[DocumentEventPublisher] Sent {} for document {} to {}",
                    event.eventType(), event.documentId(), sqsConfig.getDocumentEventQueue());
        } catch (Exception e) {
            // After-commit: the state is already persisted, so a failed send is logged, not thrown.
            log.error("[DocumentEventPublisher] Failed to send {} for document {}",
                    event.eventType(), event.documentId(), e);
        }
    }

}
