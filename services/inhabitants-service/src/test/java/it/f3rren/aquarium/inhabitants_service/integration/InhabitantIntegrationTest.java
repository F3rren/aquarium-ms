package it.f3rren.aquarium.inhabitants_service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
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

import it.f3rren.aquarium.inhabitants_service.client.SpeciesClient;
import it.f3rren.aquarium.inhabitants_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.inhabitants_service.dto.CreateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.dto.FishDTO;
import it.f3rren.aquarium.inhabitants_service.dto.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.dto.UpdateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.kafka.listener.AquariumEventListener;
import it.f3rren.aquarium.inhabitants_service.model.InhabitantType;

/**
 * End-to-end integration test for inhabitant CRUD operations.
 * Uses a real PostgreSQL container via Testcontainers and runs all Flyway migrations.
 * Kafka and the species service are not required: {@link AquariumEventListener} and
 * {@link SpeciesClient} are mocked.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class InhabitantIntegrationTest {

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
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "inhabitants");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.schemas", () -> "inhabitants");
        // Prevent Kafka from trying to connect to a non-existent broker
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9999");
        registry.add("spring.kafka.listener.auto-startup", () -> "false");
    }

    @MockBean
    private AquariumEventListener eventListener;

    @MockBean
    private SpeciesClient speciesClient;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpMocks() {
        FishDTO fish = new FishDTO();
        fish.setId(1L);
        fish.setCommonName("Clownfish");
        fish.setScientificName("Amphiprioninae");
        when(speciesClient.getFishById(anyLong())).thenReturn(fish);
    }

    @Test
    @DisplayName("full CRUD lifecycle: add → list → update → delete → empty list")
    void fullCrudLifecycle() {
        Long aquariumId = 42L;

        // 1. Add inhabitant
        CreateInhabitantDTO createDto = new CreateInhabitantDTO();
        createDto.setInhabitantType(InhabitantType.FISH);
        createDto.setInhabitantId(1L);
        createDto.setQuantity(2);

        ResponseEntity<ApiResponseDTO<InhabitantDetailsDTO>> addResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/inhabitants", HttpMethod.POST,
                new HttpEntity<>(createDto),
                new ParameterizedTypeReference<>() {});

        assertThat(addResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponseDTO<InhabitantDetailsDTO> addBody = addResp.getBody();
        assertThat(addBody).isNotNull();
        assertThat(addBody.getSuccess()).isTrue();

        InhabitantDetailsDTO added = objectMapper.convertValue(addBody.getData(), InhabitantDetailsDTO.class);
        Long id = added.getId();
        assertThat(id).isNotNull();
        assertThat(added.getType()).isEqualTo("fish");
        assertThat(added.getQuantity()).isEqualTo(2);

        // 2. List inhabitants — added entry must appear
        ResponseEntity<ApiResponseDTO<Object>> listResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/inhabitants", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> listBody = listResp.getBody();
        assertThat(listBody).isNotNull();
        assertThat(listBody.getSuccess()).isTrue();

        // 3. Update quantity
        UpdateInhabitantDTO updateDto = new UpdateInhabitantDTO();
        updateDto.setQuantity(5);

        ResponseEntity<ApiResponseDTO<InhabitantDetailsDTO>> updateResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/inhabitants/" + id, HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                new ParameterizedTypeReference<>() {});

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<InhabitantDetailsDTO> updateBody = updateResp.getBody();
        assertThat(updateBody).isNotNull();
        InhabitantDetailsDTO updated = objectMapper.convertValue(updateBody.getData(), InhabitantDetailsDTO.class);
        assertThat(updated.getQuantity()).isEqualTo(5);
        assertThat(updated.getType()).isEqualTo("fish");

        // 4. Delete
        restTemplate.delete("/aquariums/" + aquariumId + "/inhabitants/" + id);

        // 5. List after delete → empty
        ResponseEntity<ApiResponseDTO<Object>> afterDeleteResp = restTemplate.exchange(
                "/aquariums/" + aquariumId + "/inhabitants", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(afterDeleteResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> afterDeleteBody = afterDeleteResp.getBody();
        assertThat(afterDeleteBody).isNotNull();
        assertThat(afterDeleteBody.getSuccess()).isTrue();
        assertThat(afterDeleteBody.getData()).asInstanceOf(LIST).isEmpty();
    }
}
