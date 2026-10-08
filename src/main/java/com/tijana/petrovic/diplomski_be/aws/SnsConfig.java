package com.tijana.petrovic.diplomski_be.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.sns")
public class SnsConfig {

    /**
     * Topic the "document ready" event is published to. A topic rather than a direct queue
     * so any number of consumers - Databricks ingestion now, an indexer or audit later -
     * can subscribe their own queue without the backend knowing about them.
     */
    private String documentReadyTopicArn;

}
