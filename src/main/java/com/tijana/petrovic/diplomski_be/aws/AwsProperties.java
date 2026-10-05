package com.tijana.petrovic.diplomski_be.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.net.URI;

/**
 * Settings shared by every AWS client: where to talk and how to sign.
 * Per-service settings live in {@link S3Config} and {@link SqsConfig}.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    private String region = "eu-central-1";

    /**
     * Endpoint override. Points at LocalStack during development and stays empty in a real
     * deployment, where the SDK resolves the AWS endpoint from the region.
     */
    private URI endpoint;

    private String accessKey;

    private String secretKey;

    /**
     * Only LocalStack needs a hardcoded key pair. When these are left empty the SDK falls
     * back to its default provider chain, which is how a deployed instance gets its role.
     */
    public boolean hasStaticCredentials() {
        return StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey);
    }

}
