package com.example.game;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.geometry.Pos;


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
        Image image = new Image("file:./src/asset/Image/background.jpg");
        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(anchorPane.widthProperty());
        imageView.fitHeightProperty().bind(anchorPane.heightProperty());

        HBox gameContainer = new HBox();


        // 获取屏幕尺寸
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        gameContainer.setPrefSize(screenWidth, screenHeight);

        gameContainer.setAlignment(Pos.CENTER);

//        int width = (int) imageView.getBoundsInParent().getWidth()/2 + 350;
//        gameContainer.setStyle("-fx-padding: 300;");
        HBox gameBoard = new HBox();
        gameBoard.setPrefWidth(600);
        gameBoard.setPrefHeight(400);
        gameBoard.setStyle("-fx-background-color: #fee3c5;-fx-border-color: #000000;-fx-border-width: 2px;");
        gameContainer.getChildren().add(gameBoard);

        // Add the ImageView to the children of the AnchorPane
        anchorPane.getChildren().addAll(imageView, gameContainer);


        // Set up the stage and scene
        stage.setScene(new Scene(root));
//        stage.setMaximized(true);
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