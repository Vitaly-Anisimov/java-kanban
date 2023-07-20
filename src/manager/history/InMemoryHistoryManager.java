package manager.history;

import model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> taskHistory = new HashMap<>();
    private Node head;
    private Node tail;

    private Node linkLast(Task task) {
        final Node newTail = new Node(task, null, tail);

        if (tail == null) {
            head = newTail;
        } else {
            tail.setNext(newTail);
        }
        tail = newTail;
        return newTail;
    }

    private void removeNode(Node delNode) {
        if (delNode == null) {
            return;
        }

        final Node nextNode = delNode.getNext();
        final Node prevNode = delNode.getPrev();

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.setPrev(prevNode);
        }

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.setNext(nextNode);
        }
    }

    private List<Task> getTasks() {
        final List<Task> historyList = new ArrayList<>();
        Node iterNode = head;

        while (iterNode != null) {
            historyList.add(iterNode.getTask());
            iterNode = iterNode.getNext();
        }
        return historyList;
    }

    @Override
    public void add(Task task) {
        Node foundedNode = taskHistory.remove(task.getId());

        removeNode(foundedNode);
        taskHistory.put(task.getId(), linkLast(task));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node foundedNode = taskHistory.remove(id);

        if (foundedNode == null) {
            return;
        }

        removeNode(foundedNode);
        taskHistory.remove(id);
    }

    private static class Node {
        private final Task task;
        private Node next;
        private Node prev;

        protected Node(Task task, Node next, Node prev) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

        protected Task getTask() {
            return task;
        }

        protected Node getNext() {
            return next;
        }

        protected Node getPrev() {
            return prev;
        }

        protected void setNext(Node next) {
            this.next = next;
        }

        protected void setPrev(Node prev) {
            this.prev = prev;
        }
    }
}
