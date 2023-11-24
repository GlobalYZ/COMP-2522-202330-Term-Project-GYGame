package com.example.game;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.stage.Stage;

import java.io.IOException;

public class GameController {

    @FXML
    private HBox myBox;

    public HBox loadImage() {
        Image image = new Image("file:../../../../../asset/Image/background.jpg");
        image.errorProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("Error loading image!");
            }
        });
        ImageView imageView = new ImageView(image);

        // Bind the fit width and height of the ImageView to the width and height of the HBox
        imageView.setFitWidth(200); // Set width
        imageView.setFitHeight(150); // Set height

        myBox.getChildren().add(imageView); // Add the image view to the HBox
        return myBox;
    }


}