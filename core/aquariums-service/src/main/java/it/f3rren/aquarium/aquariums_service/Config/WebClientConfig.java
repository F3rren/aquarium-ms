package it.f3rren.aquarium.aquariums_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for setting up web clients for different services.
 * This class provides configuration for web clients used to communicate with external services.
 * The URLs for different services are injected via application properties.
 * These web clients can be injected into service classes for making HTTP requests to these services.
 * Each web client is configured with a base URL and can be used to make HTTP requests to the respective service.
 * @author F3rren
 */
@Configuration
public class WebClientConfig {

    /**
     * URL for the water parameters service as provided in the application properties. This URL is used to configure the web client.
     */
    @Value("${services.water-parameters.url}")
    private String waterParametersUrl;

    /**
     * URL for the manual parameters service as provided in the application properties. This URL is used to configure the web client.
     */
    @Value("${services.manual-parameters.url}")
    private String manualParametersUrl;

    /**
     * URL for the target parameters service as provided in the application properties. This URL is used to configure the web client.
     */
    @Value("${services.target-parameters.url}")
    private String targetParametersUrl;

    /**
     * Bean to configure and create a web client for the water parameters service. This web client is used to make HTTP requests to the water
     * parameters service.
     * @return WebClient instance for water parameters service
     */
    @Bean
    public WebClient waterParametersWebClient() {
        return WebClient.builder()
                .baseUrl(waterParametersUrl)
                .build();
    }

    /**
     * Bean to configure and create a web client for the manual parameters service. This web client is used to make HTTP requests to the manual
     * parameters service.
     * @return WebClient instance for manual parameters service
     */
    @Bean
    public WebClient manualParametersWebClient() {
        return WebClient.builder()
                .baseUrl(manualParametersUrl)
                .build();
    }

    /**
     * Bean to configure and create a web client for the target parameters service. This web client is used to make HTTP requests to the target
     * parameters service.
     * @return WebClient instance for target parameters service
     */
    @Bean
    public WebClient targetParametersWebClient() {
        return WebClient.builder()
                .baseUrl(targetParametersUrl)
                .build();
    }
}
