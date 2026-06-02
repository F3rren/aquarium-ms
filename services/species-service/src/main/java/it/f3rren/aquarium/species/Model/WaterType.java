package it.f3rren.aquarium.species.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WaterType {
    MARINE("marine"), FRESHWATER("freshwater"), BRACKISH("brackish");

    private final String value;

    WaterType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static WaterType fromValue(String value) {
        for (WaterType w : values()) {
            if (w.value.equalsIgnoreCase(value)) return w;
        }
        throw new IllegalArgumentException("Invalid water type: '" + value + "'");
    }
}
