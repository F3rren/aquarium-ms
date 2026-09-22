package it.f3rren.aquarium.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * No default on purpose: a missing JWT_SECRET must fail startup, not sign
     * tokens with a
     * weak, predictable key. See application.yml.
     */
    private String secret;
    private long expirationMinutes = 60;
    private List<String> excludedPaths = List.of("/actuator", "/swagger-ui", "/v3/api-docs", "/auth/login");
}
