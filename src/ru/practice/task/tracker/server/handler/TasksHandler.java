package ru.practice.task.tracker.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practice.task.tracker.manager.TaskManager;

import java.io.IOException;

public class TasksHandler extends BaseHandler {
    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void onGetRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, manager.getPrioritizedTasks());
    }
}
