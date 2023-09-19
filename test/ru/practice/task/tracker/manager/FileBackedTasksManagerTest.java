package ru.practice.task.tracker.manager;

import org.junit.jupiter.api.Test;
import ru.practice.task.tracker.enums.TaskStatus;
import ru.practice.task.tracker.model.Epic;
import ru.practice.task.tracker.model.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private static final File saveFile = new File("save.csv");
    protected FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager(saveFile));
    }

    @Test
    public void correctlySaveAndLoadTest() {
        Task task = new Task("Description", "Title", TaskStatus.NEW, 0, LocalDateTime.now());
        taskManager.addTask(task);
        Epic epic = new Epic("Description", "Title", 0, LocalDateTime.now());
        taskManager.addEpic(epic);
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(saveFile);
        assertEquals(Collections.singletonList(task), fileManager.getTasks());
        assertEquals(Collections.singletonList(epic), fileManager.getEpics());
    }

    @Test
    public void saveAndLoadEmptyTasksEpicsSubtasksTest() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(saveFile);
        fileManager.save();
        fileManager = FileBackedTasksManager.loadFromFile(saveFile);
        assertEquals(Collections.EMPTY_LIST, fileManager.getTasks());
        assertEquals(Collections.EMPTY_LIST, fileManager.getEpics());
        assertEquals(Collections.EMPTY_LIST, fileManager.getSubtasks());
    }

    @Test
    public void saveAndLoadEmptyHistoryTest() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(saveFile);
        fileManager.save();
        fileManager = FileBackedTasksManager.loadFromFile(saveFile);
        assertEquals(Collections.EMPTY_LIST, fileManager.getHistory());
    }
}
