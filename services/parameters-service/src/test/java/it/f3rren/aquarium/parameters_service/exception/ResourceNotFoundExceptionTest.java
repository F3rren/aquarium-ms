package it.f3rren.aquarium.parameters_service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Nested
    @DisplayName("Single-arg constructor")
    class SingleArg {

        @Test
        @DisplayName("should set message and leave cause null")
        void shouldSetMessageAndNullCause() {
            ResourceNotFoundException ex =
                    new ResourceNotFoundException("No parameter found for aquarium with ID: 1");

            assertEquals("No parameter found for aquarium with ID: 1", ex.getMessage());
            assertNull(ex.getCause());
            assertInstanceOf(RuntimeException.class, ex);
        }
    }

    @Nested
    @DisplayName("Two-arg constructor")
    class TwoArg {

        @Test
        @DisplayName("should set both message and cause")
        void shouldSetMessageAndCause() {
            Throwable cause = new IllegalStateException("root cause");
            ResourceNotFoundException ex = new ResourceNotFoundException("Resource missing", cause);

            assertEquals("Resource missing", ex.getMessage());
            assertSame(cause, ex.getCause());
            assertInstanceOf(RuntimeException.class, ex);
        }
    }
}
