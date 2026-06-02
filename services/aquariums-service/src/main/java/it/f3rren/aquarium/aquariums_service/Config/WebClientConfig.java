package it.f3rren.aquarium.aquariums_service.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Configuration class for setting up REST clients for different parameter services.
 * Each RestClient is configured with a base URL from application properties
 * and can be injected into service classes for making HTTP requests.
 * @author F3rren
 */
@Configuration
public class WebClientConfig {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(10);

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
        return buildRestClient(waterParametersUrl);
    }

    /**
     * RestClient bean for the manual parameters service.
     * @return RestClient instance configured with the manual parameters base URL
     */
    @Bean
    public RestClient manualParametersRestClient() {
        return buildRestClient(manualParametersUrl);
    }

    /**
     * RestClient bean for the target parameters service.
     * @return RestClient instance configured with the target parameters base URL
     */
    @Bean
    public RestClient targetParametersRestClient() {
        return buildRestClient(targetParametersUrl);
    }

    /**
     * Builds a {@link RestClient} with a fixed base URL and shared timeout settings.
     *
     * <p>Uses the JDK {@link HttpClient} as the underlying transport so that
     * connect and read timeouts are enforced at the socket level, preventing
     * downstream services from blocking threads indefinitely.</p>
     *
     * @param baseUrl the base URL of the downstream service
     * @return a configured {@link RestClient} instance
     */
    private RestClient buildRestClient(String baseUrl) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(READ_TIMEOUT);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}
