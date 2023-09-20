package ru.practice.task.tracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.practice.task.tracker.enums.TaskStatus;
import ru.practice.task.tracker.manager.Managers;
import ru.practice.task.tracker.manager.TaskManager;
import ru.practice.task.tracker.model.Epic;
import ru.practice.task.tracker.model.Subtask;
import ru.practice.task.tracker.model.Task;
import ru.practice.task.tracker.server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Главный класс программы.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        new KVServer().start();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).create();

        TaskManager httpTaskManager = Managers.getDefault();

        Task task1 = new Task(
                "Разработать лифт до луны", "Космолифт",
                TaskStatus.NEW,
                1, LocalDateTime.now()
        );
        httpTaskManager.addTask(task1);

        Epic epic1 = new Epic(
                "Посадить дерево",
                "Дерево",
                2, LocalDateTime.now()
        );
        httpTaskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask(
                "Купить семена",
                "Семена",
                TaskStatus.NEW,
                3, LocalDateTime.now(),
                epic1.getId()
        );
        httpTaskManager.addSubtask(subtask1);


        httpTaskManager.getTask(task1.getId());
        httpTaskManager.getEpic(epic1.getId());
        httpTaskManager.getSubtask(subtask1.getId());

        System.out.println("Печать всех задач");
        System.out.println(gson.toJson(httpTaskManager.getTasks()));
        System.out.println("Печать всех эпиков");
        System.out.println(gson.toJson(httpTaskManager.getEpics()));
        System.out.println("Печать всех подзадач");
        System.out.println(gson.toJson(httpTaskManager.getSubtasks()));
        System.out.println("Загруженный менеджер");
        System.out.println(httpTaskManager);
    }
}
