package ru.yandex.practicum.services;

import ru.yandex.practicum.entities.Task;
import ru.yandex.practicum.interfaces.HistoryManager;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static class CustomLinkedList {
        Map<Integer, Node> nodeContainer = new HashMap<>();
        private Node head;
        private Node tail;

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
                tail.setNext(newNode);
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
                resultList.add(node.getTask());
                node = node.getNext();
            }

            return resultList;
        }

        /**
         * Удаляет узел в кастомном списке.
         */
        private void removeNode (Node node) {
            if (node != null) {
                Node prev = node.getPrev();
                Node next = node.getNext();

                if (prev != null) {
                    prev.setNext(next);
                }
                if (next != null) {
                    next.setPrev(prev);
                }
                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrev();
                }

                nodeContainer.remove(node.getTask().getId());
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

class Node {
    private Node next;
    private Task task;
    private Node prev;

    public Node(Node prev, Task task, Node next) {
        this.next = next;
        this.task = task;
        this.prev = prev;
    }

    public Task getTask() {
        return task;
    }

//    public void setTask(Task task) {
//        this.task = task;
//    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
