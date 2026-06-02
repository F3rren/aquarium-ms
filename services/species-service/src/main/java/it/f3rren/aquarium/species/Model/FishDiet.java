package it.f3rren.aquarium.species.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FishDiet {
    OMNIVORE("omnivore"), HERBIVORE("herbivore"), CARNIVORE("carnivore");

    private final String value;

    FishDiet(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FishDiet fromValue(String value) {
        for (FishDiet d : values()) {
            if (d.value.equalsIgnoreCase(value)) return d;
        }
        throw new IllegalArgumentException("Invalid fish diet: '" + value + "'");
    }
}
