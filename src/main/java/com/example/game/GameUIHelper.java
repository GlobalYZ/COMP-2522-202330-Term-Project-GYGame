package com.example.game;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.beans.property.StringProperty;

/**
 * A Game UI Helper class that controls the GUI.
 *
 * @author Muyang Li
 * @version 2023
 */
public final class GameUIHelper {
    /**
     * The background color of the game.
     */
    public static final String BACKGROUND_COLOR = "-fx-background-color: #fee3c5;";

    /**
     * The style of the box.
     */
    public static final String BOX_STYLE = "-fx-background-color: #fee3c5;"
            + "-fx-border-color: #000000;-fx-border-width: 2px;"
            + "-fx-border-radius: 5px;";

    private static final Label CURRENT_SCORE_LABEL = new Label();
    private static final Label HISTORY_SCORE_LABEL = new Label();

    private static StringProperty scoreProperty;

    private static StringProperty hisScoreProperty;

    private static StringProperty levelProperty;

    private GameUIHelper() { }

    /**
     * Generate the preview box for the game.
     *
     * @param path The path of the image
     * @param size The size of the image
     * @return The node of the preview box
     */
    public static Node generatePreviewElement(final String path, final int size) {
        final int padding = 10;
        Image image = new Image(path);
        ImageView imageView = new ImageView(image);
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(size + padding, size + padding);
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
        final int padding = 10;
        int tileSize = GameManager.TILE_SIZE;
        int gridWidth = GameManager.GRID_WIDTH;
        VBox scoreBox = new VBox();
        scoreBox.setPrefWidth(tileSize * gridWidth);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(padding, 0, padding * 2, 0));
        scoreProperty = new SimpleStringProperty("SCORE: " + scoreNum + "   ");
        hisScoreProperty = new SimpleStringProperty("ACHIEVED :" + scoreAchieved);
        CURRENT_SCORE_LABEL.textProperty().bind(scoreProperty);
        HISTORY_SCORE_LABEL.textProperty().bind(hisScoreProperty);
        scoreBox.setStyle("-fx-color: #a88d53; -fx-font-size: 28px;-fx-text-alignment: center;");
        scoreBox.getChildren().addAll(CURRENT_SCORE_LABEL, HISTORY_SCORE_LABEL);
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
        final int padding = 10;
        final int previewSize = 3;
        int tileSize = GameManager.TILE_SIZE;
        VBox previewBox = new VBox();

        previewBox.setPrefWidth(tileSize + padding);
        previewBox.setStyle(BOX_STYLE);

        // Set the minimum height to ensure it's respected
        previewBox.setMaxHeight(previewSize * (tileSize + padding));
        return previewBox;
    }

    /**
     * Generate the lv box for the game.
     *
     * @param level The current level
     * @return The node of the lv box
     */
    public static Node createLvBox(final Integer level) {
        final int padding = 10;
        int tileSize = GameManager.TILE_SIZE;
        FlowPane lvBox = new FlowPane();
        levelProperty = new SimpleStringProperty("LV" + level.toString());
        Label lvLabel = new Label();
        lvLabel.textProperty().bind(levelProperty);
        lvLabel.setStyle("-fx-font-size: 20px;-fx-text-alignment: center;-fx-color: #a88d53;");
        lvBox.setAlignment(Pos.CENTER);
        lvBox.setMaxWidth(tileSize + padding);
        lvBox.setMinHeight(tileSize + padding);
        lvBox.setStyle(BOX_STYLE);
        lvBox.getChildren().add(lvLabel);
        return lvBox;
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
