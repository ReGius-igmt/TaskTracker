package ru.practice.task.tracker.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practice.task.tracker.manager.TaskManager;
import ru.practice.task.tracker.model.Task;

import java.io.IOException;

public class TaskHandler extends BaseHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void onGetRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            sendResponse(exchange, manager.getTasks());
        } else {
            try {
                int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                Task task = manager.getTask(id);
                if (task == null)
                    sendResponse(exchange, 404, "Задача с id=%s не найдена", id);
                else
                    sendResponse(exchange, task);
            } catch (StringIndexOutOfBoundsException e) {
                sendResponse(exchange, 400, "В запросе отсутствует необходимый параметр [id]");
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Неверный формат [id]");
            }
        }
    }

    @Override
    protected void onPostRequest(HttpExchange exchange) throws IOException {
        Task task = getBody(exchange, Task.class);
        int id = task.getId();
        if (manager.getTask(id) != null) {
            manager.updateTask(task);
            sendResponse(exchange, 200, "Задача с id=%s обновлена", id);
        } else {
            task = manager.addTask(task);
            id = task.getId();
            sendResponse(exchange, 201, "Добавлена задача с id=%s", id);
        }
    }

    @Override
    protected void onDeleteRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            manager.deleteTasks();
            sendResponse(exchange, "Задачи удалены");
        } else {
            try {
                int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                manager.deleteTask(id);
                sendResponse(exchange, "Задача %s удалена", id);
            } catch (StringIndexOutOfBoundsException e) {
                sendResponse(exchange, 400, "В запросе отсутствует необходимый параметр [id]");
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Неверный формат [id]");
            }
        }
    }
}
