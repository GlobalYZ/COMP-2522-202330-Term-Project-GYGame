package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.io.IOException;
import javafx.scene.Parent;

public class GameManager extends Application {
    @FXML private Button newGameButton;
    @FXML private Button loadGameButton;

    @FXML
    private void handleNewGame(ActionEvent event) {
        System.out.println("New Game");
    }
    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage.setScene(new Scene(root, 400, 275));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}