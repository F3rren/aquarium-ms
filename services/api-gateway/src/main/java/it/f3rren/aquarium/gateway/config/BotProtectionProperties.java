package it.f3rren.aquarium.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "bot-protection")
public class BotProtectionProperties {

    private boolean enabled = true;
    private List<String> blockedUserAgentPatterns = List.of(
            "curl/", "python-requests/", "Go-http-client/",
            "Java/", "Apache-HttpClient", "okhttp/",
            "wget", "libwww-perl", "scrapy"
    );
    private List<String> excludedPaths = List.of("/actuator", "/swagger-ui", "/v3/api-docs");
}
