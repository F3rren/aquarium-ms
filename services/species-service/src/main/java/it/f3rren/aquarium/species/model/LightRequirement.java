package it.f3rren.aquarium.species.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LightRequirement {
    LOW("low"), MEDIUM("medium"), HIGH("high");

    private final String value;

    LightRequirement(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LightRequirement fromValue(String value) {
        for (LightRequirement l : values()) {
            if (l.value.equalsIgnoreCase(value)) return l;
        }
        throw new IllegalArgumentException("Invalid light requirement: '" + value + "'");
    }
}
