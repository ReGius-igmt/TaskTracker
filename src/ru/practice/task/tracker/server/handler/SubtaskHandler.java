package ru.practice.task.tracker.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practice.task.tracker.manager.TaskManager;
import ru.practice.task.tracker.model.Subtask;

import java.io.IOException;

public class SubtaskHandler extends BaseHandler {
    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void onGetRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            sendResponse(exchange, manager.getSubtasks());
        } else {
            try {
                int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                Subtask subtask = manager.getSubtask(id);
                if (subtask == null)
                    sendResponse(exchange, 404, "Подзадача с id=%s не найдена", id);
                else
                    sendResponse(exchange, subtask);
            } catch (StringIndexOutOfBoundsException e) {
                sendResponse(exchange, 400, "В запросе отсутствует необходимый параметр [id]");
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Неверный формат [id]");
            }
        }
    }

    @Override
    protected void onPostRequest(HttpExchange exchange) throws IOException {
        Subtask subtask = getBody(exchange, Subtask.class);
        int id = subtask.getId();
        if (manager.getSubtask(id) != null) {
            manager.updateSubtask(subtask);
            sendResponse(exchange, 200, "Подзадача с id=%s обновлена", id);
        } else {
            subtask = manager.addSubtask(subtask);
            id = subtask.getId();
            sendResponse(exchange, 201, "Добавлена подзадача с id=%s", id);
        }
    }

    @Override
    protected void onDeleteRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            manager.deleteSubtasks();
            sendResponse(exchange, "Подзадачи удалены");
        } else {
            try {
                int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                manager.deleteSubtask(id);
                sendResponse(exchange, "Подзадача %s удалена", id);
            } catch (StringIndexOutOfBoundsException e) {
                sendResponse(exchange, 400, "В запросе отсутствует необходимый параметр [id]");
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Неверный формат [id]");
            }
        }
    }
}
