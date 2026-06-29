package it.f3rren.aquarium.maintenance_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskFrequency {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    CUSTOM("custom");

    private final String value;

    TaskFrequency(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TaskFrequency fromValue(String value) {
        for (TaskFrequency f : values()) {
            if (f.value.equalsIgnoreCase(value)) {
                return f;
            }
        }
        throw new IllegalArgumentException("Invalid task frequency: '" + value + "'. Must be one of: daily, weekly, monthly, custom");
    }
}
