package ru.practice.task.tracker.manager;

import ru.practice.task.tracker.model.Task;

import java.util.List;

/**
 * Менеджер для управления историей просмотров.
 */
public interface HistoryManager {

    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
}
