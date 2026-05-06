package it.f3rren.aquarium.aquariums_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AquariumTest {

    @Nested
    @DisplayName("Convenience constructor Aquarium(String, int)")
    class ConvenienceConstructor {

        @Test
        @DisplayName("should set name and volume; all other fields remain null/zero")
        void shouldSetNameAndVolume() {
            Aquarium aquarium = new Aquarium("Reef Tank", 200);

            assertEquals("Reef Tank", aquarium.getName());
            assertEquals(200, aquarium.getVolume());
            assertNull(aquarium.getId());
            assertNull(aquarium.getType());
            assertNull(aquarium.getCreatedAt());
            assertNull(aquarium.getDescription());
            assertNull(aquarium.getImageUrl());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters/getters")
    class NoArgsConstructor {

        private Aquarium aquarium;

        @BeforeEach
        void setUp() {
            aquarium = new Aquarium();
        }

        @Test
        @DisplayName("should allow setting and reading all fields individually")
        void shouldAllowIndividualFieldAccess() {
            LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);

            aquarium.setId(1L);
            aquarium.setName("Freshwater Tank");
            aquarium.setVolume(100);
            aquarium.setType("freshwater");
            aquarium.setCreatedAt(now);
            aquarium.setDescription("A peaceful tank");
            aquarium.setImageUrl("https://example.com/tank.jpg");

            assertEquals(1L, aquarium.getId());
            assertEquals("Freshwater Tank", aquarium.getName());
            assertEquals(100, aquarium.getVolume());
            assertEquals("freshwater", aquarium.getType());
            assertEquals(now, aquarium.getCreatedAt());
            assertEquals("A peaceful tank", aquarium.getDescription());
            assertEquals("https://example.com/tank.jpg", aquarium.getImageUrl());
        }
    }

    @Nested
    @DisplayName("All-args constructor")
    class AllArgsConstructor {

        @Test
        @DisplayName("should populate all seven fields from constructor arguments")
        void shouldPopulateAllFields() {
            LocalDateTime now = LocalDateTime.of(2025, 6, 15, 10, 30);

            Aquarium aquarium = new Aquarium(
                    1L,
                    "Saltwater Tank",
                    350,
                    "saltwater",
                    now,
                    "A reef aquarium",
                    "https://example.com/reef.jpg"
            );

            assertEquals(1L, aquarium.getId());
            assertEquals("Saltwater Tank", aquarium.getName());
            assertEquals(350, aquarium.getVolume());
            assertEquals("saltwater", aquarium.getType());
            assertEquals(now, aquarium.getCreatedAt());
            assertEquals("A reef aquarium", aquarium.getDescription());
            assertEquals("https://example.com/reef.jpg", aquarium.getImageUrl());
        }
    }
}
