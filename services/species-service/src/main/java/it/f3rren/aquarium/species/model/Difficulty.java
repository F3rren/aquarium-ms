package it.f3rren.aquarium.species.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Difficulty {
    EASY("easy"), MEDIUM("medium"), HARD("hard");

    private final String value;

    Difficulty(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Difficulty fromValue(String value) {
        for (Difficulty d : values()) {
            if (d.value.equalsIgnoreCase(value)) return d;
        }
        throw new IllegalArgumentException("Invalid difficulty: '" + value + "'");
    }
}
