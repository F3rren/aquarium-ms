package it.f3rren.aquarium.maintenance_service.kafka.event;

public record AquariumEvent(Long aquariumId, String type) {}
