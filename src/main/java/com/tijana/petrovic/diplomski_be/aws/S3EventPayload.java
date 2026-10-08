package com.tijana.petrovic.diplomski_be.aws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The slice of an S3 ObjectCreated notification the listener needs.
 * <p>
 * Only bucket, key and size are mapped - the notification carries far more, and S3 also
 * sends a keyless {@code s3:TestEvent} when the notification is first wired, which
 * deserializes here to an empty {@link #records()}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record S3EventPayload(@JsonProperty("Records") List<Entry> records) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entry(@JsonProperty("s3") S3 s3) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record S3(@JsonProperty("bucket") Bucket bucket, @JsonProperty("object") S3Object object) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Bucket(@JsonProperty("name") String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record S3Object(@JsonProperty("key") String key, @JsonProperty("size") Long size) {
    }

}
