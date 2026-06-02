package it.f3rren.aquarium.aquariums_service.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseDTOTest {

    @Nested
    @DisplayName("success(String message, T data)")
    class SuccessWithData {

        @Test
        @DisplayName("should set success=true, message, data and null metadata")
        void shouldCreateSuccessWithData() {
            ApiResponseDTO<String> result = ApiResponseDTO.success("Created", "payload");

            assertTrue(result.getSuccess());
            assertEquals("Created", result.getMessage());
            assertEquals("payload", result.getData());
            assertNull(result.getMetadata());
        }
    }

    @Nested
    @DisplayName("success(String message) — no data")
    class SuccessWithoutData {

        @Test
        @DisplayName("should set success=true, message and null data and metadata")
        void shouldCreateSuccessWithoutData() {
            ApiResponseDTO<Void> result = ApiResponseDTO.success("Deleted");

            assertTrue(result.getSuccess());
            assertEquals("Deleted", result.getMessage());
            assertNull(result.getData());
            assertNull(result.getMetadata());
        }
    }

    @Nested
    @DisplayName("No-args constructor")
    class NoArgsConstructorTest {

        @Test
        @DisplayName("should create instance with all null fields")
        void shouldCreateWithNullFields() {
            ApiResponseDTO<String> dto = new ApiResponseDTO<>();

            assertNull(dto.getSuccess());
            assertNull(dto.getMessage());
            assertNull(dto.getData());
            assertNull(dto.getMetadata());
        }
    }

    @Nested
    @DisplayName("All-args constructor")
    class AllArgsConstructorTest {

        @Test
        @DisplayName("should set all fields from constructor arguments")
        void shouldSetAllFields() {
            Map<String, String> meta = Map.of("page", "1");
            ApiResponseDTO<Integer> dto = new ApiResponseDTO<>(true, "ok", 42, meta);

            assertTrue(dto.getSuccess());
            assertEquals("ok", dto.getMessage());
            assertEquals(42, dto.getData());
            assertEquals(meta, dto.getMetadata());
        }
    }

    @Nested
    @DisplayName("equals and hashCode (Lombok @Data)")
    class EqualsHashCode {

        @Test
        @DisplayName("two instances with identical fields should be equal and have the same hash code")
        void equalInstancesShouldBeEqualAndHaveSameHashCode() {
            ApiResponseDTO<String> a = new ApiResponseDTO<>(true, "msg", "data", null);
            ApiResponseDTO<String> b = new ApiResponseDTO<>(true, "msg", "data", null);

            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        @DisplayName("instances with different fields should not be equal")
        void differentInstancesShouldNotBeEqual() {
            ApiResponseDTO<String> a = new ApiResponseDTO<>(true, "msg", "data", null);
            ApiResponseDTO<String> b = new ApiResponseDTO<>(false, "other", null, null);

            assertNotEquals(a, b);
        }
    }

    @Nested
    @DisplayName("toString (Lombok @Data)")
    class ToStringTest {

        @Test
        @DisplayName("toString should contain field values")
        void toStringShouldContainFieldValues() {
            ApiResponseDTO<String> dto = new ApiResponseDTO<>(true, "hello", "world", null);
            String str = dto.toString();

            assertTrue(str.contains("true"));
            assertTrue(str.contains("hello"));
            assertTrue(str.contains("world"));
        }
    }
}
