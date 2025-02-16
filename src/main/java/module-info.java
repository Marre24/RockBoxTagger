module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;


    opens com.example.rockboxtagger to javafx.fxml;
    exports com.example.rockboxtagger;
}