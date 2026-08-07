package it.f3rren.aquarium.species.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlowRequirement {
    LOW("low"), MEDIUM("medium"), HIGH("high");

    private final String value;

    FlowRequirement(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FlowRequirement fromValue(String value) {
        for (FlowRequirement f : values()) {
            if (f.value.equalsIgnoreCase(value)) return f;
        }
        throw new IllegalArgumentException("Invalid flow requirement: '" + value + "'");
    }
}
