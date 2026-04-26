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
    public ObservableList<Book> books = FXCollections.observableArrayList();
    public ObservableList<Book> borrowedBooks = FXCollections.observableArrayList();
    public ObservableList<Book> availableBooks = FXCollections.observableArrayList();
    public IntegerProperty amountOfBooks = new SimpleIntegerProperty(0);
    public IntegerProperty amountOfBooksBorrowed = new SimpleIntegerProperty(0);
    public IntegerProperty amountOfBooksAvailable = new SimpleIntegerProperty(0);

    public UserQueue userQueue = new UserQueue();
    public ObservableList<User> reservationQueue = FXCollections.observableArrayList();

    public Library() {
        downloadBooks();
    }

    public void downloadBooks() {
        try {
            List<String> lines = Files.readAllLines(Path.of("src/main/resources/data/books.txt"));
            for (String line : lines) {
                String[] words = line.split(", ");

                Book book = new Book(words[1], words[2]);
                book.setBorrowed(Boolean.parseBoolean(words[3]));
                book.setPerson(words[4]);
                book.setDays(Integer.parseInt(words[5]));

                books.add(book);
                amountOfBooks.set(amountOfBooks.get() + 1);

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

    public void addBook(Book book) {
        amountOfBooks.set(amountOfBooks.get() + 1);
        amountOfBooksAvailable.set(amountOfBooksAvailable.get() + 1);
        books.add(book);
        availableBooks.add(book);
        saveBooks();
    }

    public void borrowBook(Book book, int id) {
        amountOfBooksAvailable.set(amountOfBooksAvailable.get() - 1);
        amountOfBooksBorrowed.set(amountOfBooksBorrowed.get() + 1);
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == id) {
                books.set(i, book);
                break;
            }
        }
        borrowedBooks.add(book);
        saveBooks();
    }

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
        userQueue.removeByLogin(user.getLogin());
        reservationQueue.remove(user);
        user.setReservedBook(null);
        saveBooks();
    }

    private void saveBooks() {
        List<String> lines = new ArrayList<>();
        for (Book book : books) {
            lines.add(book.toString());
        }
        try {
            Files.write(Path.of("src/main/resources/data/books.txt"), lines);
        } catch (IOException e) {}
    }

    public void deleteBook(int id) {
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

        books.removeIf(book -> book.getId() == id);
        borrowedBooks.removeIf(book -> book.getId() == id);
        availableBooks.removeIf(book -> book.getId() == id);

        books.forEach(book -> {
            if (book.getId() > id) book.decreaseId();
        });

        Book.resetBookId(books.size() + 1);

        saveBooks();
    }
}