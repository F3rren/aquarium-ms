package it.f3rren.aquarium.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;
    private int generalCapacity = 120;
    private int generalRefillTokens = 2;
    private int generalRefillSeconds = 1;
    private int writeCapacity = 20;
    private int writeRefillTokens = 1;
    private int writeRefillSeconds = 3;
    private long cleanupIntervalMs = 300_000L;
    private int bucketTtlMinutes = 10;
    private List<String> excludedPaths = List.of("/actuator", "/swagger-ui", "/v3/api-docs");
}
