module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens com.example.game to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.example.game;
}