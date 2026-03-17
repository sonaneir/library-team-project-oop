package app;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.io.IOException;

public class LibraryApplication extends Application {
    private StackPane content = new StackPane();
    private Library library = new Library();

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        root.setCenter(content);
        root.setLeft(createSideBar());

        content.getChildren().add(createDashboard());

        Scene scene = new Scene(root, 1100, 650);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("Library Manager");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSideBar() {
        VBox side = new VBox(15);
        side.setPadding(new Insets(20));

        Label logo = new Label("LIBRARY");

        Button dash = menuBtn("Dashboard", createDashboard());
        Button check = new Button("Check Books");
        check.setMaxWidth(Double.MAX_VALUE);
        check.setOnAction(event -> content.getChildren().setAll(createCheck()));
        Button issue = menuBtn("Issue Book", createIssue());
        Button borrowed = menuBtn("Borrowed", createBorrowed());
        Button available = menuBtn("Available", createAvailable());
        Button add =  menuBtn("Add Book", createAdd());

        side.getChildren().addAll(logo, dash, check, issue, borrowed, available, add);

        return side;
    }

    private Button menuBtn(String text, Pane pane) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);

        b.setOnAction(event -> {content.getChildren().setAll(pane);});

        return b;
    }

    private Pane createDashboard() {
        VBox page = new VBox(28);
        page.setPadding(new Insets(40, 44, 40, 44));
        page.setStyle("-fx-background-color: #F5F0EB;");

        VBox header = new VBox(4);
        Label greeting = new Label("Welcome back");
        greeting.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: #A09080;" +
                        "-fx-letter-spacing: 1px;"
        );
        Label title = new Label("Library Dashboard");
        title.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2C2420;"
        );
        header.getChildren().addAll(greeting, title);

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color: #E5DDD5;");

        HBox cardsRow = new HBox(20);
        cardsRow.setPadding(new Insets(4, 0, 4, 0));

        cardsRow.getChildren().addAll(
                buildStatCard("TOTAL BOOKS",    library.amountOfBooks,          "#C8A882", "📚"),
                buildStatCard("BORROWED",       library.amountOfBooksBorrowed,   "#D4846A", "📤"),
                buildStatCard("AVAILABLE",      library.amountOfBooksAvailable,  "#7A9E7E", "📥")
        );

        VBox progressSection = new VBox(12);
        progressSection.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #E5DDD5;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 14;" +
                        "-fx-padding: 24 28 24 28;" +
                        "-fx-effect: dropshadow(gaussian, rgba(44,36,32,0.06), 10, 0, 0, 2);"
        );

        Label progressTitle = new Label("Collection Overview");
        progressTitle.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2C2420;" +
                        "-fx-letter-spacing: 0.5px;"
        );

        VBox borrowedProgress = buildProgressBar(
                "Borrowed Books", library.amountOfBooksBorrowed, library.amountOfBooks, "#D4846A"
        );

        VBox availableProgress = buildProgressBar(
                "Available Books", library.amountOfBooksAvailable, library.amountOfBooks, "#7A9E7E"
        );

        progressSection.getChildren().addAll(progressTitle, borrowedProgress, availableProgress);

        HBox infoRow = new HBox(16);

        VBox tipBox = new VBox(8);
        tipBox.setStyle(
                "-fx-background-color: #FDF4EA;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #E8D5BC;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-padding: 20 24 20 24;"
        );
        HBox.setHgrow(tipBox, javafx.scene.layout.Priority.ALWAYS);

        Label tipIcon = new Label("💡");
        tipIcon.setStyle("-fx-font-size: 20px;");
        Label tipTitle = new Label("Quick Tip");
        tipTitle.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #A07E58;"
        );
        Label tipText = new Label("Use 'Issue Book' to lend\na book to a customer.");
        tipText.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: #6B5A48;" +
                        "-fx-wrap-text: true;"
        );
        tipBox.getChildren().addAll(tipIcon, tipTitle, tipText);

        VBox statusBox = new VBox(8);
        statusBox.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #E5DDD5;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-padding: 20 24 20 24;"
        );
        HBox.setHgrow(statusBox, javafx.scene.layout.Priority.ALWAYS);

        Label statusIcon = new Label("📊");
        statusIcon.setStyle("-fx-font-size: 20px;");
        Label statusTitle = new Label("Library Status");
        statusTitle.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2C2420;"
        );

        Label statusValue = new Label();
        statusValue.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: #6B6560;" +
                        "-fx-wrap-text: true;"
        );

        Runnable updateStatus = () -> {
            int total = library.amountOfBooks.get();
            int borrowed = library.amountOfBooksBorrowed.get();
            if (total == 0) {
                statusValue.setText("No books in library yet.");
            } else {
                int pct = (int)((borrowed / (double) total) * 100);
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

    private VBox buildStatCard(String label, javafx.beans.property.IntegerProperty valueProp, String accent, String icon) {
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #E5DDD5;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 14;" +
                        "-fx-padding: 22 26 22 26;" +
                        "-fx-effect: dropshadow(gaussian, rgba(44,36,32,0.07), 12, 0, 0, 3);"
        );
        HBox.setHgrow(card, javafx.scene.layout.Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-background-color: " + accent + "22;" +
                        "-fx-background-radius: 50;" +
                        "-fx-padding: 10 12 10 12;"
        );

        Label valueLabel = new Label();
        valueLabel.textProperty().bind(valueProp.asString());
        valueLabel.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 38px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2C2420;"
        );

        Region accentBar = new Region();
        accentBar.setPrefHeight(3);
        accentBar.setPrefWidth(40);
        accentBar.setStyle(
                "-fx-background-color: " + accent + ";" +
                        "-fx-background-radius: 2;"
        );

        Label titleLabel = new Label(label);
        titleLabel.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 10px;" +
                        "-fx-text-fill: #A09080;" +
                        "-fx-letter-spacing: 2px;"
        );

        card.getChildren().addAll(iconLabel, valueLabel, accentBar, titleLabel);
        return card;
    }

    private VBox buildProgressBar(String label, javafx.beans.property.IntegerProperty valueProp,
                                  javafx.beans.property.IntegerProperty totalProp, String color) {
        VBox container = new VBox(6);

        HBox labelRow = new HBox();
        Label nameLabel = new Label(label);
        nameLabel.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: #6B5A48;"
        );
        Label countLabel = new Label();
        countLabel.setStyle(
                "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: #A09080;"
        );
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        labelRow.getChildren().addAll(nameLabel, spacer, countLabel);

        StackPane track = new StackPane();
        track.setStyle(
                "-fx-background-color: #F0E8E0;" +
                        "-fx-background-radius: 4;" +
                        "-fx-pref-height: 8;"
        );
        track.setPrefHeight(8);
        track.setMaxWidth(Double.MAX_VALUE);

        Region fill = new Region();
        fill.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 4;"
        );
        fill.setPrefHeight(8);
        StackPane.setAlignment(fill, javafx.geometry.Pos.CENTER_LEFT);

        track.getChildren().add(fill);

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

    private Pane createCheck() {
        VBox searchBox = new VBox(15);
        searchBox.setPadding(new Insets(30));

        TextField searchField = new TextField();
        searchField.setPromptText("Search book...");

        TableView<Book> booksTable = new TableView<>();

        TableColumn<Book, String> idColumn = new TableColumn("Book ID");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));

        TableColumn<Book, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getBorrowed() ? "Borrowed" : "Available"
        ));

        booksTable.getColumns().addAll(idColumn, titleColumn, authorColumn, statusColumn);
        booksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ObservableList<Book> allBooks = FXCollections.observableArrayList();
        allBooks.addAll(library.availableBooks);
        allBooks.addAll(library.borrowedBooks);

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

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBooks.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return book.getTitle().toLowerCase().startsWith(lower) || book.getAuthor().toLowerCase().startsWith(lower);
            });
        });

        booksTable.setItems(filteredBooks);

        Button deleteButton = new Button("Delete");
        deleteButton.setDisable(true);

        booksTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            deleteButton.setDisable(newValue == null);
        });

        deleteButton.setOnAction(event -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
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


        searchBox.getChildren().addAll(searchField, booksTable, deleteButton);

        return searchBox;
    }

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
        issueAlert.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        issueAlert.setHeaderText(null);

        issueButton.setOnAction(event -> {
            IntegerProperty days = new SimpleIntegerProperty(0);
            boolean checkInt = false;

            try {
                days = new SimpleIntegerProperty(Integer.parseInt(daysField.getText()));
                checkInt = true;
            } catch (NumberFormatException e) {
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Days must be a number!");
            }

            if (checkInt && !bookField.getText().isEmpty() &&  !personField.getText().isEmpty() && !daysField.getText().isEmpty()) {
                String bookName = bookField.getText();
                String personName = personField.getText();
                boolean checkBook = false;


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

            } else if (!bookField.getText().isEmpty() &&  personField.getText().isEmpty() && daysField.getText().isEmpty()){
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the customer's name and the days!");
            } else if (!bookField.getText().isEmpty() &&  !personField.getText().isEmpty() && daysField.getText().isEmpty()){
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the days!");
            } else if (!bookField.getText().isEmpty() && personField.getText().isEmpty() && !daysField.getText().isEmpty()){
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the customer's name!");
            } else if (bookField.getText().isEmpty() && !personField.getText().isEmpty() && !daysField.getText().isEmpty() && checkInt){
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the book to borrow!");
            } else if (bookField.getText().isEmpty() && !personField.getText().isEmpty() && daysField.getText().isEmpty() && checkInt){
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the days and the book to borrow!");
            } else if (bookField.getText().isEmpty() &&  personField.getText().isEmpty() && !daysField.getText().isEmpty() && checkInt){
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please enter the book to borrow and customer's name!");
            } else if (bookField.getText().isEmpty() &&  personField.getText().isEmpty() && daysField.getText().isEmpty() && checkInt){
                issueAlert.setTitle("Error");
                issueAlert.setContentText("Please fill all the fields!");
            }

            issueAlert.showAndWait();
        });

        issueBox.getChildren().addAll(bookField, personField, daysField, issueButton);

        return issueBox;
    }

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

    private Pane createAvailable(){
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

    private Pane createAdd() {
        VBox addBox = new VBox(12);
        addBox.setPadding(new Insets(30));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        Button addButton = new Button("Add Book");

        Alert addAlert = new Alert(Alert.AlertType.INFORMATION);
        addAlert.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        addAlert.setHeaderText(null);

        addButton.setOnAction(event -> {
            if(!titleField.getText().isEmpty() && !authorField.getText().isEmpty()){
                Book newBook = new Book(titleField.getText(), authorField.getText());

                library.addBook(newBook);

                addAlert.setTitle("Success");
                addAlert.setContentText("Book added successfully!");

                titleField.clear();
                authorField.clear();
            } else if (titleField.getText().isEmpty() && !authorField.getText().isEmpty()){
                addAlert.setTitle("Error");
                addAlert.setContentText("Please enter the title!");
            } else if (!titleField.getText().isEmpty() && authorField.getText().isEmpty()){
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
