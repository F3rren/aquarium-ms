package it.f3rren.aquarium.target_parameter_service.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.f3rren.aquarium.target_parameter_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.target_parameter_service.dto.SaveTargetParameterDTO;
import it.f3rren.aquarium.target_parameter_service.dto.TargetParameterResponseDTO;
import it.f3rren.aquarium.target_parameter_service.kafka.listener.AquariumEventListener;
import it.f3rren.aquarium.target_parameter_service.repository.ITargetParameterRepository;

/**
 * End-to-end integration test for target parameter CRUD.
 * Uses a real PostgreSQL container via Testcontainers. Kafka listener is mocked out.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class TargetParameterIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "parameters");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.schemas", () -> "parameters");
        registry.add("spring.flyway.default-schema", () -> "parameters");
        registry.add("spring.flyway.table", () -> "flyway_schema_history_target");
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9999");
        registry.add("spring.kafka.listener.auto-startup", () -> "false");
    }

    @MockBean
    private AquariumEventListener eventListener;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ITargetParameterRepository targetParameterRepository;

    @BeforeEach
    void setUp() {
        targetParameterRepository.deleteAll();
    }

    @Test
    @DisplayName("create target parameters → get → update → get again")
    void manageTargetParameters() {
        SaveTargetParameterDTO dto = new SaveTargetParameterDTO();
        dto.setTemperature(25.0);
        dto.setPh(8.2);
        dto.setSalinity(35.0);
        dto.setOrp(350.0);

        // POST — create
        ResponseEntity<ApiResponseDTO<Object>> createResp = restTemplate.exchange(
                "/aquariums/1/settings/targets", HttpMethod.POST,
                new HttpEntity<>(dto, jsonHeaders()), new ParameterizedTypeReference<>() {});

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> createBody = createResp.getBody();
        assertThat(createBody).isNotNull();
        assertThat(createBody.getSuccess()).isTrue();
        TargetParameterResponseDTO created = objectMapper.convertValue(createBody.getData(), TargetParameterResponseDTO.class);
        assertThat(created.getTemperature()).isEqualTo(25.0);
        assertThat(created.getPh()).isEqualTo(8.2);

        // GET — retrieve
        ResponseEntity<ApiResponseDTO<Object>> getResp = restTemplate.exchange(
                "/aquariums/1/settings/targets", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> getBody = getResp.getBody();
        assertThat(getBody).isNotNull();
        assertThat(getBody.getSuccess()).isTrue();
        TargetParameterResponseDTO fetched = objectMapper.convertValue(getBody.getData(), TargetParameterResponseDTO.class);
        assertThat(fetched.getTemperature()).isEqualTo(25.0);

        // POST — update (upsert semantics)
        dto.setTemperature(26.0);
        ResponseEntity<ApiResponseDTO<Object>> updateResp = restTemplate.exchange(
                "/aquariums/1/settings/targets", HttpMethod.POST,
                new HttpEntity<>(dto, jsonHeaders()), new ParameterizedTypeReference<>() {});

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> updateBody = updateResp.getBody();
        assertThat(updateBody).isNotNull();
        TargetParameterResponseDTO updated = objectMapper.convertValue(updateBody.getData(), TargetParameterResponseDTO.class);
        assertThat(updated.getTemperature()).isEqualTo(26.0);

        // GET — unknown aquarium: service returns null → controller returns 200 with null data
        ResponseEntity<ApiResponseDTO<Object>> notConfiguredResp = restTemplate.exchange(
                "/aquariums/99/settings/targets", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(notConfiguredResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notConfiguredResp.getBody()).isNotNull();
        assertThat(notConfiguredResp.getBody().getSuccess()).isTrue();
        assertThat(notConfiguredResp.getBody().getData()).isNull();
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
