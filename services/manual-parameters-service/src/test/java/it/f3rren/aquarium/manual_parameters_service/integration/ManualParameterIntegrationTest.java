package it.f3rren.aquarium.manual_parameters_service.integration;

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

import it.f3rren.aquarium.manual_parameters_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.manual_parameters_service.dto.CreateManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.dto.ManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.kafka.listener.AquariumEventListener;

/**
 * End-to-end integration test for manual parameter read/write operations.
 * Uses a real PostgreSQL container via Testcontainers and runs all Flyway migrations.
 * Kafka is not required: {@link AquariumEventListener} is mocked.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class ManualParameterIntegrationTest {

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
        registry.add("spring.flyway.table", () -> "flyway_schema_history_manual");
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
        CreateManualParameterDTO createDto = new CreateManualParameterDTO();
        createDto.setCalcium(420.0);
        createDto.setMagnesium(1300.0);
        createDto.setKh(8.5);

        ResponseEntity<ApiResponseDTO<ManualParameterDTO>> createResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/parameters/manual", HttpMethod.POST,
                new HttpEntity<>(createDto),
                new ParameterizedTypeReference<>() {});

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponseDTO<ManualParameterDTO> createBody = createResp.getBody();
        assertThat(createBody).isNotNull();
        assertThat(createBody.getSuccess()).isTrue();

        ManualParameterDTO created = objectMapper.convertValue(createBody.getData(), ManualParameterDTO.class);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getCalcium()).isEqualTo(420.0);
        assertThat(created.getMeasuredAt()).isNotNull();

        // 2. Get latest — must return the created parameter
        ResponseEntity<ApiResponseDTO<ManualParameterDTO>> latestResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/parameters/manual", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(latestResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<ManualParameterDTO> latestBody = latestResp.getBody();
        assertThat(latestBody).isNotNull();
        assertThat(latestBody.getSuccess()).isTrue();

        ManualParameterDTO latest = objectMapper.convertValue(latestBody.getData(), ManualParameterDTO.class);
        assertThat(latest.getId()).isEqualTo(created.getId());

        // 3. Get history — must contain at least the created parameter
        ResponseEntity<ApiResponseDTO<Object>> historyResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/parameters/manual/history", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(historyResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> historyBody = historyResp.getBody();
        assertThat(historyBody).isNotNull();
        assertThat(historyBody.getSuccess()).isTrue();
        assertThat(historyBody.getData()).asInstanceOf(LIST).hasSize(1);
    }
}
