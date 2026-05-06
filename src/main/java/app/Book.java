package app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Book {
    private String title;
    private String author;
    // false by default — book is available when first created
    private boolean borrowed = false;
    private String person = "name";
    // IntegerProperty so the UI can bind to it and update automatically
    private IntegerProperty days = new SimpleIntegerProperty(0);
    // static — shared across all books, used to give each book a unique id
    private static int bookId = 1;
    private int id;
    // login of the user who reserved this book (null = not reserved)
    private String reservedBy = null;

    Book(String title, String author) {
        // assign current bookId to this book, then increment for the next one
        this.id = bookId++;
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean getBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getPerson() {
        return person;
    }

    public void setDays(int days) {
        this.days.set(days);
    }

    // returns days as a string (handy for table cells)
    public String getStringDays() {
        return String.valueOf(days.get());
    }

    public int getDays() {
        return days.get();
    }

    public int getId() {
        return id;
    }

    // used after a book is deleted to shift other ids down
    public void decreaseId() {
        this.id--;
    }

    // resets the static counter so newly added books get correct ids
    public static void resetBookId(int id) {
        bookId = id;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(String login) {
        this.reservedBy = login;
    }

    public boolean isReserved() {
        return reservedBy != null;
    }

    // string format used when saving the book to the file
    @Override
    public String toString() {
        return id + ", " + title + ", " + author + ", " + borrowed + ", " + person + ", " + days.get();
    }
}