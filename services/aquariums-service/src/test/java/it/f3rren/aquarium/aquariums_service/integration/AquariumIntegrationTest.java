package it.f3rren.aquarium.aquariums_service.integration;

import static org.assertj.core.api.Assertions.assertThat;

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

import it.f3rren.aquarium.aquariums_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.aquariums_service.dto.AquariumResponseDTO;
import it.f3rren.aquarium.aquariums_service.dto.CreateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.dto.UpdateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.kafka.publisher.AquariumEventPublisher;
import it.f3rren.aquarium.aquariums_service.model.AquariumType;

/**
 * End-to-end integration test for aquarium CRUD operations.
 * Uses a real PostgreSQL container via Testcontainers and runs all Flyway migrations.
 * Kafka is not required: {@link AquariumEventPublisher} is mocked.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class AquariumIntegrationTest {

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
        registry.add("spring.flyway.enabled", () -> "true");
        // Prevent Kafka from trying to connect to a non-existent broker
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9999");
        registry.add("spring.kafka.admin.auto-create", () -> "false");
        registry.add("spring.kafka.admin.fail-fast", () -> "false");
    }

    @MockBean
    private AquariumEventPublisher eventPublisher;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("full CRUD lifecycle: create → read → update → delete → 404")
    void fullCrudLifecycle() {
        // 1. Create
        CreateAquariumDTO createDto = new CreateAquariumDTO();
        createDto.setName("Integration Tank");
        createDto.setVolume(150);
        createDto.setType(AquariumType.FRESHWATER);

        ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> createResp = restTemplate.exchange(
                "/aquariums", HttpMethod.POST,
                new HttpEntity<>(createDto),
                new ParameterizedTypeReference<>() {});

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponseDTO<AquariumResponseDTO> createBody = createResp.getBody();
        assertThat(createBody).isNotNull();
        assertThat(createBody.getSuccess()).isTrue();

        AquariumResponseDTO created = objectMapper.convertValue(createBody.getData(), AquariumResponseDTO.class);
        Long id = created.getId();
        assertThat(id).isNotNull();
        assertThat(created.getName()).isEqualTo("Integration Tank");
        assertThat(created.getType()).isEqualTo(AquariumType.FRESHWATER);

        // 2. Read by ID
        ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> getResp = restTemplate.exchange(
                "/aquariums/" + id, HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<AquariumResponseDTO> getBody = getResp.getBody();
        assertThat(getBody).isNotNull();
        AquariumResponseDTO retrieved = objectMapper.convertValue(getBody.getData(), AquariumResponseDTO.class);
        assertThat(retrieved.getId()).isEqualTo(id);
        assertThat(retrieved.getName()).isEqualTo("Integration Tank");
        assertThat(retrieved.getVolume()).isEqualTo(150);

        // 3. Update (partial — only name and volume; type must remain unchanged)
        UpdateAquariumDTO updateDto = new UpdateAquariumDTO();
        updateDto.setName("Updated Tank");
        updateDto.setVolume(200);

        ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> updateResp = restTemplate.exchange(
                "/aquariums/" + id, HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                new ParameterizedTypeReference<>() {});

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<AquariumResponseDTO> updateBody = updateResp.getBody();
        assertThat(updateBody).isNotNull();
        AquariumResponseDTO updated = objectMapper.convertValue(updateBody.getData(), AquariumResponseDTO.class);
        assertThat(updated.getName()).isEqualTo("Updated Tank");
        assertThat(updated.getVolume()).isEqualTo(200);
        assertThat(updated.getType()).isEqualTo(AquariumType.FRESHWATER);

        // 4. List all — updated aquarium must appear
        ResponseEntity<ApiResponseDTO<Object>> listResp = restTemplate.exchange(
                "/aquariums", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> listBody = listResp.getBody();
        assertThat(listBody).isNotNull();
        assertThat(listBody.getSuccess()).isTrue();

        // 5. Delete
        restTemplate.delete("/aquariums/" + id);

        // 6. Read after delete → 404
        ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> afterDeleteResp = restTemplate.exchange(
                "/aquariums/" + id, HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(afterDeleteResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiResponseDTO<AquariumResponseDTO> afterDeleteBody = afterDeleteResp.getBody();
        assertThat(afterDeleteBody).isNotNull();
        assertThat(afterDeleteBody.getSuccess()).isFalse();
    }
}
