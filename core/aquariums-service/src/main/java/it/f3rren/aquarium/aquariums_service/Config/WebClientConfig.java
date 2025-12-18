package it.f3rren.aquarium.aquariums_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.water-parameters.url}")
    private String waterParametersUrl;

    @Value("${services.manual-parameters.url}")
    private String manualParametersUrl;

    @Value("${services.target-parameters.url}")
    private String targetParametersUrl;

    @Bean
    public WebClient waterParametersWebClient() {
        return WebClient.builder()
                .baseUrl(waterParametersUrl)
                .build();
    }

    @Bean
    public WebClient manualParametersWebClient() {
        return WebClient.builder()
                .baseUrl(manualParametersUrl)
                .build();
    }

    @Bean
    public WebClient targetParametersWebClient() {
        return WebClient.builder()
                .baseUrl(targetParametersUrl)
                .build();
    }
}
