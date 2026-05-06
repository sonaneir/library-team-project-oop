package app;

import java.util.LinkedList;

public class UserQueue {
    // the actual queue where users are stored
    private LinkedList<User> queue;

    public UserQueue() {
        this.queue = new LinkedList<>();
    }

    // add user to the end of the queue
    public void enqueue(User user) {
        queue.addLast(user);
    }

    // remove and return the first user (FIFO)
    public User dequeue() {
        if (isEmpty()) return null;
        return queue.removeFirst();
    }

    // look at the first user without removing
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

    // check if a user with this login is in the queue
    public boolean containsUser(String login) {
        for (User u : queue) {
            if (u.getLogin().equals(login)) return true;
        }
        return false;
    }

    // remove user by login using a lambda
    public void removeByLogin(String login) {
        queue.removeIf(u -> u.getLogin().equals(login));
    }
}