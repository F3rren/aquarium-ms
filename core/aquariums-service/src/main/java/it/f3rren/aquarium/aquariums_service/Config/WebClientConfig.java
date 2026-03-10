package it.f3rren.aquarium.aquariums_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration class for setting up REST clients for different parameter services.
 * Each RestClient is configured with a base URL from application properties
 * and can be injected into service classes for making HTTP requests.
 * @author F3rren
 */
@Configuration
public class WebClientConfig {

    /**
     * URL for the water parameters service.
     */
    @Value("${services.water-parameters.url}")
    private String waterParametersUrl;

    /**
     * URL for the manual parameters service.
     */
    @Value("${services.manual-parameters.url}")
    private String manualParametersUrl;

    /**
     * URL for the target parameters service.
     */
    @Value("${services.target-parameters.url}")
    private String targetParametersUrl;

    /**
     * RestClient bean for the water parameters service.
     * @return RestClient instance configured with the water parameters base URL
     */
    @Bean
    public RestClient waterParametersRestClient() {
        return RestClient.builder()
                .baseUrl(waterParametersUrl)
                .build();
    }

    /**
     * RestClient bean for the manual parameters service.
     * @return RestClient instance configured with the manual parameters base URL
     */
    @Bean
    public RestClient manualParametersRestClient() {
        return RestClient.builder()
                .baseUrl(manualParametersUrl)
                .build();
    }

    /**
     * RestClient bean for the target parameters service.
     * @return RestClient instance configured with the target parameters base URL
     */
    @Bean
    public RestClient targetParametersRestClient() {
        return RestClient.builder()
                .baseUrl(targetParametersUrl)
                .build();
    }
}
