package com.example.game;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;
import javafx.beans.property.StringProperty;

/**
 * A Game UI Helper class that controls the GUI.
 *
 * @author Muyang Li
 * @version 2023
 */
public final class GameUIHelper {

    private GameUIHelper() { }
    private static Label currentScoreLabel = new Label();

    private static Label historyScoreLabel = new Label();

    private static StringProperty scoreProperty;

    private static StringProperty hisScoreProperty;

    private static StringProperty levelProperty;

    /**
     * The background color of the game.
     */
    public static final String backgroundColor = "-fx-background-color: #fee3c5;";

    /**
     * The style of the box.
     */
    public static final String boxStyle = "-fx-background-color: #fee3c5;"
            + "-fx-border-color: #000000;-fx-border-width: 2px;"
            + "-fx-border-radius: 5px;";

    private static final String activeButtonStyle = "-fx-background-color: #2ed573; -fx-text-fill: #ffffff;"
            + "-fx-border-color: black; -fx-border-radius: 5px; -fx-background-radius:8px; -fx-font-size: 20px;"
            + "-fx-border-width:3px; -fx-shadow-color: #000000; -fx-shadow-radius: 5px; -fx-shadow-inset: 0px;"
            + "-fx-cursor: hand;";

    private static final String inactiveButtonStyle = "-fx-background-color: #dfe6e9; -fx-text-fill: #929292;"
            + "-fx-border-color: #929292; -fx-border-radius: 5px; -fx-background-radius:8px;"
            + "-fx-font-size: 20px; -fx-border-width:3px;";

    /**
     * Generate the preview box for the game.
     *
     * @param path The path of the image
     * @param size The size of the image
     * @return The node of the preview box
     */
    public static Node generatePreviewElement(final String path, final int size) {
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
    /**
     * Generate the score box for the game.
     *
     * @param scoreNum The current score
     * @param scoreAchieved The highest score
     * @return The node of the score box
     */
    public static Node createScoreBoard(final Integer scoreNum, final Integer scoreAchieved) {
        int tileSize = GameManager.TILE_SIZE;
        int gridWidth = GameManager.GRID_WIDTH;
        VBox scoreBox = new VBox();
        scoreBox.setPrefWidth(tileSize * gridWidth);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(10, 0, 20, 0));
        scoreProperty = new SimpleStringProperty("SCORE: " + scoreNum + "   ");
        hisScoreProperty = new SimpleStringProperty("ACHIEVED :" + scoreAchieved);
        currentScoreLabel.textProperty().bind(scoreProperty);
        historyScoreLabel.textProperty().bind(hisScoreProperty);
        scoreBox.setStyle("-fx-color: #a88d53; -fx-font-size: 28px;-fx-text-alignment: center;");
        scoreBox.getChildren().addAll(currentScoreLabel, historyScoreLabel);
        return scoreBox;
    }
    /**
     * Update the current score.
     *
     * @param scoreNum The current score
     */
    public static void updateCurrentScore(final Integer scoreNum) {
        scoreProperty.set("SCORE: " + scoreNum + "\n");
    }
    /**
     * Update the highest score.
     *
     * @param scoreAchieved The highest score
     */
    public static void updateHistoryScore(final Integer scoreAchieved) {

        hisScoreProperty.set("ACHIEVED :" + scoreAchieved);

    }
    /**
     * Generate the dynamic preview boxes for the game.
     *
     * @return The node of the preview box
     */
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

    /**
     * Generate the lv box for the game.
     *
     * @return The node of the lv box
     */
    public static Node createLvBox(final Integer level) {
        int tileSize = GameManager.TILE_SIZE;
        FlowPane LvBox = new FlowPane();
        levelProperty = new SimpleStringProperty("LV" + level.toString());
        Label LvLabel = new Label();
        LvLabel.textProperty().bind(levelProperty);
        LvLabel.setStyle("-fx-font-size: 20px;-fx-text-alignment: center;-fx-color: #a88d53;");
        LvBox.setAlignment(Pos.CENTER);
        LvBox.setMaxWidth(tileSize+10);
        LvBox.setMinHeight(tileSize+10);
        LvBox.setStyle(boxStyle);
        LvBox.getChildren().add(LvLabel);
        return LvBox;
    }

    /**
     * Update the lv box for the game.
     *
     * @param lv The current level
     */
    public static void updateLv(final Integer lv) {
        levelProperty.set("LV" + lv);
    }
}
