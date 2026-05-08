package it.f3rren.aquarium.aquariums_service.kafka.event;

public record AquariumEvent(Long aquariumId, String type) {}
