package ru.practice.task.tracker.model;

import ru.practice.task.tracker.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Эпик - большая задача, которая состоит из подзадач.
 */
public class Epic extends Task {

    private final List<Integer> subtasksIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, int duration, LocalDateTime startTime) {
        super(name, description, TaskStatus.NEW, duration, startTime);
        this.endTime = super.getEndTime();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtask(Integer id) {
        subtasksIds.add(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksIds, epic.subtasksIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds, endTime);
    }

    @Override
    public String toString() {
        return String.format("Эпик: id = %s, name = %s, description = %s, status = %s, subtasksIds = %s",
                super.getId(), super.getName(), super.getDescription(), super.getStatus(), subtasksIds);
    }
}
