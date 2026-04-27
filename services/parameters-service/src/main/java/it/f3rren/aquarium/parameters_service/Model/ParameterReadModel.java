package it.f3rren.aquarium.parameters_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * CQRS Read Model: mantiene sempre l'ultima misurazione per acquario.
 * Aggiornato in modo asincrono dal consumer di parameter.measurements.
 * GET /latest legge da qui invece di fare MAX(measured_at) sulla tabella write.
 */
@Entity
@Table(name = "parameter_latest", schema = "parameters")
public class ParameterReadModel {

    @Id
    @Column(name = "aquarium_id")
    private Long aquariumId;

    private Double temperature;
    private Double ph;
    private Integer salinity;
    private Integer orp;
    private LocalDateTime measuredAt;
    private LocalDateTime updatedAt;

    public ParameterReadModel() {}

    public Long getAquariumId() { return aquariumId; }
    public void setAquariumId(Long aquariumId) { this.aquariumId = aquariumId; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }
    public Integer getSalinity() { return salinity; }
    public void setSalinity(Integer salinity) { this.salinity = salinity; }
    public Integer getOrp() { return orp; }
    public void setOrp(Integer orp) { this.orp = orp; }
    public LocalDateTime getMeasuredAt() { return measuredAt; }
    public void setMeasuredAt(LocalDateTime measuredAt) { this.measuredAt = measuredAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
