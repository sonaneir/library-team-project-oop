package app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Book {
    private String title;
    private String author;
    private boolean borrowed = false;
    private String person = "name";
    private IntegerProperty days = new SimpleIntegerProperty(0);
    private static int bookId = 1;
    private int id;
    private String reservedBy = null;

    Book(String title, String author) {
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

    public String getStringDays() {
        return String.valueOf(days.get());
    }

    public int getDays() {
        return days.get();
    }

    public int getId() {
        return id;
    }

    public void decreaseId() {
        this.id--;
    }

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

    @Override
    public String toString() {
        return id + ", " + title + ", " + author + ", " + borrowed + ", " + person + ", " + days.get();
    }
}