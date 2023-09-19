package ru.practice.task.tracker.manager;

import org.junit.jupiter.api.Test;
import ru.practice.task.tracker.enums.TaskStatus;
import ru.practice.task.tracker.model.Epic;
import ru.practice.task.tracker.model.Subtask;
import ru.practice.task.tracker.model.Task;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
public abstract class TaskManagerTest<T extends TaskManager> {
    protected final T taskManager;

    protected TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    protected Task createTask() {
        return new Task("Description", "Title", TaskStatus.NEW, 0, LocalDateTime.now());
    }
    protected Epic addEpic() {
        return new Epic("Description", "Title", 0, LocalDateTime.now());
    }
    protected Subtask addSubtask(Epic epic) {
        return new Subtask("Description", "Title", TaskStatus.NEW, 0, LocalDateTime.now(), epic.getId());
    }

    @Test
    public void createTaskTest() {
        Task task = createTask();
        taskManager.addTask(task);
        List<Task> tasks = taskManager.getTasks();
        assertNotNull(task.getStatus());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(Collections.singletonList(task), tasks);
    }

    @Test
    public void createEpicTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epic.getStatus());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubtasksIds());
        assertEquals(Collections.singletonList(epic), epics);
    }

    @Test
    public void createSubtaskTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.addSubtask(subtask);
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(Collections.singletonList(subtask), subtasks);
        assertEquals(Collections.singletonList(subtask.getId()), epic.getSubtasksIds());
    }

    @Test
    void returnNullWhenCreateTaskNullTest() {
        Task task = taskManager.addTask(null);
        assertNull(task);
    }

    @Test
    void returnNullWhenCreateEpicIsNullTest() {
        Epic epic = taskManager.addEpic(null);
        assertNull(epic);
    }

    @Test
    void returnNullWhenCreateSubtaskIsNullTest() {
        Subtask subtask = taskManager.addSubtask(null);
        assertNull(subtask);
    }

    @Test
    public void updateTaskStatusToInProgressTest() {
        Task task = createTask();
        taskManager.addTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTask(task.getId()).getStatus());
    }

    @Test
    public void updateEpicStatusToInProgressTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        epic.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateSubtaskStatusToInProgressTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.addSubtask(subtask);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubtask(subtask.getId()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateTaskStatusToInDoneTest() {
        Task task = createTask();
        taskManager.addTask(task);
        task.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task);
        assertEquals(TaskStatus.DONE, taskManager.getTask(task.getId()).getStatus());
    }

    @Test
    public void updateEpicStatusToInDoneTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        epic.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateSubtaskStatusToInDoneTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.addSubtask(subtask);
        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
        assertEquals(TaskStatus.DONE, taskManager.getSubtask(subtask.getId()).getStatus());
        assertEquals(TaskStatus.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void notUpdateTaskIfNullTest() {
        Task task = createTask();
        taskManager.addTask(task);
        taskManager.updateTask(null);
        assertEquals(task, taskManager.getTask(task.getId()));
    }

    @Test
    public void notUpdateEpicIfNullTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        taskManager.updateEpic(null);
        assertEquals(epic, taskManager.getEpic(epic.getId()));
    }

    @Test
    public void notUpdateSubtaskIfNullTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.addSubtask(subtask);
        taskManager.updateSubtask(null);
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
    public void deleteAllTasksTest() {
        Task task = createTask();
        taskManager.addTask(task);
        taskManager.deleteTasks();
        assertEquals(Collections.EMPTY_LIST, taskManager.getTasks());
    }

    @Test
    public void deleteAllEpicsTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        taskManager.deleteEpics();
        assertEquals(Collections.EMPTY_LIST, taskManager.getEpics());
    }

    @Test
    public void deleteAllSubtasksTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.addSubtask(subtask);
        taskManager.deleteSubtasks();
        assertTrue(epic.getSubtasksIds().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    public void deleteTaskByIdTest() {
        Task task = createTask();
        taskManager.addTask(task);
        taskManager.deleteTask(task.getId());
        assertEquals(Collections.EMPTY_LIST, taskManager.getTasks());
    }

    @Test
    public void deleteEpicByIdTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        taskManager.deleteEpic(epic.getId());
        assertEquals(Collections.EMPTY_LIST, taskManager.getEpics());
    }

    @Test
    public void notDeleteTaskIfBadIdTest() {
        Task task = createTask();
        taskManager.addTask(task);
        taskManager.deleteTask(999);
        assertEquals(Collections.singletonList(task), taskManager.getTasks());
    }

    @Test
    public void notDeleteEpicIfBadIdTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        taskManager.deleteEpic(999);
        assertEquals(Collections.singletonList(epic), taskManager.getEpics());
    }

    @Test
    public void notDeleteSubtaskIfBadIdTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.addSubtask(subtask);
        taskManager.deleteSubtask(999);
        assertEquals(Collections.singletonList(subtask), taskManager.getSubtasks());
        assertEquals(Collections.singletonList(subtask.getId()), taskManager.getEpic(epic.getId()).getSubtasksIds());
    }

    @Test
    public void doNothingIfTaskHashMapIsEmptyTest(){
        taskManager.deleteTasks();
        taskManager.deleteTask(100);
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void doNothingIfEpicHashMapIsEmptyTest(){
        taskManager.deleteEpics();
        taskManager.deleteEpic(100);
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    public void doNothingIfSubtaskHashMapIsEmptyTest(){
        taskManager.deleteEpics();
        taskManager.deleteSubtask(100);
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void emptyListWhenGetSubtaskByEpicIdIsEmptyTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getId());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void emptyListTasksIfNoTasksTest() {
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    public void emptyListEpicsIfNoEpicsTest() {
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    public void emptyListSubtasksIfNoSubtasksTest() {
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    public void returnNullIfTaskDoesNotExistTest() {
        assertNull(taskManager.getTask(100));
    }

    @Test
    public void returnNullIfEpicDoesNotExistTest() {
        assertNull(taskManager.getEpic(100));
    }

    @Test
    public void returnNullIfSubtaskDoesNotExistTest() {
        assertNull(taskManager.getSubtask(100));
    }

    @Test
    public void returnEmptyHistoryTest() {
        assertEquals(Collections.EMPTY_LIST, taskManager.getHistory());
    }

    @Test
    public void returnEmptyHistoryIfTasksNotExistTest() {
        taskManager.getTask(100);
        taskManager.getSubtask(100);
        taskManager.getEpic(100);
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    public void returnHistoryWithTasksTest() {
        Epic epic = addEpic();
        taskManager.addEpic(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.addSubtask(subtask);
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());
        List<Task> list = taskManager.getHistory();
        assertEquals(2, list.size());
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }
}
