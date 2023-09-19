package ru.practice.task.tracker.manager;

import ru.practice.task.tracker.model.Node;
import ru.practice.task.tracker.model.Task;

import java.util.*;

/**
 * Менеджер для управления историей просмотров,
 * хранящий всю информацию в оперативной памяти.
 */
public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> first;
    private Node<Task> last;
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    // Добавление задачи в историю
    @Override
    public void add(Task task) {
        if(task == null)
            throw new RuntimeException("task cant been null");

        Node<Task> node = linkLast(task);
        node = nodeMap.put(task.getId(), node);
        if(node != null) {
            removeNode(node);
        }
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodeMap.remove(id);
        if(node != null)
            removeNode(node);
    }

    // Стандартная реализация LinkedList
    private void removeNode(Node<Task> node) {
        final Node<Task> next = node.getNext();
        final Node<Task> prev = node.getPrev();

        if (prev == null)
            first = next;
        else
            prev.setNext(next);

        if (next == null)
            last = prev;
        else
            next.setPrev(prev);
    }

    // Получение истории просмотров
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public Node<Task> linkLast(Task task) {
        final Node<Task> l = last;
        final Node<Task> newNode = new Node<>(l, task, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.setNext(newNode);
        return newNode;
    }

    private List<Task> getTasks() {
        if(nodeMap.isEmpty())
            return Collections.emptyList();
        List<Task> tasks = new ArrayList<>(nodeMap.size());
        Node<Task> current = first;
        while (current != null) {
            tasks.add(current.getItem());
            current = current.getNext();
        }
        return tasks;
    }
}
