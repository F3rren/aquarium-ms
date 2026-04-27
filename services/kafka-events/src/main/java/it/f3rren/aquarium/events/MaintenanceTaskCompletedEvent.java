package it.f3rren.aquarium.events;

import java.time.Instant;

public class MaintenanceTaskCompletedEvent extends BaseEvent {

    private String taskId;
    private String title;
    private Instant completedAt;

    public MaintenanceTaskCompletedEvent() {}

    public MaintenanceTaskCompletedEvent(String aquariumId, String taskId, String title, Instant completedAt) {
        super("MaintenanceTaskCompleted", aquariumId);
        this.taskId = taskId;
        this.title = title;
        this.completedAt = completedAt;
    }

    public String getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public Instant getCompletedAt() { return completedAt; }
}
