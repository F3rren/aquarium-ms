package it.f3rren.aquarium.species.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CoralFeeding {
    NO_FEEDING("no_feeding"), OCCASIONAL("occasional"), REGULAR("regular");

    private final String value;

    CoralFeeding(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CoralFeeding fromValue(String value) {
        for (CoralFeeding f : values()) {
            if (f.value.equalsIgnoreCase(value)) return f;
        }
        throw new IllegalArgumentException("Invalid coral feeding: '" + value + "'");
    }
}
