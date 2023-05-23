package managers;

import tasks.Task;
public class Node {
    private Task task;
    private Node next;
    private Node prev;

    public Node(Task task, Node next, Node prev) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

    public Task getTask() {
        return task;
    }

    public Node getNext() {
        return next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
