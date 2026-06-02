package it.f3rren.aquarium.species.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CoralPlacement {
    BOTTOM("bottom"), MIDDLE("middle"), TOP("top");

    private final String value;

    CoralPlacement(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CoralPlacement fromValue(String value) {
        for (CoralPlacement p : values()) {
            if (p.value.equalsIgnoreCase(value)) return p;
        }
        throw new IllegalArgumentException("Invalid coral placement: '" + value + "'");
    }
}
