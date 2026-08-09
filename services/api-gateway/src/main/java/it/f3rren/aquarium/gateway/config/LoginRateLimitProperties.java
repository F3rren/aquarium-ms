package it.f3rren.aquarium.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Brute-force protection specifically for {@code POST /auth/login}, deliberately separate from
 * {@link RateLimitProperties}: that one only ever sees requests the gateway routes to a backend
 * (see {@link it.f3rren.aquarium.gateway.filter.LoginRateLimitFilter} for why login needs its
 * own mechanism), and a login endpoint's tolerable attempt rate is a distinct concern from a
 * general API's read/write throughput anyway.
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth.rate-limit")
public class LoginRateLimitProperties {

    private boolean enabled = true;
    private int capacity = 5;
    private int refillTokens = 5;
    private int refillSeconds = 60;
    private long cleanupIntervalMs = 300_000L;
    private int bucketTtlMinutes = 10;
}
