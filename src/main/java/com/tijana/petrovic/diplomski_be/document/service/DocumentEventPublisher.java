package com.tijana.petrovic.diplomski_be.document.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tijana.petrovic.diplomski_be.aws.SnsConfig;
import com.tijana.petrovic.diplomski_be.document.dto.DocumentReadyMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

/**
 * Publishes the "document ready" event to SNS, after the confirming transaction commits.
 * <p>
 * Binding to {@link TransactionPhase#AFTER_COMMIT} is what keeps the signal honest: a
 * consumer is told a document is ready only once its row is durably committed. The trade-off
 * is that a publish failing here, after commit, is lost rather than retried - acceptable
 * because the confirmation is idempotent and a consumer deduplicates by document id; a
 * transactional outbox would be the next step if that gap ever mattered.
 */
@Log4j2
@RequiredArgsConstructor
@Component
public class DocumentEventPublisher {

    private final SnsClient snsClient;
    private final SnsConfig snsConfig;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDocumentUploaded(DocumentUploadedEvent event) {
        publish(event.message());
    }

    private void publish(DocumentReadyMessage message) {
        try {
            var body = objectMapper.writeValueAsString(message);

            snsClient.publish(PublishRequest.builder()
                    .topicArn(snsConfig.getDocumentReadyTopicArn())
                    .message(body)
                    // A message attribute, not a body field, so a subscription can filter on it.
                    .messageAttributes(Map.of("eventType", MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue(message.eventType())
                            .build()))
                    .build());

            log.info("[DocumentEventPublisher] Published {} for document {} to {}",
                    message.eventType(), message.documentId(), snsConfig.getDocumentReadyTopicArn());
        } catch (Exception e) {
            // After-commit: the row is already persisted, so a failed publish is logged, not thrown.
            log.error("[DocumentEventPublisher] Failed to publish {} for document {}",
                    message.eventType(), message.documentId(), e);
        }
    }

}
