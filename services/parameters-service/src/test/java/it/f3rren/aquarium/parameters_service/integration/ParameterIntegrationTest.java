package it.f3rren.aquarium.parameters_service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.f3rren.aquarium.parameters_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.parameters_service.dto.CreateParameterDTO;
import it.f3rren.aquarium.parameters_service.dto.ParameterDTO;
import it.f3rren.aquarium.parameters_service.kafka.listener.AquariumEventListener;

/**
 * End-to-end integration test for water parameter read/write operations.
 * Uses a real PostgreSQL container via Testcontainers and runs all Flyway migrations.
 * Kafka is not required: {@link AquariumEventListener} is mocked.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class ParameterIntegrationTest {

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
        registry.add("spring.flyway.table", () -> "flyway_schema_history_water");
        // Prevent Kafka from trying to connect to a non-existent broker
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9999");
        registry.add("spring.kafka.listener.auto-startup", () -> "false");
    }

    @MockBean
    private AquariumEventListener eventListener;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("create → get latest → get history")
    void createAndRetrieveParameters() {
        Long aquariumId = 42L;

        // 1. Create parameter
        CreateParameterDTO createDto = new CreateParameterDTO();
        createDto.setTemperature(25.5);
        createDto.setPh(8.2);
        createDto.setSalinity(35);
        createDto.setOrp(350);

        ResponseEntity<ApiResponseDTO<ParameterDTO>> createResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/parameters", HttpMethod.POST,
                new HttpEntity<>(createDto),
                new ParameterizedTypeReference<>() {});

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponseDTO<ParameterDTO> createBody = createResp.getBody();
        assertThat(createBody).isNotNull();
        assertThat(createBody.getSuccess()).isTrue();

        ParameterDTO created = objectMapper.convertValue(createBody.getData(), ParameterDTO.class);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTemperature()).isEqualTo(25.5);
        assertThat(created.getMeasuredAt()).isNotNull();

        // 2. Get latest — must return the created parameter
        ResponseEntity<ApiResponseDTO<ParameterDTO>> latestResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/parameters/latest", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(latestResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<ParameterDTO> latestBody = latestResp.getBody();
        assertThat(latestBody).isNotNull();
        assertThat(latestBody.getSuccess()).isTrue();

        ParameterDTO latest = objectMapper.convertValue(latestBody.getData(), ParameterDTO.class);
        assertThat(latest.getId()).isEqualTo(created.getId());

        // 3. Get history — must contain the created parameter
        ResponseEntity<ApiResponseDTO<Object>> historyResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/parameters/history", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(historyResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> historyBody = historyResp.getBody();
        assertThat(historyBody).isNotNull();
        assertThat(historyBody.getSuccess()).isTrue();
        assertThat(historyBody.getData()).asInstanceOf(LIST).hasSize(1);
    }
}
