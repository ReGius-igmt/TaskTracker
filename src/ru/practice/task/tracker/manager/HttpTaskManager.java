package ru.practice.task.tracker.manager;

import com.google.gson.*;
import ru.practice.task.tracker.LocalDateTimeDeserializer;
import ru.practice.task.tracker.exception.ManagerInitException;
import ru.practice.task.tracker.model.Epic;
import ru.practice.task.tracker.model.Subtask;
import ru.practice.task.tracker.model.Task;
import ru.practice.task.tracker.server.KVTaskClient;

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

    public HttpTaskManager(String url, boolean doLoad) {
        super(null);
        try {
            client = new KVTaskClient(url);
        } catch (Exception e) {
            throw new ManagerInitException(e);
        }

        if (doLoad) {
            loadData();
        }
    }

    private void loadData() {
        int maxId = 0;
        JsonElement jsonTasks = JsonParser.parseString(client.load(KEY_TASKS));
        if (!jsonTasks.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                if(maxId < task.getId())
                    maxId = task.getId();
                this.tasks.put(task.getId(), task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(KEY_EPICS));
        if (!jsonEpics.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic task = gson.fromJson(jsonEpic, Epic.class);
                if(maxId < task.getId())
                    maxId = task.getId();
                this.epics.put(task.getId(), task);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(KEY_SUBTASKS));
        if (!jsonSubtasks.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask task = gson.fromJson(jsonSubtask, Subtask.class);
                if(maxId < task.getId())
                    maxId = task.getId();
                Epic epic = this.epics.get(task.getEpicId());
                epic.addSubtask(task.getId());
                this.subtasks.put(task.getId(), task);
                this.prioritizedTasks.add(task);
                this.subtasks.put(task.getId(), task);
            }
        }

        this.taskId = maxId;
        JsonElement jsonHistoryList = JsonParser.parseString(client.load(KEY_HISTORY));
        if (!jsonHistoryList.isJsonNull() && jsonTasks.isJsonArray()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int taskId = jsonTaskId.getAsInt();
                Task task = this.tasks.get(taskId);
                if(task == null)
                    task = this.epics.get(taskId);
                if(task == null)
                    task = this.subtasks.get(taskId);
                this.historyManager.add(task);
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
