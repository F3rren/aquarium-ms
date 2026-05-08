package it.f3rren.aquarium.maintenance_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
    }

    @Nested
    @DisplayName("isExpired()")
    class IsExpired {

        @Test
        @DisplayName("should return false when expiryDate is null")
        void shouldReturnFalseWhenNullExpiryDate() {
            product.setExpiryDate(null);
            assertFalse(product.isExpired());
        }

        @Test
        @DisplayName("should return true when expiryDate is in the past")
        void shouldReturnTrueWhenExpired() {
            product.setExpiryDate(LocalDate.now().minusDays(1));
            assertTrue(product.isExpired());
        }

        @Test
        @DisplayName("should return false when expiryDate is in the future")
        void shouldReturnFalseWhenNotExpired() {
            product.setExpiryDate(LocalDate.now().plusDays(10));
            assertFalse(product.isExpired());
        }
    }

    @Nested
    @DisplayName("isExpiringSoon()")
    class IsExpiringSoon {

        @Test
        @DisplayName("should return false when expiryDate is null")
        void shouldReturnFalseWhenNullExpiryDate() {
            product.setExpiryDate(null);
            assertFalse(product.isExpiringSoon());
        }

        @Test
        @DisplayName("should return true when expiry is within 30 days")
        void shouldReturnTrueWhenExpiringSoon() {
            product.setExpiryDate(LocalDate.now().plusDays(15));
            assertTrue(product.isExpiringSoon());
        }

        @Test
        @DisplayName("should return false when expiry is more than 30 days away")
        void shouldReturnFalseWhenNotExpiringSoon() {
            product.setExpiryDate(LocalDate.now().plusDays(60));
            assertFalse(product.isExpiringSoon());
        }

        @Test
        @DisplayName("should return false when already expired (daysUntilExpiry <= 0)")
        void shouldReturnFalseWhenAlreadyExpired() {
            product.setExpiryDate(LocalDate.now().minusDays(5));
            assertFalse(product.isExpiringSoon());
        }
    }

    @Nested
    @DisplayName("isLowStock()")
    class IsLowStock {

        @Test
        @DisplayName("should return false when quantity is null")
        void shouldReturnFalseWhenNullQuantity() {
            product.setQuantity(null);
            assertFalse(product.isLowStock());
        }

        @Test
        @DisplayName("should return true when quantity is below threshold")
        void shouldReturnTrueWhenLowStock() {
            product.setQuantity(10.0);
            assertTrue(product.isLowStock());
        }

        @Test
        @DisplayName("should return false when quantity is at or above threshold")
        void shouldReturnFalseWhenSufficientStock() {
            product.setQuantity(50.0);
            assertFalse(product.isLowStock());
        }
    }

    @Nested
    @DisplayName("getDaysSinceLastUse()")
    class GetDaysSinceLastUse {

        @Test
        @DisplayName("should return null when lastUsed is null")
        void shouldReturnNullWhenLastUsedIsNull() {
            product.setLastUsed(null);
            assertNull(product.getDaysSinceLastUse());
        }

        @Test
        @DisplayName("should return correct number of days since last use")
        void shouldReturnDayCount() {
            product.setLastUsed(LocalDate.now().minusDays(7));
            assertEquals(7L, product.getDaysSinceLastUse());
        }
    }

    @Nested
    @DisplayName("shouldUseAgain()")
    class ShouldUseAgain {

        @Test
        @DisplayName("should return false when usageFrequency is null")
        void shouldReturnFalseWhenNullFrequency() {
            product.setUsageFrequency(null);
            product.setLastUsed(LocalDate.now().minusDays(10));
            assertFalse(product.shouldUseAgain());
        }

        @Test
        @DisplayName("should return false when lastUsed is null")
        void shouldReturnFalseWhenLastUsedIsNull() {
            product.setUsageFrequency(7);
            product.setLastUsed(null);
            assertFalse(product.shouldUseAgain());
        }

        @Test
        @DisplayName("should return true when days since last use >= usageFrequency")
        void shouldReturnTrueWhenOverdue() {
            product.setUsageFrequency(7);
            product.setLastUsed(LocalDate.now().minusDays(10));
            assertTrue(product.shouldUseAgain());
        }

        @Test
        @DisplayName("should return false when days since last use < usageFrequency")
        void shouldReturnFalseWhenNotYetDue() {
            product.setUsageFrequency(14);
            product.setLastUsed(LocalDate.now().minusDays(3));
            assertFalse(product.shouldUseAgain());
        }
    }
}
