package it.f3rren.aquarium.species.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.f3rren.aquarium.species.dto.ApiResponseDTO;
import it.f3rren.aquarium.species.dto.FishResponseDTO;
import it.f3rren.aquarium.species.model.Difficulty;
import it.f3rren.aquarium.species.model.Fish;
import it.f3rren.aquarium.species.model.FishDiet;
import it.f3rren.aquarium.species.model.FishTemperament;
import it.f3rren.aquarium.species.model.WaterType;
import it.f3rren.aquarium.species.model.Coral;
import it.f3rren.aquarium.species.model.CoralFeeding;
import it.f3rren.aquarium.species.model.CoralPlacement;
import it.f3rren.aquarium.species.model.FlowRequirement;
import it.f3rren.aquarium.species.model.LightRequirement;
import it.f3rren.aquarium.species.repository.ICoralRepository;
import it.f3rren.aquarium.species.repository.IFishRepository;

/**
 * End-to-end integration test for the species read API.
 * Uses a real PostgreSQL container via Testcontainers. No Kafka or external HTTP clients.
 * Data is pre-populated via repositories since the API is read-only.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class SpeciesIntegrationTest {

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
        registry.add("spring.flyway.default-schema", () -> "inhabitants");
        registry.add("spring.flyway.table", () -> "flyway_schema_history_species");
    }

    @Autowired
    private IFishRepository fishRepository;

    @Autowired
    private ICoralRepository coralRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        fishRepository.deleteAll();
        coralRepository.deleteAll();
    }

    @Test
    @DisplayName("read fish: list → get by id → 404 for unknown id")
    void readFish() {
        Fish saved = fishRepository.save(Fish.builder()
                .commonName("Clownfish")
                .scientificName("Amphiprion ocellaris")
                .family("Pomacentridae")
                .minTankSize(30)
                .maxSize(11)
                .difficulty(Difficulty.EASY)
                .isReefSafe(true)
                .temperament(FishTemperament.PEACEFUL)
                .diet(FishDiet.OMNIVORE)
                .waterType(WaterType.MARINE)
                .build());

        // GET /species/fish — list
        ResponseEntity<ApiResponseDTO<Object>> listResp = restTemplate.exchange(
                "/species/fish", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> listBody = listResp.getBody();
        assertThat(listBody).isNotNull();
        assertThat(listBody.getSuccess()).isTrue();
        assertThat(listBody.getData()).asInstanceOf(LIST).hasSize(1);

        // GET /species/fish/{id} — by id
        ResponseEntity<ApiResponseDTO<FishResponseDTO>> byIdResp = restTemplate.exchange(
                "/species/fish/" + saved.getId(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<FishResponseDTO> byIdBody = byIdResp.getBody();
        assertThat(byIdBody).isNotNull();
        FishResponseDTO fish = objectMapper.convertValue(byIdBody.getData(), FishResponseDTO.class);
        assertThat(fish.getCommonName()).isEqualTo("Clownfish");
        assertThat(fish.getDifficulty()).isEqualTo("easy");

        // GET /species/fish/99999 — not found
        ResponseEntity<ApiResponseDTO<Object>> notFoundResp = restTemplate.exchange(
                "/species/fish/99999", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(notFoundResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("read corals: list → get by id")
    void readCorals() {
        Coral saved = coralRepository.save(Coral.builder()
                .commonName("Hammer Coral")
                .scientificName("Euphyllia ancora")
                .type("LPS")
                .minTankSize(100)
                .maxSize(30)
                .difficulty(Difficulty.MEDIUM)
                .lightRequirement(LightRequirement.MEDIUM)
                .flowRequirement(FlowRequirement.MEDIUM)
                .placement(CoralPlacement.BOTTOM)
                .isAggressive(false)
                .feeding(CoralFeeding.OCCASIONAL)
                .description("A popular LPS coral with flowing tentacles.")
                .build());

        // GET /species/corals — list
        ResponseEntity<ApiResponseDTO<Object>> listResp = restTemplate.exchange(
                "/species/corals", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> listBody = listResp.getBody();
        assertThat(listBody).isNotNull();
        assertThat(listBody.getSuccess()).isTrue();
        assertThat(listBody.getData()).asInstanceOf(LIST).hasSize(1);

        // GET /species/corals/{id} — by id
        ResponseEntity<ApiResponseDTO<Object>> byIdResp = restTemplate.exchange(
                "/species/corals/" + saved.getId(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponseDTO<Object> byIdBody = byIdResp.getBody();
        assertThat(byIdBody).isNotNull();
        assertThat(byIdBody.getSuccess()).isTrue();
    }
}
