package it.f3rren.aquarium.species.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FishTemperament {
    PEACEFUL("peaceful"), SEMI_AGGRESSIVE("semi_aggressive"), AGGRESSIVE("aggressive");

    private final String value;

    FishTemperament(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FishTemperament fromValue(String value) {
        for (FishTemperament t : values()) {
            if (t.value.equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Invalid fish temperament: '" + value + "'");
    }
}
