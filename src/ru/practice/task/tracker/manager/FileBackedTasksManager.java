package ru.practice.task.tracker.manager;

import ru.practice.task.tracker.exception.ManagerLoadException;
import ru.practice.task.tracker.exception.ManagerSaveException;
import ru.practice.task.tracker.enums.TaskStatus;
import ru.practice.task.tracker.enums.TaskType;
import ru.practice.task.tracker.model.Epic;
import ru.practice.task.tracker.model.Subtask;
import ru.practice.task.tracker.model.Task;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File saveFile;

    public FileBackedTasksManager(File saveFile) {
        this.saveFile = saveFile;
    }

    public static void main(String[] args) {
        File saveFile = new File("save.csv");
        FileBackedTasksManager manager = new FileBackedTasksManager(saveFile);

        System.out.println("TASKS:");
        Task t1 = manager.addTask(new Task("t1", "t1", TaskStatus.NEW, 0, LocalDateTime.now()));
        System.out.println(t1);
        Task t2 = manager.addTask(new Task("t2", "t2", TaskStatus.DONE, 0, LocalDateTime.now()));
        System.out.println(t2);
        Task t3 = manager.addTask(new Task("t3", "t3", TaskStatus.IN_PROGRESS, 0, LocalDateTime.now()));
        System.out.println(t3);

        Epic e1 = manager.addEpic(new Epic("e1", "e1", 0, LocalDateTime.now()));
        Epic e2 = manager.addEpic(new Epic("e2", "e2", 0, LocalDateTime.now()));

        System.out.println("SUBTASKS:");
        Subtask st1 = manager.addSubtask(new Subtask("st1", "st1", TaskStatus.NEW, 0, LocalDateTime.now(), e1.getId()));
        System.out.println(st1);
        Subtask st2 = manager.addSubtask(new Subtask("st2", "st2", TaskStatus.DONE, 0, LocalDateTime.now(), e2.getId()));
        System.out.println(st2);
        Subtask st3 = manager.addSubtask(new Subtask("st3", "st3", TaskStatus.IN_PROGRESS, 0, LocalDateTime.now(), e1.getId()));
        System.out.println(st3);

        System.out.println("EPICS:");
        System.out.println(e1);
        System.out.println(e2);

        manager.getTask(t2.getId());
        manager.getEpic(e2.getId());
        manager.getSubtask(st3.getId());

        System.out.println("HISTORY: " + manager.getHistory());
        System.out.println("LOAD MANAGER FROM FILE");

        FileBackedTasksManager loadManager = FileBackedTasksManager.loadFromFile(saveFile);
        System.out.println("FROM FILE HISTORY " + loadManager.getHistory());
        assert manager.getHistory().equals(loadManager.getHistory());
        System.out.println("FROM FILE TASK " + loadManager.getTask(t1.getId()));
        assert t1.equals(loadManager.getTask(t1.getId()));
        System.out.println("FROM FILE TASK " + loadManager.getTask(t2.getId()));
        assert t2.equals(loadManager.getTask(t2.getId()));
        System.out.println("FROM FILE TASK " + loadManager.getTask(t3.getId()));
        assert t3.equals(loadManager.getTask(t3.getId()));
        System.out.println("FROM FILE EPIC " + loadManager.getEpic(e1.getId()));
        assert e1.equals(loadManager.getEpic(e1.getId()));
        System.out.println("FROM FILE EPIC " + loadManager.getEpic(e2.getId()));
        assert e2.equals(loadManager.getEpic(e2.getId()));
        System.out.println("FROM FILE SUBTASK " + loadManager.getSubtask(st1.getId()));
        assert st1.equals(loadManager.getSubtask(st1.getId()));
        System.out.println("FROM FILE SUBTASK " + loadManager.getSubtask(st2.getId()));
        assert st2.equals(loadManager.getSubtask(st2.getId()));
        System.out.println("FROM FILE SUBTASK " + loadManager.getSubtask(st3.getId()));
        assert st3.equals(loadManager.getSubtask(st3.getId()));
    }

    public static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream().map(h -> Integer.toString(h.getId())).collect(Collectors.joining(","));
    }

    public static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            int maxId = 0;
            for(String line; (line = reader.readLine()) != null; ) {
                if(line.isEmpty()) {
                    break;
                }
                Task task = manager.fromString(line);
                if(maxId < task.getId())
                    maxId = task.getId();
                if(task instanceof Epic)
                    manager.epics.put(task.getId(), (Epic) task);
                else if(task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    Epic epic = manager.epics.get(subtask.getEpicId());
                    epic.addSubtask(task.getId());
                    manager.subtasks.put(task.getId(), subtask);
                    manager.prioritizedTasks.add(task);
                } else {
                    manager.tasks.put(task.getId(), task);
                    manager.prioritizedTasks.add(task);
                }
            }
            manager.taskId = maxId;
            String historyLine = reader.readLine();
            if(historyLine != null && !historyLine.isEmpty()) {
                List<Integer> history = historyFromString(historyLine);
                for(Integer id : history) {
                    Task task = manager.tasks.get(id);
                    if(task == null)
                        task = manager.epics.get(id);
                    if(task == null)
                        task = manager.subtasks.get(id);
                    manager.historyManager.add(task);
                }
            }
        } catch (Exception e) {
            throw new ManagerLoadException(e);
        }
        return manager;
    }

    public void save() {
        try(Writer writer = new FileWriter(saveFile)) {
            writer.write("id,type,name,status,description,epic\n");
            for(Task t : getTasks()) {
                writer.write(toString(t));
                writer.write('\n');
            }
            for(Epic e : getEpics()) {
                writer.write(toString(e));
                writer.write('\n');
            }
            for(Subtask st : getSubtasks()) {
                writer.write(toString(st));
                writer.write('\n');
            }
            writer.write('\n');
            writer.write(historyToString(historyManager));
        } catch (Exception e) {
            throw new ManagerSaveException(e);
        }
    }

    public String toString(Task task) {
        TaskType type = TaskType.TASK;
        if(task instanceof Epic)
            type = TaskType.EPIC;
        else if(task instanceof Subtask)
            type = TaskType.SUBTASK;
        return task.getId() + "," + type + "," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription() + ","
                + task.getDuration() + "," + task.getStartTime() + ","
                + task.getEndTime() + ","
                + (TaskType.SUBTASK.equals(type) ? ((Subtask) task).getEpicId() : "");
    }

    public Task fromString(String value) {
        if(value == null || value.isEmpty())
            throw new IllegalArgumentException("string value is empty or null");
        String[] d = value.split(",");
        if(d.length < 7)
            throw new IllegalArgumentException("string length < 5");
        TaskType type = TaskType.valueOf(d[1]);
        int taskId = Integer.parseInt(d[0]);
        Task task;
        int duration = Integer.parseInt(d[5]);
        LocalDateTime timeStart = LocalDateTime.parse(d[6]);
        switch (type) {
            case EPIC: {
                task = new Epic(d[2], d[4], duration, timeStart);
                task.setStatus(TaskStatus.valueOf(d[3]));
                ((Epic) task).setEndTime(LocalDateTime.parse(d[7]));
                break;
            }
            case SUBTASK: {
                task = new Subtask(d[2], d[4], TaskStatus.valueOf(d[3]), duration, timeStart, Integer.parseInt(d[8]));
                break;
            }
            default: {
                task = new Task(d[2], d[4], TaskStatus.valueOf(d[3]), duration, timeStart);
            }
        }
        task.setId(taskId);
        return task;
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task t = super.getTask(id);
        save();
        return t;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask st = super.getSubtask(id);
        save();
        return st;
    }

    @Override
    public Epic getEpic(int id) {
        Epic e = super.getEpic(id);
        save();
        return e;
    }
}
