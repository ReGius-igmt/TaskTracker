package ru.practice.task.tracker.manager;

import ru.practice.task.tracker.server.KVServer;

import java.io.File;
import java.io.IOException;

/**
 * Класс, ответственный за создание менеджеров.
 */
public class Managers {

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:" + KVServer.PORT);
    }

    public static TaskManager getFileBackend() {
        return new FileBackedTasksManager(new File("save.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
