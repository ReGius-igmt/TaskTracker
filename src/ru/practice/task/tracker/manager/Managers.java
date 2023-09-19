package ru.practice.task.tracker.manager;

/**
 * Класс, ответственный за создание менеджеров.
 */
public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
