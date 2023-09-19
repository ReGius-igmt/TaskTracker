package ru.practice.task.tracker.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practice.task.tracker.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHandler {
    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void onGetRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, manager.getHistory());
    }
}
