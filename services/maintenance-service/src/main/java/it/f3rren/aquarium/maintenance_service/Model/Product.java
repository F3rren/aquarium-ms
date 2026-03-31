package it.f3rren.aquarium.maintenance_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ProductCategory category;
    
    @Column(name = "brand")
    private String brand;
    
    @Column(name = "quantity")
    private Double quantity;
    
    @Column(name = "unit")
    private String unit; // ml, g, pcs, etc.
    
    @Column(name = "cost")
    private Double cost;
    
    @Column(name = "currency")
    private String currency = "€";
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = false;
    
    @Column(name = "usage_frequency")
    private Integer usageFrequency; // days between uses
    
    @Column(name = "last_used")
    private LocalDate lastUsed;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Helper methods
    @Transient
    public boolean isExpired() {
        if (expiryDate == null) return false;
        return LocalDate.now().isAfter(expiryDate);
    }
    
    @Transient
    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        return daysUntilExpiry <= 30 && daysUntilExpiry > 0;
    }
    
    @Transient
    public boolean isLowStock() {
        if (quantity == null) return false;
        return quantity < 20; // Customizable threshold
    }
    
    @Transient
    public Long getDaysSinceLastUse() {
        if (lastUsed == null) return null;
        return ChronoUnit.DAYS.between(lastUsed, LocalDate.now());
    }
    
    @Transient
    public boolean shouldUseAgain() {
        if (usageFrequency == null || lastUsed == null) return false;
        Long daysSinceLastUse = getDaysSinceLastUse();
        return daysSinceLastUse != null && daysSinceLastUse >= usageFrequency;
    }
}
