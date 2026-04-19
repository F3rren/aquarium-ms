package it.f3rren.aquarium.aquariums_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * JPA entity representing a physical aquarium in the system.
 *
 * <p>Mapped to the {@code aquariums} table. An aquarium is the central aggregate
 * around which water parameters, manual measurements, and target values are organized.</p>
 *
 * @author F3rren
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "aquariums")
public class Aquarium {

    /** Primary key, auto-incremented by the database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Display name of the aquarium.
     * Must be between 2 and 100 characters and is trimmed before persistence.
     */
    @NotNull
    @Size(min = 2, max = 100)
    @Column(name = "name")
    private String name;

    /**
     * Water volume in liters. Used to contextualize dosing and parameter targets.
     * Must be at least 1.
     */
    @NotNull
    @Min(1)
    @Column(name = "volume")
    private int volume;

    /**
     * Aquarium type: either {@code "saltwater"} or {@code "freshwater"}.
     * Drives which parameter sets are relevant for this aquarium.
     */
    @NotNull
    @Size(min = 2, max = 50)
    @Column(name = "type")
    private String type;

    /**
     * Timestamp set automatically when the record is first inserted.
     * Never updated after creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Optional free-text description providing additional context about the aquarium
     * (inhabitants, setup notes, etc.). Maximum 500 characters.
     */
    @Size(max = 500)
    @Column(name = "description")
    private String description;

    /**
     * Optional URL pointing to an image of the aquarium.
     * Must be a valid http/https URL. Maximum 2000 characters.
     */
    @Size(max = 2000)
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Convenience constructor for tests and seeders that only need name and volume.
     *
     * @param name   display name
     * @param volume water volume in liters
     */
    public Aquarium(String name, int volume) {
        this.name = name;
        this.volume = volume;
    }
}
