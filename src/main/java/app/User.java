package app;

public class User {
    private String name;
    private String login;
    // the book this user has reserved (null if none)
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

    // true if the user currently has a reserved book
    public boolean hasReservation() {
        return reservedBook != null;
    }

    // custom string representation (used when printing the user)
    @Override
    public String toString() {
        return login + ", " + name + ", " + (reservedBook != null ? reservedBook.getId() : "none");
    }
}