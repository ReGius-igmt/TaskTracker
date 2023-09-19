package ru.practice.task.tracker.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practice.task.tracker.manager.TaskManager;
import ru.practice.task.tracker.model.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHandler {
    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void onGetRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            sendResponse(exchange, manager.getEpics());
        } else {
            try {
                int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                Epic epic = manager.getEpic(id);
                if (epic == null)
                    sendResponse(exchange, 404, "Эпик с id=%s не найдена", id);
                else
                    sendResponse(exchange, epic);
            } catch (StringIndexOutOfBoundsException e) {
                sendResponse(exchange, 400, "В запросе отсутствует необходимый параметр [id]");
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Неверный формат [id]");
            }
        }
    }

    @Override
    protected void onPostRequest(HttpExchange exchange) throws IOException {
        Epic epic = getBody(exchange, Epic.class);
        int id = epic.getId();
        if (manager.getEpic(id) != null) {
            manager.updateEpic(epic);
            sendResponse(exchange, 200, "Эпик с id=%s обновлена", id);
        } else {
            epic = manager.addEpic(epic);
            id = epic.getId();
            sendResponse(exchange, 201, "Добавлен эпик с id=%s", id);
        }
    }

    @Override
    protected void onDeleteRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            manager.deleteEpics();
            sendResponse(exchange, "Эпики удалены");
        } else {
            try {
                int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                manager.deleteEpic(id);
                sendResponse(exchange, "Эпик %s удалена", id);
            } catch (StringIndexOutOfBoundsException e) {
                sendResponse(exchange, 400, "В запросе отсутствует необходимый параметр [id]");
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Неверный формат [id]");
            }
        }
    }
}
