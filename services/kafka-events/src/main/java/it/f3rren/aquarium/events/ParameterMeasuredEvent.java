package it.f3rren.aquarium.events;

import java.time.Instant;

public class ParameterMeasuredEvent extends BaseEvent {

    private Double temperature;
    private Double ph;
    private Double salinity;
    private Double orp;
    private Instant measuredAt;

    public ParameterMeasuredEvent() {}

    public ParameterMeasuredEvent(String aquariumId, Double temperature, Double ph,
                                   Double salinity, Double orp, Instant measuredAt) {
        super("ParameterMeasured", aquariumId);
        this.temperature = temperature;
        this.ph = ph;
        this.salinity = salinity;
        this.orp = orp;
        this.measuredAt = measuredAt;
    }

    public Double getTemperature() { return temperature; }
    public Double getPh() { return ph; }
    public Double getSalinity() { return salinity; }
    public Double getOrp() { return orp; }
    public Instant getMeasuredAt() { return measuredAt; }
}
