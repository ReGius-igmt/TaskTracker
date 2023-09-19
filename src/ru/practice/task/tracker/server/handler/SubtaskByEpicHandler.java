package ru.practice.task.tracker.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practice.task.tracker.manager.TaskManager;

import java.io.IOException;

public class SubtaskByEpicHandler extends BaseHandler {
    public SubtaskByEpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void onGetRequest(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
            sendResponse(exchange, manager.getEpicSubtasks(id));
        } catch (StringIndexOutOfBoundsException e) {
            sendResponse(exchange, 400, "В запросе отсутствует необходимый параметр [id]");
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Неверный формат [id]");
        }
    }
}
