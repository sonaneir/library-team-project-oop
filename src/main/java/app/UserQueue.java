package app;

import java.util.LinkedList;

public class UserQueue {
    private LinkedList<User> queue;

    public UserQueue() {
        this.queue = new LinkedList<>();
    }

    public void enqueue(User user) {
        queue.addLast(user);
    }

    public User dequeue() {
        if (isEmpty()) return null;
        return queue.removeFirst();
    }

    public User peek() {
        if (isEmpty()) return null;
        return queue.getFirst();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public LinkedList<User> getAll() {
        return queue;
    }

    public boolean containsUser(String login) {
        for (User u : queue) {
            if (u.getLogin().equals(login)) return true;
        }
        return false;
    }

    public void removeByLogin(String login) {
        queue.removeIf(u -> u.getLogin().equals(login));
    }
}