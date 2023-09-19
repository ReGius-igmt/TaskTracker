package ru.practice.task.tracker.manager;

import ru.practice.task.tracker.enums.TaskStatus;
import ru.practice.task.tracker.exception.ManagerValidateException;
import ru.practice.task.tracker.model.Epic;
import ru.practice.task.tracker.model.Subtask;
import ru.practice.task.tracker.model.Task;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Менеджер для управления всеми задачами,
 * хранящий всю информацию в оперативной памяти.
 */
public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final Comparator<Task> taskComparator = (o1, o2) -> {
        if (Objects.isNull(o1.getStartTime()) || Objects.isNull(o2.getStartTime()))
            return Integer.compare(o1.getId(), o2.getId());
        return o1.getStartTime().compareTo(o2.getStartTime());
    };
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int taskId = 0;

    // Генерирует ID задачи
    private int generateId() {
        return ++taskId;
    }

    // Методы для каждого из типа задач (задача, подзадача, эпик)

    // Создание задачи

    @Override
    public Task addTask(Task task) {
        if (task == null)
            return null;
        int id = generateId();
        task.setId(id);
        updateTask(task);
        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (subtask == null)
            return null;
        int id = generateId();
        subtask.setId(id);
        updateSubtask(subtask);
        return subtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null)
            return null;
        int id = generateId();
        epic.setId(id);
        updateEpicStatus(epic);
        epics.put(id, epic);
        return epic;
    }

    // Получение задачи по идентификатору

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if(task == null)
            return null;
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if(subtask == null)
            return null;
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if(epic == null)
            return null;
        historyManager.add(epic);
        return epic;
    }

    // Получение списка всех задач

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    // Обновление задачи

    @Override
    public void updateTask(Task task) {
        if (task == null)
            return;
        addNewPrioritizedTask(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null)
            return;
        addNewPrioritizedTask(subtask);
        subtasks.put(subtask.getId(), subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic);
        updateTimeEpic(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null)
            return;
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        updateTimeEpic(epic);
    }

    // Удаление задачи по идентификатору

    @Override
    public void deleteTask(int id) {
        if(tasks.remove(id) != null)
            prioritizedTasks.removeIf(task -> task.getId() == id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null)
            return;
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        List<Integer> epicSubtasksIds = epic.getSubtasksIds();
        epicSubtasksIds.remove(Integer.valueOf(id));
        updateEpicStatus(epic);
        updateTimeEpic(epic);
        prioritizedTasks.remove(subtask);
        subtasks.remove(id);
        historyManager.remove(id);
    }

    private void addNewPrioritizedTask(Task task) {
        validateTaskPriority(task);
        prioritizedTasks.add(task);
    }

    private void validateTaskPriority(Task checkTask) {
        for (Task t : getPrioritizedTasks()) {
            if (t.getId() == checkTask.getId())
                continue;
            if (checkTime(t, checkTask)) {
                throw new ManagerValidateException("Задачи #" + t.getId() + " и #" + checkTask.getId() + " пересекаются");
            }
        }
    }

    public boolean checkTime(Task t1, Task t2) {
        if (Objects.nonNull(t1.getStartTime()) && Objects.nonNull(t2.getEndTime())) {
            return (
                    (
                            (t2.getStartTime().isBefore(t1.getStartTime()) || t2.getStartTime().equals(t1.getStartTime()))
                            && t1.getStartTime().isBefore(t2.getEndTime()) || t1.getStartTime().equals(t2.getEndTime())
                    )
                    ||
                    (
                            (t2.getStartTime().isBefore(t1.getEndTime()) || t2.getStartTime().equals(t1.getEndTime()))
                            && (t1.getEndTime().isBefore(t2.getEndTime()) || t1.getEndTime().equals(t2.getEndTime()))
                    )
            );
        }
        return false;
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if(epic == null)
            return;
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), subtaskId));
        }
        historyManager.remove(id);
    }

    public void updateTimeEpic(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());
        if (subtasks.isEmpty())
            return;
        LocalDateTime startTime = subtasks.get(0).getStartTime();
        LocalDateTime endTime = subtasks.get(0).getEndTime();

        int duration = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime))
                startTime = subtask.getStartTime();
            if (subtask.getEndTime().isAfter(endTime))
                endTime = subtask.getEndTime();
            duration += subtask.getDuration();
        }

        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    // Удаление всех задач

    @Override
    public void deleteTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            deleteSubtask(entry.getKey());
        }
    }

    @Override
    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }

    // Получение списка всех подзадач эпика
    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        Epic epic = epics.get(id);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        List<Subtask> subtasksList = new ArrayList<>();
        for (Integer subtaskId : subtasksIds) {
            Subtask subtask = subtasks.get(subtaskId);
            subtasksList.add(subtask);
        }
        return subtasksList;
    }

    // Установка статуса эпика в зависимости от статусов подзадач
    private void updateEpicStatus(Epic epic) {
        int newTasksCount = 0;
        int doneTasksCount = 0;
        int allTasksCount = epic.getSubtasksIds().size();
        for (int id : epic.getSubtasksIds()) {
            Subtask subtask = subtasks.get(id);
            TaskStatus status = subtask.getStatus();
            if (status == TaskStatus.NEW) {
                newTasksCount++;
            } else if (status == TaskStatus.DONE) {
                doneTasksCount++;
            }
        }
        if (newTasksCount == allTasksCount) {
            epic.setStatus(TaskStatus.NEW);
        } else if (doneTasksCount == allTasksCount) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        epics.put(epic.getId(), epic);
    }

    // Получение истории просмотров
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}
