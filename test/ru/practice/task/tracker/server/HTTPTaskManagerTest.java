package ru.practice.task.tracker.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practice.task.tracker.enums.TaskStatus;
import ru.practice.task.tracker.manager.HttpTaskManager;
import ru.practice.task.tracker.manager.Managers;
import ru.practice.task.tracker.manager.TaskManagerTest;
import ru.practice.task.tracker.model.Epic;
import ru.practice.task.tracker.model.Subtask;
import ru.practice.task.tracker.model.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private KVServer server;

    protected HTTPTaskManagerTest() {
        super(null);
    }

    @BeforeEach
    public void createManager() {
        try {
            server = new KVServer();
            server.start();
            taskManager = (HttpTaskManager) Managers.getDefault();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }


    @Test
    public void shouldLoadTasks() {
        Task task1 = new Task("description1", "name1", TaskStatus.NEW, 1, LocalDateTime.now());
        Task task2 = new Task("description2", "name2", TaskStatus.NEW, 2, LocalDateTime.now().plus(2, ChronoUnit.MINUTES));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        List<Task> list = taskManager.getHistory();
        assertEquals(taskManager.getTasks(), list);
    }

    @Test
    public void shouldLoadEpics() {
        Epic epic1 = new Epic("description1", "name1", 3, LocalDateTime.now());
        Epic epic2 = new Epic("description2", "name2", 4, LocalDateTime.now());
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        List<Task> list = taskManager.getHistory();
        assertEquals(taskManager.getEpics(), list);
    }

    @Test
    public void shouldLoadSubtasks() {
        Epic epic1 = new Epic("description1", "name1", 5, LocalDateTime.now());
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("description1", "name1", TaskStatus.NEW, 6, LocalDateTime.now(), epic1.getId());
        Subtask subtask2 = new Subtask("description2", "name2", TaskStatus.NEW, 7, LocalDateTime.now().plus(10, ChronoUnit.MINUTES), epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        List<Task> list = taskManager.getHistory();
        assertEquals(taskManager.getSubtasks(), list);
    }

}
