module com.example.libraryteamprojectoop {
    requires javafx.controls;
    requires javafx.fxml;

    opens app to javafx.fxml;
    exports app;
}