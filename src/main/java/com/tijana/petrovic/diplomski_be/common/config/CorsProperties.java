package com.tijana.petrovic.diplomski_be.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * CORS settings for the browser frontend. Origins must be listed explicitly (never {@code *}):
 * the auth flow relies on a credentialed refresh cookie, and {@code allowCredentials} forbids a
 * wildcard origin.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    private List<String> allowedOrigins = new ArrayList<>();

    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

    private List<String> allowedHeaders = List.of("*");

    private boolean allowCredentials = true;

    private Duration maxAge = Duration.ofHours(1);
}
