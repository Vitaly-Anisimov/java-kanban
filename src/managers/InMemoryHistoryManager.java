package managers;

import tasks.Task;
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
        if (task != null) {
            if (taskHistory.containsKey(task.getId())) {
                removeNode(taskHistory.get(task.getId()));
                taskHistory.remove(task.getId());
            }
            taskHistory.put(task.getId(), linkLast(task));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (taskHistory.containsKey(id)) {
            removeNode(taskHistory.get(id));
            taskHistory.remove(id);
        }
    }

}
