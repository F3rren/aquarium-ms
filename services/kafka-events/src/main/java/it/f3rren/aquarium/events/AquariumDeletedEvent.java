package it.f3rren.aquarium.events;

public class AquariumDeletedEvent extends BaseEvent {

    public AquariumDeletedEvent() {}

    public AquariumDeletedEvent(String aquariumId) {
        super("AquariumDeleted", aquariumId);
    }
}
