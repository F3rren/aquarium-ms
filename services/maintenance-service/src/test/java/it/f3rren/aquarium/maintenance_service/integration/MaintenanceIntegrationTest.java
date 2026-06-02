package it.f3rren.aquarium.maintenance_service.integration;

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

import it.f3rren.aquarium.maintenance_service.dto.request.CreateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.request.UpdateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.response.MaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.kafka.listener.AquariumEventListener;
import it.f3rren.aquarium.maintenance_service.dto.ApiResponseDTO;

/**
 * End-to-end integration test for maintenance task CRUD operations.
 * Uses a real PostgreSQL container via Testcontainers and runs all Flyway migrations.
 * Kafka is not required: {@link AquariumEventListener} is mocked.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class MaintenanceIntegrationTest {

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
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "maintenance");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.schemas", () -> "maintenance");
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
    @DisplayName("full CRUD lifecycle: create → list → update → complete → delete → empty list")
    void fullCrudLifecycle() {
        Long aquariumId = 42L;

        // 1. Create task
        CreateMaintenanceTaskDTO createDto = new CreateMaintenanceTaskDTO();
        createDto.setTitle("Water change");

        ResponseEntity<ApiResponseDTO<MaintenanceTaskDTO>> createResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/tasks", HttpMethod.POST,
                new HttpEntity<>(createDto),
                new ParameterizedTypeReference<>() {});

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponseDTO<MaintenanceTaskDTO> createBody = createResp.getBody();
        assertThat(createBody).isNotNull();
        assertThat(createBody.getSuccess()).isTrue();

        MaintenanceTaskDTO created = objectMapper.convertValue(createBody.getData(), MaintenanceTaskDTO.class);
        Long id = created.getId();
        assertThat(id).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Water change");
        assertThat(created.getIsCompleted()).isFalse();

        // 2. List tasks — created task must appear
        ResponseEntity<ApiResponseDTO<Object>> listResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/tasks", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> listBody = listResp.getBody();
        assertThat(listBody).isNotNull();
        assertThat(listBody.getSuccess()).isTrue();

        // 3. Update title
        UpdateMaintenanceTaskDTO updateDto = new UpdateMaintenanceTaskDTO();
        updateDto.setTitle("Water change (updated)");

        ResponseEntity<ApiResponseDTO<MaintenanceTaskDTO>> updateResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/tasks/" + id, HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                new ParameterizedTypeReference<>() {});

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<MaintenanceTaskDTO> updateBody = updateResp.getBody();
        assertThat(updateBody).isNotNull();
        MaintenanceTaskDTO updated = objectMapper.convertValue(updateBody.getData(), MaintenanceTaskDTO.class);
        assertThat(updated.getTitle()).isEqualTo("Water change (updated)");

        // 4. Complete task
        ResponseEntity<ApiResponseDTO<MaintenanceTaskDTO>> completeResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/tasks/" + id + "/complete", HttpMethod.POST,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(completeResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<MaintenanceTaskDTO> completeBody = completeResp.getBody();
        assertThat(completeBody).isNotNull();
        MaintenanceTaskDTO completed = objectMapper.convertValue(completeBody.getData(), MaintenanceTaskDTO.class);
        assertThat(completed.getIsCompleted()).isTrue();
        assertThat(completed.getCompletedAt()).isNotNull();

        // 5. Delete
        restTemplate.delete("/aquariums/" + aquariumId + "/tasks/" + id);

        // 6. List after delete → empty
        ResponseEntity<ApiResponseDTO<Object>> afterDeleteResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/tasks", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(afterDeleteResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> afterDeleteBody = afterDeleteResp.getBody();
        assertThat(afterDeleteBody).isNotNull();
        assertThat(afterDeleteBody.getData()).asInstanceOf(LIST).isEmpty();
    }
}
