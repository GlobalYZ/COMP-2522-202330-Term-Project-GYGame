package com.example.game;
import javafx.fxml.FXML;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.List;

public final class GameUIHelper {

    public static final String backgroundColor = "-fx-background-color: #fee3c5;";


    public static final String boxStyle = "-fx-background-color: #fee3c5;" +
            "-fx-border-color: #000000;-fx-border-width: 2px;" +
            "-fx-border-radius: 5px;";

    public static final String activeButtonStyle = "-fx-background-color: #2ed573; -fx-text-fill: #ffffff;" +
            "-fx-border-color: black; -fx-border-radius: 5px; -fx-background-radius:8px; -fx-font-size: 20px;" +
            "-fx-border-width:3px; -fx-shadow-color: #000000; -fx-shadow-radius: 5px; -fx-shadow-inset: 0px;" +
            "-fx-cursor: hand;";

    public static final String inactiveButtonStyle = "-fx-background-color: #dfe6e9; -fx-text-fill: #929292;" +
            "-fx-border-color: #929292; -fx-border-radius: 5px; -fx-background-radius:8px;" +
            "-fx-font-size: 20px; -fx-border-width:3px;";


    public static Node generatePreviewElement(String path, int size) {
        Image image = new Image(path);
        ImageView imageView = new ImageView(image);
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(size +10, size +10);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        stackPane.getChildren().add(imageView);

        // Center the image both horizontally and vertically
        StackPane.setAlignment(imageView, Pos.CENTER);
        return stackPane;
    }

    public static Node createScoreBoard(Integer scoreNum, Integer scoreAchieved) {
        int tileSize = GameManager.TILE_SIZE;
        int gridWidth = GameManager.GRID_WIDTH;
        HBox scoreBox = new HBox();
        scoreBox.setPrefWidth(tileSize * gridWidth);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(10, 0, 20, 0));
        Text scoreText = new Text();
        Text achievedScore = new Text();
        scoreText.setText("SCORE: " + scoreNum.toString() + "   ");
        achievedScore.setText("ACHIEVED :" + scoreAchieved.toString());
        scoreBox.setStyle("-fx-color: #a88d53; -fx-font-size: 28px;-fx-text-alignment: center;");
        scoreBox.getChildren().addAll(scoreText, achievedScore);
        return scoreBox;
    }

    public static Node createPreviewBox() {
        int tileSize = GameManager.TILE_SIZE;
        VBox previewBox = new VBox();

        previewBox.setPrefWidth(tileSize + 10);
        previewBox.setStyle(boxStyle);
        List<Node> elements = new ArrayList<>();
        for (int i=0;i<3;i++) {
            elements.add(generatePreviewElement("file:./src/asset/Image/Paper.png", tileSize));
        }
        previewBox.getChildren().addAll(elements);

        // Set the minimum height to ensure it's respected
        previewBox.setMaxHeight(3 * (tileSize + 10));
        return previewBox;
    }

    public static Node createLvBox(Integer level) {
        int tileSize = GameManager.TILE_SIZE;
        FlowPane LvBox = new FlowPane();
        Text LvText = new Text();
        LvText.setText("LV" + level.toString());
        LvText.setStyle("-fx-font-size: 20px;-fx-text-alignment: center;-fx-color: #a88d53;");
        LvBox.setAlignment(Pos.CENTER);
        LvBox.setMaxWidth(tileSize+10);
        LvBox.setMinHeight(tileSize+10);
        LvBox.setStyle(boxStyle);
        LvBox.getChildren().add(LvText);
        return LvBox;
    }

    public static Button createGeneralButton(String context){
        Button btn = new Button();
        btn.setText(context);
        btn.setStyle(activeButtonStyle);
        btn.setMinHeight(50);
        btn.setMinWidth(140);
        return btn;
    }
}