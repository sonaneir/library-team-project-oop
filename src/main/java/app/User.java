package app;

public class User {
    private String name;
    private String login;
    private Book reservedBook;

    public User(String name, String login) {
        this.name = name;
        this.login = login;
        this.reservedBook = null;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public Book getReservedBook() {
        return reservedBook;
    }

    public void setReservedBook(Book book) {
        this.reservedBook = book;
    }

    public boolean hasReservation() {
        return reservedBook != null;
    }

    @Override
    public String toString() {
        return login + ", " + name + ", " + (reservedBook != null ? reservedBook.getId() : "none");
    }
}