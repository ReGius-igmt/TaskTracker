package ru.practice.task.tracker.manager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    protected InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }
}
