package it.f3rren.aquarium.inhabitants_service.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InhabitantTypeTest {

    @Nested
    @DisplayName("fromValue — valid inputs")
    class ValidInputs {

        @Test
        @DisplayName("should return FISH for 'fish' (case-insensitive)")
        void shouldReturnFishForFish() {
            assertEquals(InhabitantType.FISH, InhabitantType.fromValue("fish"));
            assertEquals(InhabitantType.FISH, InhabitantType.fromValue("FISH"));
            assertEquals(InhabitantType.FISH, InhabitantType.fromValue("Fish"));
        }

        @Test
        @DisplayName("should return CORAL for 'coral' (case-insensitive)")
        void shouldReturnCoralForCoral() {
            assertEquals(InhabitantType.CORAL, InhabitantType.fromValue("coral"));
            assertEquals(InhabitantType.CORAL, InhabitantType.fromValue("CORAL"));
        }
    }

    @Nested
    @DisplayName("fromValue — invalid input")
    class InvalidInput {

        @Test
        @DisplayName("should throw IllegalArgumentException for unrecognised value")
        void shouldThrowForUnknownType() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> InhabitantType.fromValue("shrimp")
            );
            assertTrue(ex.getMessage().contains("shrimp"));
        }
    }

    @Nested
    @DisplayName("getValue")
    class GetValue {

        @Test
        @DisplayName("should return the JSON-serialised string value")
        void shouldReturnStringValue() {
            assertEquals("fish", InhabitantType.FISH.getValue());
            assertEquals("coral", InhabitantType.CORAL.getValue());
        }
    }
}
