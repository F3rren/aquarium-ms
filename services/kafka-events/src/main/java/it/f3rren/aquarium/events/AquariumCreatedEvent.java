package it.f3rren.aquarium.events;

public class AquariumCreatedEvent extends BaseEvent {

    private String name;
    private Integer volume;
    private String type;

    public AquariumCreatedEvent() {}

    public AquariumCreatedEvent(String aquariumId, String name, Integer volume, String type) {
        super("AquariumCreated", aquariumId);
        this.name = name;
        this.volume = volume;
        this.type = type;
    }

    public String getName() { return name; }
    public Integer getVolume() { return volume; }
    public String getType() { return type; }
}
