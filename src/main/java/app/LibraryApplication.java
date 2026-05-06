package app;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// main JavaFX application class — extends Application to use the GUI framework
public class LibraryApplication extends Application {
    // central area where different pages are swapped in and out
    private StackPane content = new StackPane();
    private Library library = new Library();
    // currently logged-in user (null when admin or no one is logged in)
    private User currentUser = null;

    // entry point of the JavaFX app — first screen is role selection
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(createRoleSelection(stage), 1100, 650);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setTitle("Library Manager");
        stage.setScene(scene);
        stage.show();
    }

    // first screen — choose between Admin and User
    private Pane createRoleSelection(Stage stage) {
        VBox root = new VBox(32);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("role-selection-root");

        Label title = new Label("LIBRARY");
        title.getStyleClass().add("role-logo");

        Label subtitle = new Label("Select your role to continue");
        subtitle.getStyleClass().add("role-subtitle");

        HBox cards = new HBox(28);
        cards.setAlignment(Pos.CENTER);

        // admin card — clicking it opens the admin panel
        VBox adminCard = new VBox(16);
        adminCard.getStyleClass().add("role-card");
        adminCard.setAlignment(Pos.CENTER);
        Label adminIcon = new Label("🔑");
        adminIcon.getStyleClass().add("role-icon");
        Label adminLabel = new Label("Administrator");
        adminLabel.getStyleClass().add("role-card-title");
        Label adminDesc = new Label("Manage books, issue,\nand handle reservations");
        adminDesc.getStyleClass().add("role-card-desc");
        adminCard.getChildren().addAll(adminIcon, adminLabel, adminDesc);
        adminCard.setOnMouseClicked(e -> launchAdmin(stage));

        // user card — clicking it opens the user login screen
        VBox userCard = new VBox(16);
        userCard.getStyleClass().add("role-card");
        userCard.setAlignment(Pos.CENTER);
        Label userIcon = new Label("📖");
        userIcon.getStyleClass().add("role-icon");
        Label userLabel = new Label("User");
        userLabel.getStyleClass().add("role-card-title");
        Label userDesc = new Label("Browse and reserve\navailable books");
        userDesc.getStyleClass().add("role-card-desc");
        userCard.getChildren().addAll(userIcon, userLabel, userDesc);
        userCard.setOnMouseClicked(e -> showUserLogin(stage));

        cards.getChildren().addAll(adminCard, userCard);
        root.getChildren().addAll(title, subtitle, cards);
        return root;
    }

    // builds the main admin layout: sidebar on the left, content in the center
    private void launchAdmin(Stage stage) {
        BorderPane root = new BorderPane();
        root.setCenter(content);
        root.setLeft(createAdminSideBar(stage));
        content.getChildren().setAll(createDashboard());
        Scene scene = new Scene(root, 1100, 650);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setScene(scene);
    }

    // simple form to enter user name and login before browsing books
    private void showUserLogin(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("role-selection-root");

        Label title = new Label("User Login");
        title.getStyleClass().add("dashboard-title");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setMaxWidth(320);

        TextField loginField = new TextField();
        loginField.setPromptText("Login (username)");
        loginField.setMaxWidth(320);

        Button continueBtn = new Button("Continue");
        continueBtn.setMaxWidth(320);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #D4846A;");

        // back button — returns to the role selection screen
        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            Scene scene = new Scene(createRoleSelection(stage), 1100, 650);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            stage.setScene(scene);
        });

        // validates fields, creates a User object and opens the user view
        continueBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty() || loginField.getText().isEmpty()) {
                errorLabel.setText("Please fill in both fields.");
                return;
            }
            currentUser = new User(nameField.getText(), loginField.getText());
            launchUser(stage);
        });

        root.getChildren().addAll(backBtn, title, nameField, loginField, continueBtn, errorLabel);
        Scene scene = new Scene(root, 1100, 650);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setScene(scene);
    }

    // user view layout — sidebar + content area
    private void launchUser(Stage stage) {
        StackPane userContent = new StackPane();
        BorderPane root = new BorderPane();
        root.setCenter(userContent);
        root.setLeft(createUserSideBar(stage, userContent));
        userContent.getChildren().setAll(createUserBrowse(userContent));
        Scene scene = new Scene(root, 1100, 650);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setScene(scene);
    }

    // sidebar for the user view with navigation buttons
    private VBox createUserSideBar(Stage stage, StackPane userContent) {
        VBox side = new VBox(15);
        side.setPadding(new Insets(20));

        Label logo = new Label("LIBRARY");

        Button browseBtn = new Button("Browse Books");
        browseBtn.setMaxWidth(Double.MAX_VALUE);
        browseBtn.setOnAction(e -> userContent.getChildren().setAll(createUserBrowse(userContent)));

        Button myReservationBtn = new Button("My Reservation");
        myReservationBtn.setMaxWidth(Double.MAX_VALUE);
        myReservationBtn.setOnAction(e -> userContent.getChildren().setAll(createUserReservation()));

        // exit button — clears current user and returns to role selection
        Button backBtn = new Button("← Exit");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            currentUser = null;
            Scene scene = new Scene(createRoleSelection(stage), 1100, 650);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            stage.setScene(scene);
        });

        Label userLabel = new Label("Logged in as:\n" + currentUser.getName());
        userLabel.getStyleClass().add("user-info-label");

        side.getChildren().addAll(logo, browseBtn, myReservationBtn, backBtn, userLabel);
        return side;
    }

    // page where users browse and reserve available books
    private Pane createUserBrowse(StackPane userContent) {
        VBox page = new VBox(16);
        page.setPadding(new Insets(36, 40, 36, 40));

        Label title = new Label("Available Books");
        title.getStyleClass().add("dashboard-title");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by title or author...");

        TableView<Book> table = new TableView<>();

        // table columns — each gets its value from the Book object
        TableColumn<Book, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAuthor()));

        TableColumn<Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> {
            Book b = d.getValue();
            if (b.isReserved()) return new SimpleStringProperty("Reserved");
            return new SimpleStringProperty("Available");
        });

        table.getColumns().addAll(idCol, titleCol, authorCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // filtered list — updates automatically as the user types in the search field
        FilteredList<Book> filtered = new FilteredList<>(library.availableBooks, b -> true);
        searchField.textProperty().addListener((obs, ov, nv) -> {
            filtered.setPredicate(b -> {
                if (nv == null || nv.isEmpty()) return true;
                String lower = nv.toLowerCase();
                return b.getTitle().toLowerCase().contains(lower) || b.getAuthor().toLowerCase().contains(lower);
            });
        });
        table.setItems(filtered);

        Button reserveBtn = new Button("Reserve");
        reserveBtn.setDisable(true);

        // enable reserve button only if a free book is selected and user has no reservation yet
        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            reserveBtn.setDisable(nv == null || nv.isReserved() || currentUser.hasReservation());
        });

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        // reserve action — checks for existing reservation, then reserves
        reserveBtn.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            if (currentUser.hasReservation()) {
                alert.setTitle("Info");
                alert.setContentText("You already have an active reservation.");
                alert.showAndWait();
                return;
            }
            library.reserveBook(currentUser, selected);
            alert.setTitle("Success");
            alert.setContentText("Book \"" + selected.getTitle() + "\" reserved successfully!");
            alert.showAndWait();
            userContent.getChildren().setAll(createUserBrowse(userContent));
        });

        page.getChildren().addAll(title, searchField, table, reserveBtn);
        return page;
    }

    // page that shows the user's currently reserved book (or "no reservation")
    private Pane createUserReservation() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(36, 40, 36, 40));

        Label title = new Label("My Reservation");
        title.getStyleClass().add("dashboard-title");

        if (!currentUser.hasReservation()) {
            Label none = new Label("You have no active reservations.");
            none.getStyleClass().add("greeting-label");
            page.getChildren().addAll(title, none);
            return page;
        }

        Book b = currentUser.getReservedBook();

        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card");
        card.setMaxWidth(400);

        Label bookTitle = new Label("📚  " + b.getTitle());
        bookTitle.getStyleClass().add("stat-card-value");
        bookTitle.setStyle("-fx-font-size: 18px;");

        Label bookAuthor = new Label("Author: " + b.getAuthor());
        bookAuthor.getStyleClass().add("progress-name-label");

        Label status = new Label("Status: Reserved — waiting for admin to issue");
        status.getStyleClass().add("tip-text");

        card.getChildren().addAll(bookTitle, bookAuthor, status);
        page.getChildren().addAll(title, card);
        return page;
    }

    // sidebar for the admin panel with all admin functions
    private VBox createAdminSideBar(Stage stage) {
        VBox side = new VBox(15);
        side.setPadding(new Insets(20));

        Label logo = new Label("LIBRARY");

        Button dash = menuBtn("Dashboard", createDashboard());
        Button check = new Button("Check Books");
        check.setMaxWidth(Double.MAX_VALUE);
        // rebuild the page each time so the table refreshes with current data
        check.setOnAction(event -> content.getChildren().setAll(createCheck()));
        Button issue = menuBtn("Issue Book", createIssue());
        Button borrowed = menuBtn("Borrowed", createBorrowed());
        Button available = menuBtn("Available", createAvailable());
        Button reserved = menuBtn("Reserved", createReserved());
        Button add = menuBtn("Add Book", createAdd());

        Button backBtn = new Button("← Exit");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            Scene scene = new Scene(createRoleSelection(stage), 1100, 650);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            stage.setScene(scene);
        });

        side.getChildren().addAll(logo, dash, check, issue, borrowed, available, reserved, add, backBtn);
        return side;
    }

    // helper to create a sidebar button that switches the central pane
    private Button menuBtn(String text, Pane pane) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(event -> content.getChildren().setAll(pane));
        return b;
    }

    // admin dashboard — stat cards, progress bars and info section
    private Pane createDashboard() {
        VBox page = new VBox(28);
        page.getStyleClass().add("dashboard-root");

        VBox header = new VBox(4);
        Label greeting = new Label("Welcome back");
        greeting.getStyleClass().add("greeting-label");
        Label title = new Label("Library Dashboard");
        title.getStyleClass().add("dashboard-title");
        header.getChildren().addAll(greeting, title);

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.getStyleClass().add("divider");

        // three stat cards: total / borrowed / available
        HBox cardsRow = new HBox(20);
        cardsRow.setPadding(new Insets(4, 0, 4, 0));
        cardsRow.getChildren().addAll(
                buildStatCard("TOTAL BOOKS", library.amountOfBooks, "#C8A882", "📚"),
                buildStatCard("BORROWED", library.amountOfBooksBorrowed, "#D4846A", "📤"),
                buildStatCard("AVAILABLE", library.amountOfBooksAvailable, "#7A9E7E", "📥")
        );

        VBox progressSection = new VBox(12);
        progressSection.getStyleClass().add("progress-section");

        Label progressTitle = new Label("Collection Overview");
        progressTitle.getStyleClass().add("progress-section-title");

        VBox borrowedProgress = buildProgressBar("Borrowed Books", library.amountOfBooksBorrowed, library.amountOfBooks, "#D4846A");
        VBox availableProgress = buildProgressBar("Available Books", library.amountOfBooksAvailable, library.amountOfBooks, "#7A9E7E");

        progressSection.getChildren().addAll(progressTitle, borrowedProgress, availableProgress);

        HBox infoRow = new HBox(16);

        VBox tipBox = new VBox(8);
        tipBox.getStyleClass().add("tip-box");
        HBox.setHgrow(tipBox, Priority.ALWAYS);

        Label tipIcon = new Label("💡");
        tipIcon.getStyleClass().add("icon-label");
        Label tipTitle = new Label("Quick Tip");
        tipTitle.getStyleClass().add("tip-title");
        Label tipText = new Label("Use 'Reserved' section to\nissue books from the queue.");
        tipText.getStyleClass().add("tip-text");
        tipBox.getChildren().addAll(tipIcon, tipTitle, tipText);

        VBox statusBox = new VBox(8);
        statusBox.getStyleClass().add("status-box");
        HBox.setHgrow(statusBox, Priority.ALWAYS);

        Label statusIcon = new Label("📊");
        statusIcon.getStyleClass().add("icon-label");
        Label statusTitle = new Label("Library Status");
        statusTitle.getStyleClass().add("status-title");

        Label statusValue = new Label();
        statusValue.getStyleClass().add("status-value");

        // recalculates the borrowed % whenever the totals change
        Runnable updateStatus = () -> {
            int total = library.amountOfBooks.get();
            int borrowed = library.amountOfBooksBorrowed.get();
            if (total == 0) {
                statusValue.setText("No books in library yet.");
            } else {
                int pct = (int) ((borrowed / (double) total) * 100);
                statusValue.setText(pct + "% of books are\ncurrently borrowed.");
            }
        };
        updateStatus.run();
        library.amountOfBooks.addListener((o, ov, nv) -> updateStatus.run());
        library.amountOfBooksBorrowed.addListener((o, ov, nv) -> updateStatus.run());

        statusBox.getChildren().addAll(statusIcon, statusTitle, statusValue);
        infoRow.getChildren().addAll(tipBox, statusBox);

        page.getChildren().addAll(header, divider, cardsRow, progressSection, infoRow);
        return page;
    }

    // builds a single stat card with icon, number bound to a property, and label
    private VBox buildStatCard(String label, IntegerProperty valueProp, String accent, String icon) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-background-color: " + accent + "22;" +
                        "-fx-background-radius: 50;" +
                        "-fx-padding: 10 12 10 12;"
        );

        Label valueLabel = new Label();
        // bind the label text to the property so it updates automatically
        valueLabel.textProperty().bind(valueProp.asString());
        valueLabel.getStyleClass().add("stat-card-value");

        Region accentBar = new Region();
        accentBar.setPrefHeight(3);
        accentBar.setPrefWidth(40);
        accentBar.setStyle(
                "-fx-background-color: " + accent + ";" +
                        "-fx-background-radius: 2;"
        );

        Label titleLabel = new Label(label);
        titleLabel.getStyleClass().add("stat-card-title");

        card.getChildren().addAll(iconLabel, valueLabel, accentBar, titleLabel);
        return card;
    }

    // custom progress bar — fill width is a fraction of the track
    private VBox buildProgressBar(String label, IntegerProperty valueProp, IntegerProperty totalProp, String color) {
        VBox container = new VBox(6);

        HBox labelRow = new HBox();
        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("progress-name-label");
        Label countLabel = new Label();
        countLabel.getStyleClass().add("progress-count-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        labelRow.getChildren().addAll(nameLabel, spacer, countLabel);

        StackPane track = new StackPane();
        track.getStyleClass().add("progress-track");
        track.setPrefHeight(8);
        track.setMaxWidth(Double.MAX_VALUE);

        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        fill.setPrefHeight(8);
        StackPane.setAlignment(fill, javafx.geometry.Pos.CENTER_LEFT);

        track.getChildren().add(fill);

        // recalculates the fill width whenever the value or total changes
        Runnable update = () -> {
            int total = totalProp.get();
            int val = valueProp.get();
            countLabel.setText(val + " / " + total);
            double pct = total == 0 ? 0 : (double) val / total;
            fill.prefWidthProperty().bind(track.widthProperty().multiply(pct));
        };
        update.run();
        valueProp.addListener((o, ov, nv) -> update.run());
        totalProp.addListener((o, ov, nv) -> update.run());

        container.getChildren().addAll(labelRow, track);
        return container;
    }

    // page to view all books (available + borrowed), search, sort and delete
    private Pane createCheck() {
        VBox searchBox = new VBox(12);
        searchBox.setPadding(new Insets(30));

        TextField searchField = new TextField();
        searchField.setPromptText("Search book...");

        HBox sortRow = new HBox(10);
        sortRow.setAlignment(Pos.CENTER_LEFT);
        Label sortLabel = new Label("Sort by:");
        sortLabel.getStyleClass().add("progress-name-label");

        Button sortTitle = new Button("Title");
        Button sortId = new Button("ID");
        Button sortAuthor = new Button("Author");
        sortRow.getChildren().addAll(sortLabel, sortTitle, sortId, sortAuthor);

        TableView<Book> booksTable = new TableView<>();

        TableColumn<Book, String> idColumn = new TableColumn<>("Book ID");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));

        TableColumn<Book, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> {
            Book b = data.getValue();
            if (b.getBorrowed()) return new SimpleStringProperty("Borrowed");
            if (b.isReserved()) return new SimpleStringProperty("Reserved");
            return new SimpleStringProperty("Available");
        });

        booksTable.getColumns().addAll(idColumn, titleColumn, authorColumn, statusColumn);
        booksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // combine available + borrowed into one list to show all books at once
        ObservableList<Book> allBooks = FXCollections.observableArrayList();
        allBooks.addAll(library.availableBooks);
        allBooks.addAll(library.borrowedBooks);

        // listeners — keep the combined list in sync with library changes
        library.availableBooks.addListener((javafx.collections.ListChangeListener<Book>) change -> {
            while (change.next()) {
                if (change.wasAdded()) allBooks.addAll(change.getAddedSubList());
                if (change.wasRemoved()) allBooks.removeAll(change.getRemoved());
            }
        });

        library.borrowedBooks.addListener((javafx.collections.ListChangeListener<Book>) change -> {
            while (change.next()) {
                if (change.wasAdded()) allBooks.addAll(change.getAddedSubList());
                if (change.wasRemoved()) allBooks.removeAll(change.getRemoved());
            }
        });

        FilteredList<Book> filteredBooks = new FilteredList<>(allBooks, book -> true);

        // search — filters by title/author starting with the typed text
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBooks.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return book.getTitle().toLowerCase().startsWith(lower) || book.getAuthor().toLowerCase().startsWith(lower);
            });
        });

        booksTable.setItems(filteredBooks);

        // sort buttons — use custom quicksort from BookSorter
        sortTitle.setOnAction(e -> {
            List<Book> sorted = new ArrayList<>(allBooks);
            BookSorter.quickSortByTitle(sorted, 0, sorted.size() - 1);
            allBooks.setAll(sorted);
        });

        sortId.setOnAction(e -> {
            List<Book> sorted = new ArrayList<>(allBooks);
            BookSorter.quickSortById(sorted, 0, sorted.size() - 1);
            allBooks.setAll(sorted);
        });

        sortAuthor.setOnAction(e -> {
            List<Book> sorted = new ArrayList<>(allBooks);
            BookSorter.quickSortByAuthor(sorted, 0, sorted.size() - 1);
            allBooks.setAll(sorted);
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setDisable(true);

        // enable delete only when a row is selected
        booksTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            deleteButton.setDisable(newValue == null);
        });

        // delete with a confirmation dialog
        deleteButton.setOnAction(event -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.getDialogPane().getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
                confirm.setTitle("Delete Book");
                confirm.setHeaderText(null);
                confirm.setContentText("Are you sure you want to delete \"" + selectedBook.getTitle() + "\"?");

                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        library.deleteBook(selectedBook.getId());
                        allBooks.remove(selectedBook);
                    }
                });
            }
        });

        searchBox.getChildren().addAll(searchField, sortRow, booksTable, deleteButton);
        return searchBox;
    }

    // page where admin issues a book directly by entering name/person/days
    private Pane createIssue() {
        VBox issueBox = new VBox(12);
        issueBox.setPadding(new Insets(30));

        TextField bookField = new TextField();
        bookField.setPromptText("Book");
        TextField personField = new TextField();
        personField.setPromptText("Person");
        TextField daysField = new TextField();
        daysField.setPromptText("Days");
        Button issueButton = new Button("Issue");

        Alert issueAlert = new Alert(Alert.AlertType.INFORMATION);
        issueAlert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        issueAlert.setHeaderText(null);

        issueButton.setOnAction(event -> {
            IntegerProperty days = new SimpleIntegerProperty(0);
            boolean checkInt = false;

            // try to parse days as a number; if not, show an error
            try {
                days = new SimpleIntegerProperty(Integer.parseInt(daysField.getText()));
                checkInt = true;
            } catch (NumberFormatException e) {
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Days must be a number!");
            }

            // main case — all fields are filled and days is valid
            if (checkInt && !bookField.getText().isEmpty() && !personField.getText().isEmpty() && !daysField.getText().isEmpty()) {
                String bookName = bookField.getText();
                String personName = personField.getText();
                boolean checkBook = false;

                // search the book among available ones
                for (Book issueBook : library.availableBooks) {
                    if (issueBook.getTitle().equals(bookName)) {
                        issueAlert.setTitle("Success");
                        issueAlert.setContentText("Book successfully issued!");

                        library.availableBooks.remove(issueBook);

                        issueBook.setDays(days.get());
                        issueBook.setPerson(personName);
                        issueBook.setBorrowed(true);

                        library.borrowBook(issueBook, issueBook.getId());

                        bookField.clear();
                        personField.clear();
                        daysField.clear();

                        checkBook = true;
                        break;
                    }
                }

                if (!checkBook) {
                    issueAlert.setTitle("Error");
                    issueAlert.setContentText("Book is not available!");
                    bookField.clear();
                }

                // the rest are validation cases for different combinations of empty fields
            } else if (!bookField.getText().isEmpty() && personField.getText().isEmpty() && daysField.getText().isEmpty()) {
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the customer's name and the days!");
            } else if (!bookField.getText().isEmpty() && !personField.getText().isEmpty() && daysField.getText().isEmpty()) {
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the days!");
            } else if (!bookField.getText().isEmpty() && personField.getText().isEmpty() && !daysField.getText().isEmpty()) {
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the customer's name!");
            } else if (bookField.getText().isEmpty() && !personField.getText().isEmpty() && !daysField.getText().isEmpty() && checkInt) {
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the book to borrow!");
            } else if (bookField.getText().isEmpty() && !personField.getText().isEmpty() && daysField.getText().isEmpty()) {
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the days and the book to borrow!");
            } else if (bookField.getText().isEmpty() && personField.getText().isEmpty() && !daysField.getText().isEmpty() && checkInt) {
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the book to borrow and customer's name!");
            } else {
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please fill all the fields!");
            }

            issueAlert.showAndWait();
        });

        issueBox.getChildren().addAll(bookField, personField, daysField, issueButton);
        return issueBox;
    }

    // page that shows the reservation queue and lets admin issue to the first user
    private Pane createReserved() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(36, 40, 36, 40));

        Label title = new Label("Reserved Books — Queue");
        title.getStyleClass().add("dashboard-title");

        Label subtitle = new Label("Users are ordered by reservation time. Issue to the first in queue.");
        subtitle.getStyleClass().add("greeting-label");

        TableView<User> queueTable = new TableView<>();

        // # column — shows position in the queue (1-based)
        TableColumn<User, String> posCol = new TableColumn<>("#");
        posCol.setCellValueFactory(d -> {
            int idx = library.reservationQueue.indexOf(d.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(idx));
        });
        posCol.setMaxWidth(50);

        TableColumn<User, String> userNameCol = new TableColumn<>("User Name");
        userNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<User, String> loginCol = new TableColumn<>("Login");
        loginCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLogin()));

        TableColumn<User, String> bookCol = new TableColumn<>("Reserved Book");
        bookCol.setCellValueFactory(d -> {
            Book b = d.getValue().getReservedBook();
            return new SimpleStringProperty(b != null ? b.getTitle() : "—");
        });

        TableColumn<User, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(d -> {
            Book b = d.getValue().getReservedBook();
            return new SimpleStringProperty(b != null ? b.getAuthor() : "—");
        });

        queueTable.getColumns().addAll(posCol, userNameCol, loginCol, bookCol, authorCol);
        queueTable.setItems(library.reservationQueue);
        queueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TextField daysField = new TextField();
        daysField.setPromptText("Days to issue");
        daysField.setMaxWidth(200);

        Button issueBtn = new Button("Issue to First in Queue");
        issueBtn.setDisable(library.reservationQueue.isEmpty());

        // disable the issue button when the queue becomes empty
        library.reservationQueue.addListener((javafx.collections.ListChangeListener<User>) c -> {
            issueBtn.setDisable(library.reservationQueue.isEmpty());
        });

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        // issue the reserved book to the first user in the queue
        issueBtn.setOnAction(e -> {
            if (library.userQueue.isEmpty()) return;
            int days;
            try {
                days = Integer.parseInt(daysField.getText());
            } catch (NumberFormatException ex) {
                alert.setTitle("Error");
                alert.setContentText("Please enter valid number of days.");
                alert.showAndWait();
                return;
            }

            // peek — get the first user without removing them yet
            User nextUser = library.userQueue.peek();
            Book book = nextUser.getReservedBook();
            if (book == null) return;

            book.setDays(days);
            library.issueReservation(nextUser);
            daysField.clear();

            alert.setTitle("Success");
            alert.setContentText("Book \"" + book.getTitle() + "\" issued to " + nextUser.getName() + " for " + days + " days.");
            alert.showAndWait();

            // refresh the page to show the updated queue
            content.getChildren().setAll(createReserved());
        });

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getChildren().addAll(daysField, issueBtn);

        page.getChildren().addAll(title, subtitle, queueTable, controls);
        return page;
    }

    // simple page that lists all currently borrowed books
    private Pane createBorrowed() {
        VBox borrowedBox = new VBox(20);
        borrowedBox.setPadding(new Insets(30));

        TableView<Book> borrowedList = new TableView<>();

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));

        TableColumn<Book, String> personColumn = new TableColumn<>("Person");
        personColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPerson()));

        TableColumn<Book, String> daysColumn = new TableColumn<>("Days");
        daysColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStringDays()));

        borrowedList.getColumns().addAll(titleColumn, authorColumn, personColumn, daysColumn);
        borrowedList.setItems(library.borrowedBooks);
        borrowedList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        borrowedBox.getChildren().addAll(borrowedList);
        return borrowedBox;
    }

    // simple page that lists all available books
    private Pane createAvailable() {
        VBox availableBox = new VBox(20);
        availableBox.setPadding(new Insets(30));

        TableView<Book> availableList = new TableView<>();

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));

        availableList.getColumns().addAll(titleColumn, authorColumn);
        availableList.setItems(library.availableBooks);
        availableList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        availableBox.getChildren().addAll(availableList);
        return availableBox;
    }

    // page where admin can add a new book
    private Pane createAdd() {
        VBox addBox = new VBox(12);
        addBox.setPadding(new Insets(30));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        Button addButton = new Button("Add Book");

        Alert addAlert = new Alert(Alert.AlertType.INFORMATION);
        addAlert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        addAlert.setHeaderText(null);

        addButton.setOnAction(event -> {
            // validate input — both fields must be filled
            if (!titleField.getText().isEmpty() && !authorField.getText().isEmpty()) {
                Book newBook = new Book(titleField.getText(), authorField.getText());
                library.addBook(newBook);
                addAlert.setTitle("Success");
                addAlert.setContentText("Book added successfully!");
                titleField.clear();
                authorField.clear();
            } else if (titleField.getText().isEmpty() && !authorField.getText().isEmpty()) {
                addAlert.setTitle("Error");
                addAlert.setContentText("Please enter the title!");
            } else if (!titleField.getText().isEmpty() && authorField.getText().isEmpty()) {
                addAlert.setTitle("Error");
                addAlert.setContentText("Please enter the author!");
            } else {
                addAlert.setTitle("Error");
                addAlert.setContentText("Please enter the title and the author!");
            }
            addAlert.showAndWait();
        });

        addBox.getChildren().addAll(titleField, authorField, addButton);
        return addBox;
    }
}