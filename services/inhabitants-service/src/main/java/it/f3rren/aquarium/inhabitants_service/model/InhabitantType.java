package it.f3rren.aquarium.inhabitants_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InhabitantType {
    FISH("fish"),
    CORAL("coral");

    private final String value;

    InhabitantType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InhabitantType fromValue(String value) {
        for (InhabitantType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid inhabitant type: '" + value + "'. Must be 'fish' or 'coral'");
    }
}
