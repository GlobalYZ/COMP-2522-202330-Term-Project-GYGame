package com.example.game;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;


public class GameManager extends Application {
    private double time;


    @FXML
    private void startNewGame(ActionEvent event) {
        try {
            lunchPlayBoard((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException appropriately
        }
    }

    private Parent setContent(Parent root){
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                time += 0.03;
                if(time >= 0.5) {
//                    render();
                    time = 0;
                }
            }
        };
        timer.start();
        return root;

    }

    public void lunchPlayBoard(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("play-board.fxml"));
        Parent root = loader.load(); // Load the FXML file and get the root node

        // Cast the root node to AnchorPane (or the appropriate type)
        AnchorPane anchorPane = (AnchorPane) root;

        // Create an ImageView and set an image
        Image image = new Image("file:../../../../../asset/Image/background.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200); // Set width
        imageView.setFitHeight(150); // Set height

        // Add the ImageView to the children of the AnchorPane and position it
        AnchorPane.setTopAnchor(imageView, 50.0); // Set top anchor
        AnchorPane.setLeftAnchor(imageView, 50.0); // Set left anchor

        // Add the ImageView to the children of the AnchorPane
        anchorPane.getChildren().add(imageView);

        // Set up the stage and scene
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.setTitle("EcoStack");
        stage.show();


    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage.setScene(new Scene(root, 400, 275));
        stage.setTitle("EcoStack");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}