package ru.practice.task.tracker;

import ru.practice.task.tracker.enums.TaskStatus;
import ru.practice.task.tracker.manager.Managers;
import ru.practice.task.tracker.manager.TaskManager;
import ru.practice.task.tracker.model.Epic;
import ru.practice.task.tracker.model.Subtask;
import ru.practice.task.tracker.model.Task;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Главный класс программы.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("Тестирование создания, обновления и удаления задач");

        TaskManager manager = Managers.getDefault();

        System.out.println("Создание задач");

        Task task1 = manager.addTask(new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, 0, LocalDateTime.now()));
        Task task2 = manager.addTask(new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW, 0, LocalDateTime.now()));

        Epic epic1 = manager.addEpic(new Epic("Эпик 1", "Описание эпика 1", 0, LocalDateTime.now()));
        int epic1Id = epic1.getId();

        Epic epic2 = manager.addEpic(new Epic("Эпик 2", "Описание эпика 2", 0, LocalDateTime.now()));
        int epic2Id = epic2.getId();

        Subtask subtask1 = manager.addSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, 0, LocalDateTime.now(), epic1Id));
        Subtask subtask2 = manager.addSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, 0, LocalDateTime.now(), epic1Id));
        Subtask subtask3 = manager.addSubtask(new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, 0, LocalDateTime.now(), epic2Id));

        System.out.println("Список задач, эпиков и подзадач после создания:");
        printList(manager.getTasks());
        printList(manager.getEpics());
        printList(manager.getSubtasks());

        System.out.println("Обновление задач");

        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);

        task2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task2);

        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask2);

        subtask3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);

        System.out.println("Список задач, эпиков и подзадач после обновления:");
        printList(manager.getTasks());
        printList(manager.getEpics());
        printList(manager.getSubtasks());

        System.out.println("Удаление задач");

        manager.deleteTask(task2.getId());
        manager.deleteSubtask(subtask2.getId());
        manager.deleteEpic(epic2.getId());

        System.out.println("Список задач, эпиков и подзадач после удаления:");
        printList(manager.getTasks());
        printList(manager.getEpics());
        printList(manager.getSubtasks());

        System.out.println("Тестирование истории просмотров задач");

        // Просмотр существующих задач
        task1 = manager.getTask(task1.getId());
        subtask1 = manager.getSubtask(subtask1.getId());
        epic1 = manager.getEpic(epic1.getId());
        task1 = manager.getTask(task1.getId());

        List<Task> viewedTasks = manager.getHistory();

        System.out.println("История просмотров:");
        printList(viewedTasks);

        // Добавление и просмотр новых задач

        Task task = manager.addTask(new Task("Новая задача", "Описание новой задачи", TaskStatus.NEW, 0, LocalDateTime.now()));
        task = manager.getTask(task.getId());

        Epic epic = manager.addEpic(new Epic("Новый эпик", "Описание нового эпика", 0, LocalDateTime.now()));
        epic = manager.getEpic(epic.getId());

        Subtask subtask = manager.addSubtask(new Subtask("Новая подзадача", "Описание новой подзадачи", TaskStatus.NEW, 0, LocalDateTime.now(), epic.getId()));
        subtask = manager.getSubtask(subtask.getId());

        manager.deleteEpic(epic1.getId());

        viewedTasks = manager.getHistory();
        System.out.println("История просмотров:");
        printList(viewedTasks);
    }

    private static void printList(List list) {
        for (Object object : list) {
            System.out.println(object);
        }
    }
}
