package ru.practice.task.tracker.model;

import ru.practice.task.tracker.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Подзадача.
 */
public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int duration, LocalDateTime startTime, int epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return String.format("Подзадача: id = %s, name = %s, description = %s, status = %s, epicId = %s, duration = %s, startTime = %s",
                super.getId(), super.getName(), super.getDescription(), super.getStatus(), epicId, getDuration(), getStartTime());
    }
}
