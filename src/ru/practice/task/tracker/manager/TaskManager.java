package ru.practice.task.tracker.manager;

import ru.practice.task.tracker.model.Epic;
import ru.practice.task.tracker.model.Subtask;
import ru.practice.task.tracker.model.Task;

import java.util.List;

/**
 * Менеджер для управления всеми задачами.
 */
public interface TaskManager {

    Task addTask(Task task);

    Subtask addSubtask(Subtask subtask);

    Epic addEpic(Epic epic);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    List<Subtask> getEpicSubtasks(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
