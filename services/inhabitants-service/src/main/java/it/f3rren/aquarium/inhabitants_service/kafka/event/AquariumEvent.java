package it.f3rren.aquarium.inhabitants_service.kafka.event;

public record AquariumEvent(Long aquariumId, String type) {}
