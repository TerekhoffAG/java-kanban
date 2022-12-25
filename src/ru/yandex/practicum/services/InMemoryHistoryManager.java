package ru.yandex.practicum.services;

import ru.yandex.practicum.entities.Task;
import ru.yandex.practicum.interfaces.HistoryManager;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static class CustomLinkedList {
        private final Map<Integer, Node> nodeContainer = new HashMap<>();
        private Node head;
        private Node tail;

        private static class Node {
            Node next;
            Task task;
            Node prev;

            Node(Node prev, Task task, Node next) {
                this.next = next;
                this.task = task;
                this.prev = prev;
            }
        }

        /**
         * Добавляет задачу в конец списка.
         */
        private void linkLast (Task task) {
            int id = task.getId();

            if (nodeContainer.containsKey(id)) {
                removeNode(nodeContainer.get(id));
            }

            Node newNode = new Node(tail, task, null);
            nodeContainer.put(id, newNode);

            if (tail != null) {
                tail.next = newNode;
            } else {
                head = newNode;
            }

            tail = newNode;
        }

        /**
         * Получает список всех задачи.
         */
        private List<Task> getTasks () {
            List<Task> resultList = new ArrayList<>();
            Node node = head;

            while (node != null) {
                resultList.add(node.task);
                node = node.next;
            }

            return resultList;
        }

        /**
         * Удаляет узел в кастомном списке.
         */
        private void removeNode (Node node) {
            if (node != null) {
                Node prev = node.prev;
                Node next = node.next;

                if (prev != null) {
                    prev.next = next;
                }
                if (next != null) {
                    next.prev = prev;
                }
                if (head == node) {
                    head = node.next;
                }
                if (tail == node) {
                    tail = node.prev;
                }

                nodeContainer.remove(node.task.getId());
            }
        }

        private Node getNodeById (int nodeId) {
            return nodeContainer.get(nodeId);
        }
    }

    CustomLinkedList linkedList = new CustomLinkedList();

    /**
     * Добавляет задачу в историю просмотров.
     */
    @Override
    public void add(Task task) {
        if (task != null) {
            linkedList.linkLast(task);
        }
    }

    /**
     * Удаляет задачу из истории просмотров.
     */
    @Override
    public void remove(int id) {
        linkedList.removeNode(linkedList.getNodeById(id));
    }

    /**
     * Получает историю просмотров задач.
     */
    @Override
    public List<Task> getHistory() {
        return linkedList.getTasks();
    }
}

