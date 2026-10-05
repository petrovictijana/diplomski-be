package com.tijana.petrovic.diplomski_be.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

/**
 * AWS clients used by the document domain.
 * <p>
 * They are declared here rather than left to Spring Cloud AWS auto-configuration so that the
 * endpoint override and the path-style switch - the two things that differ between LocalStack
 * and real AWS - live in one place. Declaring {@link SqsAsyncClient} also satisfies the
 * {@code @SqsListener} container factory, which backs off when the client bean already exists.
 */
@Configuration
@RequiredArgsConstructor
public class AwsConfig {

    private final AwsProperties awsProperties;
    private final S3Config s3Config;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if (!awsProperties.hasStaticCredentials()) {
            return DefaultCredentialsProvider.create();
        }

        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey()));
    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider) {
        var builder = S3Client.builder()
                .region(region())
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(s3Configuration());

        applyEndpointOverride(builder);

        return builder.build();
    }

    /**
     * Separate from {@link #s3Client} because presigning is a signing-only operation: the
     * presigner never talks to S3, it only builds and signs the URL the client then uses.
     */
    @Bean
    public S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider) {
        var builder = S3Presigner.builder()
                .region(region())
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(s3Configuration());

        if (awsProperties.getEndpoint() != null) {
            builder.endpointOverride(awsProperties.getEndpoint());
        }

        return builder.build();
    }

    @Bean
    public SqsAsyncClient sqsAsyncClient(AwsCredentialsProvider credentialsProvider) {
        var builder = SqsAsyncClient.builder()
                .region(region())
                .credentialsProvider(credentialsProvider);

        applyEndpointOverride(builder);

        return builder.build();
    }

    private Region region() {
        return Region.of(awsProperties.getRegion());
    }

    private S3Configuration s3Configuration() {
        return S3Configuration.builder()
                .pathStyleAccessEnabled(s3Config.isPathStyleAccess())
                .build();
    }

    private void applyEndpointOverride(AwsClientBuilder<?, ?> builder) {
        if (awsProperties.getEndpoint() != null) {
            builder.endpointOverride(awsProperties.getEndpoint());
        }
    }

}
