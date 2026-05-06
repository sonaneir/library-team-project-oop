package app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Library {
    // ObservableList — a list that notifies the UI automatically when it changes
    public ObservableList<Book> books = FXCollections.observableArrayList();
    public ObservableList<Book> borrowedBooks = FXCollections.observableArrayList();
    public ObservableList<Book> availableBooks = FXCollections.observableArrayList();
    // counters bound to dashboard stat cards
    public IntegerProperty amountOfBooks = new SimpleIntegerProperty(0);
    public IntegerProperty amountOfBooksBorrowed = new SimpleIntegerProperty(0);
    public IntegerProperty amountOfBooksAvailable = new SimpleIntegerProperty(0);

    // queue of users waiting for their reserved books
    public UserQueue userQueue = new UserQueue();
    public ObservableList<User> reservationQueue = FXCollections.observableArrayList();

    public Library() {
        // load saved books from file when the library is created
        downloadBooks();
    }

    // reads books from the text file and fills the lists
    public void downloadBooks() {
        try {
            List<String> lines = Files.readAllLines(Path.of("src/main/resources/data/books.txt"));
            for (String line : lines) {
                // each line is split by ", " — fields: id, title, author, borrowed, person, days
                String[] words = line.split(", ");

                Book book = new Book(words[1], words[2]);
                book.setBorrowed(Boolean.parseBoolean(words[3]));
                book.setPerson(words[4]);
                book.setDays(Integer.parseInt(words[5]));

                books.add(book);
                amountOfBooks.set(amountOfBooks.get() + 1);

                // put the book in the right list based on its status
                if (book.getBorrowed()) {
                    borrowedBooks.add(book);
                    amountOfBooksBorrowed.set(amountOfBooksBorrowed.get() + 1);
                } else {
                    availableBooks.add(book);
                    amountOfBooksAvailable.set(amountOfBooksAvailable.get() + 1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // adds a new book — it starts as available
    public void addBook(Book book) {
        amountOfBooks.set(amountOfBooks.get() + 1);
        amountOfBooksAvailable.set(amountOfBooksAvailable.get() + 1);
        books.add(book);
        availableBooks.add(book);
        saveBooks();
    }

    // moves a book from available to borrowed
    public void borrowBook(Book book, int id) {
        amountOfBooksAvailable.set(amountOfBooksAvailable.get() - 1);
        amountOfBooksBorrowed.set(amountOfBooksBorrowed.get() + 1);
        // find the book in the main list and replace it with the updated version
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == id) {
                books.set(i, book);
                break;
            }
        }
        borrowedBooks.add(book);
        saveBooks();
    }

    // user reserves a book — book leaves available list and user joins the queue
    public void reserveBook(User user, Book book) {
        book.setReservedBy(user.getLogin());
        user.setReservedBook(book);
        availableBooks.remove(book);
        userQueue.enqueue(user);
        reservationQueue.add(user);
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == book.getId()) {
                books.set(i, book);
                break;
            }
        }
        saveBooks();
    }

    // admin issues a reserved book to the user — finalizes the reservation
    public void issueReservation(User user) {
        Book book = user.getReservedBook();
        if (book == null) return;

        book.setBorrowed(true);
        book.setPerson(user.getName());
        book.setReservedBy(null);

        amountOfBooksBorrowed.set(amountOfBooksBorrowed.get() + 1);

        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == book.getId()) {
                books.set(i, book);
                break;
            }
        }

        borrowedBooks.add(book);
        // remove user from both queues and clear their reservation
        userQueue.removeByLogin(user.getLogin());
        reservationQueue.remove(user);
        user.setReservedBook(null);
        saveBooks();
    }

    // saves all books back to the text file
    private void saveBooks() {
        List<String> lines = new ArrayList<>();
        for (Book book : books) {
            lines.add(book.toString());
        }
        try {
            Files.write(Path.of("src/main/resources/data/books.txt"), lines);
        } catch (IOException e) {}
    }

    // deletes a book by id and updates all counters
    public void deleteBook(int id) {
        // update the right counter depending on the book's status
        books.forEach(book -> {
            if (book.getId() == id) {
                if (book.getBorrowed()) {
                    amountOfBooks.set(amountOfBooks.get() - 1);
                    amountOfBooksBorrowed.set(amountOfBooksBorrowed.get() - 1);
                } else {
                    amountOfBooks.set(amountOfBooks.get() - 1);
                    amountOfBooksAvailable.set(amountOfBooksAvailable.get() - 1);
                }
            }
        });

        // remove the book from all three lists
        books.removeIf(book -> book.getId() == id);
        borrowedBooks.removeIf(book -> book.getId() == id);
        availableBooks.removeIf(book -> book.getId() == id);

        // shift down ids of all books that came after the deleted one
        books.forEach(book -> {
            if (book.getId() > id) book.decreaseId();
        });

        // reset the static id counter so the next added book gets the right id
        Book.resetBookId(books.size() + 1);

        saveBooks();
    }
}