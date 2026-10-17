package com.tijana.petrovic.diplomski_be.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.sqs")
public class SqsConfig {

    /** Queue that receives the S3 ObjectCreated notifications - see the @SqsListener. */
    private String documentUploadQueue;

    /** Queue the backend publishes document events to, consumed downstream (Databricks ingestion). */
    private String documentEventQueue;

}
