package ru.practice.task.tracker.manager;

import com.google.gson.*;
import ru.practice.task.tracker.LocalDateTimeDeserializer;
import ru.practice.task.tracker.model.Epic;
import ru.practice.task.tracker.model.Subtask;
import ru.practice.task.tracker.model.Task;
import ru.practice.task.tracker.server.KVTaskClient;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private static final Gson gson =
            new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).create();
    private final static String KEY_TASKS = "tasks";
    private final static String KEY_SUBTASKS = "subtasks";
    private final static String KEY_EPICS = "epics";
    private final static String KEY_HISTORY = "history";
    private final KVTaskClient client;

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        super(null);
        client = new KVTaskClient(url);
        JsonElement jsonTasks = JsonParser.parseString(client.load(KEY_TASKS));
        if (!jsonTasks.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                this.addTask(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(KEY_EPICS));
        if (!jsonEpics.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic task = gson.fromJson(jsonEpic, Epic.class);
                this.addEpic(task);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(KEY_SUBTASKS));
        if (!jsonSubtasks.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask task = gson.fromJson(jsonSubtask, Subtask.class);
                this.addSubtask(task);
            }
        }

        JsonElement jsonHistoryList = JsonParser.parseString(client.load(KEY_HISTORY));
        if (!jsonHistoryList.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int taskId = jsonTaskId.getAsInt();
                if (this.subtasks.containsKey(taskId)) {
                    this.getSubtask(taskId);
                } else if (this.epics.containsKey(taskId)) {
                    this.getEpic(taskId);
                } else if (this.tasks.containsKey(taskId)) {
                    this.getTask(taskId);
                }
            }
        }
    }

    @Override
    public void save() {
        client.put(KEY_TASKS, gson.toJson(tasks.values()));
        client.put(KEY_SUBTASKS, gson.toJson(subtasks.values()));
        client.put(KEY_EPICS, gson.toJson(epics.values()));
        client.put(KEY_HISTORY, gson.toJson(this.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }
}
