package it.f3rren.aquarium.aquariums_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Type of an aquarium, restricting the {@code type} field to a known, finite set of values.
 *
 * <p>Persisted as a string via {@link jakarta.persistence.EnumType#STRING} (enum name,
 * e.g. {@code "SALTWATER"}) and exposed over the REST API in lower case
 * (e.g. {@code "saltwater"}) to preserve the existing public contract.</p>
 *
 * @author F3rren
 */
public enum AquariumType {

    SALTWATER,
    FRESHWATER;

    /**
     * JSON representation of the value: the lower-case form of the enum name.
     *
     * @return {@code "saltwater"} or {@code "freshwater"}
     */
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    /**
     * Builds the enum from its JSON representation, accepting any letter case.
     *
     * @param value incoming string (e.g. {@code "saltwater"}, {@code "SALTWATER"})
     * @return the matching {@link AquariumType}, or {@code null} if the input is {@code null}
     * @throws IllegalArgumentException if the value does not match a known type
     */
    @JsonCreator
    public static AquariumType fromJson(String value) {
        if (value == null) {
            return null;
        }
        return AquariumType.valueOf(value.trim().toUpperCase());
    }
}
