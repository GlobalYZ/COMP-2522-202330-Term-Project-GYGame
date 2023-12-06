
package com.example.game;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * A Game Manager class that controls the game logic.
 *
 * @author Muyang Li
 * @version 2023
 */
public class GameManager extends Application implements PuzzleGame {


    private GraphicsContext gc;

    private double time;

    private AnchorPane root;

    private Integer scoreNum = 0;
    /**
     * The style of the box.
     */
    private Integer scoreAchieved = 0;

    private Integer comboCount = 0;

    private Integer level = 1;

    private AnimationTimer timer;

    private double loadTime;

    private MediaPlayer interactMediaPlayer;

    private MediaPlayer deletionMediaPlayer;

    private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];

    private final List<Mino> original = new ArrayList<>();  // initial minos collection

    private List<Mino> minos = new ArrayList<>();  // minos on the board

    private Mino selected; // the mino that is going to be moved

    private Mino minoInQueue; // the mino that is going to be selected on the board

    private Mino minoPreview; // the mino that is going to be placed on the board

    /**
     * Generate the list of basic minos.
     */
    public void generateBasicMinos() {
        //0
        original.add(new Mino(new Piece(0, Direction.DOWN)));

        //0
        //1
        original.add(new Mino(
                new Piece(0, Direction.DOWN),
                new Piece(1, Direction.DOWN)));
        //0 1
        //1
        original.add(new Mino(
                new Piece(0, Direction.DOWN),
                new Piece(1, Direction.RIGHT),
                new Piece(1, Direction.DOWN)));
        //0
        //1
        //2
        original.add(new Mino(
                new Piece(0, Direction.DOWN),
                new Piece(1, Direction.DOWN),
                new Piece(2, Direction.DOWN)));

        //1 0 1
        //  1
        original.add(new Mino(
                new Piece(0, Direction.DOWN),
                new Piece(1, Direction.LEFT),
                new Piece(1, Direction.RIGHT),
                new Piece(1, Direction.DOWN)));
        // other two Minos needs a list of Direction for the position
        // Will adjust the code after testing the basic minos
    }

    /**
     * Start new game click event.
     *
     * @param event the event
     */
    @FXML
    public void startNewGame(final ActionEvent event) {
        try {
            launchPlayBoard((Stage) ((Node) event.getSource()).getScene().getWindow());
            loadHistoryRecord();
            spawn();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException appropriately
        }
    }

    /**
     * Load game click event.
     *
     * @param event the event
     */
    @FXML
    public void loadOldGame(final ActionEvent event) {
        try {
            try (ExecutorService executor = Executors.newSingleThreadExecutor()) {

                executor.submit(this::loadGame);
            }
            launchPlayBoard((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException appropriately
        }
    }
    /**
     * set and start render timer and load timer for the game.
     */
    private void setTimers() {
        timer = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                time += 0.03;
                if (time >= 0.9) {
                    if (selected.getPieces() != null) {
                        update();
                        render();
                    }
                    time = 0;
                }
            }
        };
        AnimationTimer loadTimer = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                loadTime += 0.03;
                if (loadTime >= 5) {
                    saveGame();
                    loadTime = 0;
                }
            }

        };
        loadTimer.start();
        timer.start();
    }
    /**
     * Return the game board node.
     *
     * @return the basic game node
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public Parent setContent() {
        Pane root = new Pane();
        root.setPrefSize(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        Canvas canvas = new Canvas(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        generateBasicMinos(); // generate the basic minos collection
        minoInQueue = original.get(new Random().nextInt(original.size())).copy();  // generate the first mino for spawn
        minoInQueue.move(GRID_WIDTH / 2, 0);
        minoPreview = original.get(new Random().nextInt(original.size())).copy(); // generate the next mino for preview
        setTimers();
        return root;
    }
    /**
     * Check if the tag ID is the given ID.
     *
     * @param id the id
     * @return true if the tag ID is the given ID
     */
    private boolean isTagID(final int id) {
        for (Piece.RecycleType t : Piece.RecycleType.values()) {
            if (t.tagID() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the state is valid.
     *
     * @return true if the state is valid
     */
    public boolean isValidateState() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] > 1 && !isTagID(grid[x][y])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * update the game board.
     */
    private void update() {
        makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
    }

    /**
     * Check if the move is valid.
     *
     * @param onSuccess the on success Direction
     * @param onFail the on fail Direction
     * @param endMove if end move
     */
    public void makeMove(final Consumer<Mino> onSuccess, final Consumer<Mino> onFail, final boolean endMove) {
        selected.getPieces().forEach(this::removePiece);
        onSuccess.accept(selected);  // move down 1 unit
        boolean offBoard = selected.getPieces().stream().anyMatch(this::isOffBoard);
        if (!offBoard) {
            selected.getPieces().forEach(this::placePiece);
        } else {
            onFail.accept(selected);  // move back to the last position
            selected.getPieces().forEach(this::placePiece);
            if (endMove) {
                selected.getPieces().forEach(this::placeTagID);
                checkAndRemove();
            }
            return;
        }
        if (isValidateState()) {
            selected.getPieces().forEach(this::removePiece);
            onFail.accept(selected);
            selected.getPieces().forEach(this::placePiece);
            if (endMove) {
                selected.getPieces().forEach(this::placeTagID);
                checkAndRemove();
            }
        }
    }

    private void dropPiece(final int targetX, final int targetY, final int shift) {
        for (Mino mino : minos) {
            mino.getPieces().stream()
                    .filter(p -> p.getX() == targetX && p.getY() == targetY)
                    .forEach(p -> {
                        clearPiece(p);
                        p.setY(p.getY() + shift);
                        placeTagID(p);
                    });
        }
    }
    private void gravity(final int targetX, final int targetY, final boolean onLeft) {
        int drop = 0;
        if (onLeft) {
            if (targetX >= 0 && targetY < GRID_HEIGHT - 1 && grid[targetX][targetY] != 0) {
                while (targetY + drop < GRID_HEIGHT - 1 && grid[targetX][targetY + drop + 1] == 0) {
                    drop++;
                }
                dropPiece(targetX, targetY, drop);
            }
        } else {
            if (targetX < GRID_WIDTH && targetY < GRID_HEIGHT - 1 && grid[targetX][targetY] != 0) {
                while (targetY + drop < GRID_HEIGHT - 1 && grid[targetX][targetY + drop + 1] == 0) {
                    drop++;
                }
                dropPiece(targetX, targetY, drop);
            }
        }
    }
    private boolean hasMatch(final boolean[][] toRemove) {
        // deletion sound
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
                if (toRemove[x][y]) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkAndRemove() {
        boolean[][] toRemove = checkMatches();
        boolean hasMatch = hasMatch(toRemove);
        int counter = 0;
        while (hasMatch) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                int match = 0;
                for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
                    if (toRemove[x][y]) {

                        System.out.println("Removing: " + grid[x][y] + " at (" + x + ", " + y + ") ");
                        for (Mino mino : minos) {
                            mino.detach(x, y);
                        }
                        grid[x][y] = 0;

                        Platform.runLater(() -> {
                            deletionMediaPlayer.stop();
                            deletionMediaPlayer.play();
                            calculateScore();
                        });
                        match++;
                    } else if (match > 0) {
                        int shift = match;
                        // another gravity, check if there are empty spots below the removed pieces
                        while (y + shift < GRID_HEIGHT - 1 && grid[x][y + shift + 1] == 0) {
                            shift++;
                        }
                        dropPiece(x, y, shift);
                        // Gravity for the pieces on the left and right
                        for (int i = -2; i <= 2; i++) {
                            if (i == 0) {
                                continue; // Skip i = 0, which is the current column
                            }
                            int newX = x + i;
                            if (i < 0 || (newX < GRID_WIDTH && !toRemove[newX][y + match])) {
                                gravity(newX, y + match, i < 0);
                            }
                        }
                    }
                }
            }
            toRemove = checkMatches();
            hasMatch = hasMatch(toRemove);
            if (hasMatch) {
                counter++;
                System.out.println("Combo!");
            }
        }
        if (counter > 0) {
            int base = 600;
            System.out.println("counter count: " + counter);
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            for (int z = 0; z < counter; z++) {
                int finalZ = z + 1;
                long delay = (long) base * finalZ;
                executor.schedule(() -> {
                    deletionMediaPlayer.stop();
                    deletionMediaPlayer.play();
                }, delay, TimeUnit.MILLISECONDS);
            }
            executor.shutdown();
        }

        minoInQueue = minoPreview;  // overwrite the going-to-be-selected mino by the previous preview mino
        minoInQueue.move(GRID_WIDTH / 2, 0);
        minoPreview = original.get(new Random().nextInt(original.size())).copy();
        spawn();
    }

    private void boosterMarkRemove(final boolean[][] toRemove, final int tagID) {
        System.out.println("Booster deleting tagID: " + tagID);

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
                if (grid[x][y] == tagID) {
                    toRemove[x][y] = true;
                }
            }
        }
    }


    private boolean[][] checkMatches() {  // return a boolean matrix to represent the matches to remove
        boolean[][] toRemove = new boolean[GRID_WIDTH][GRID_HEIGHT];
        int tagID;

        // Check for vertical matches
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = GRID_HEIGHT - 1; y - 2 >= 0; y--) {
                if ((grid[x][y] == BOOSTER_ID && grid[x][y - 1] != 0 && grid[x][y - 1] == grid[x][y - 2])
                || (grid[x][y] != 0 && grid[x][y] == grid[x][y - 1] && grid[x][y - 2] == BOOSTER_ID)
                || (grid[x][y] != 0 && grid[x][y] == grid[x][y - 2] && grid[x][y - 1] == BOOSTER_ID)
                || (grid[x][y] != 0 && grid[x][y] == grid[x][y - 1] && grid[x][y] == grid[x][y - 2])) {
                    toRemove[x][y] = true;
                    toRemove[x][y - 1] = true;
                    toRemove[x][y - 2] = true;
                    if (grid[x][y] == BOOSTER_ID || grid[x][y - 1] == BOOSTER_ID || grid[x][y - 2] == BOOSTER_ID) {
                        tagID = grid[x][y] == BOOSTER_ID ? grid[x][y - 1] : grid[x][y];
                        boosterMarkRemove(toRemove, tagID);
                    }
                }
            }
        }

        // Check for horizontal matches
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH - 2; x++) {
                if ((grid[x][y] == BOOSTER_ID && grid[x + 1][y] != 0 && grid[x + 1][y] == grid[x + 2][y])
                || (grid[x][y] != 0 && grid[x][y] == grid[x + 1][y] && grid[x + 2][y] == BOOSTER_ID)
                || (grid[x][y] != 0 && grid[x][y] == grid[x + 2][y] && grid[x + 1][y] == BOOSTER_ID)
                || (grid[x][y] != 0 && grid[x][y] == grid[x + 1][y] && grid[x][y] == grid[x + 2][y])) {
                    toRemove[x][y] = true;
                    toRemove[x + 1][y] = true;
                    toRemove[x + 2][y] = true;
                    if (grid[x][y] == BOOSTER_ID || grid[x + 1][y] == BOOSTER_ID || grid[x + 2][y] == BOOSTER_ID) {
                        tagID = grid[x][y] == BOOSTER_ID ? grid[x + 1][y] : grid[x][y];
                        boosterMarkRemove(toRemove, tagID);
                    }
                }
            }
        }

        return toRemove;
    }
    /**
     * render the canvas game board.
     */
    public void render() {
        gc.clearRect(0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        minos.forEach(mino -> mino.draw(gc));
    }

    /**
     * Level up if user reach the designed score.
     */
    @Override
    public void levelUpIfNeed() {
        final int base = 100;
        if (scoreNum >= base * level) {
            level++;
            GameUIHelper.updateLv(level);
            scoreNum = 0;
        }
    }

    /**
     * calculate the score.
     */
    @Override
    public void calculateScore() {
        final int scoreBase = 10;
        if (comboCount > 0) {
            scoreNum += scoreBase * comboCount;
        } else {
            scoreNum += scoreBase;
        }
        levelUpIfNeed();
        GameUIHelper.updateCurrentScore(scoreNum);
        if (scoreNum > scoreAchieved) {
            scoreAchieved = scoreNum;
            GameUIHelper.updateHistoryScore(scoreAchieved);
        }
    }

    private void renderPreviews() {
        Platform.runLater(() -> {
            Node targetNode = root.lookup("#preview");
            List<Node> elements = new ArrayList<>();
            if (targetNode == null) {
                throw new NullPointerException("cannot find child node fx:id for argument: preview");
            } else {
                minoPreview.getPieces().forEach(p -> elements.add(
                        GameUIHelper.generatePreviewElement(p.getTag().getImageString(), TILE_SIZE))
                );
                ((VBox) targetNode).getChildren().clear();
                ((VBox) targetNode).getChildren().addAll(elements);
            }
        });
    }

    /**
     * Spawn the new mino on the board.
     */
    public void spawn() {
        selected = minoInQueue;
        minos.add(minoInQueue);

        renderPreviews();

        for (Piece piece : minoInQueue.getPieces()) {
            placePiece(piece);
        }
        if (isValidateState()) {
            launchPopUp("Your score is " + scoreNum + ". Do you want to restart or leave?");
        }
    }
    /**
     * Place tag ID to grid.
     *
     * @param piece the piece
     */
    public void placeTagID(final Piece piece) {
        grid[piece.getX()][piece.getY()] = piece.getTag().getID();
    }

    /**
     * Place piece to grid.
     *
     * @param piece the piece
     */
    public void placePiece(final Piece piece) {
        grid[piece.getX()][piece.getY()]++;
    }

    /**
     * Remove piece from grid.
     *
     * @param piece the piece
     */
    public void removePiece(final Piece piece) {
        grid[piece.getX()][piece.getY()]--;
    }
    /**
     * Clear piece from grid.
     *
     * @param piece the piece
     */
    public void clearPiece(final Piece piece) {
        grid[piece.getX()][piece.getY()] = 0;
    }

    /**
     * Check if the piece is off the board.
     *
     * @param piece the piece
     * @return true if the piece is off the board
     */
    public boolean isOffBoard(final Piece piece) {
        return piece.getX() < 0 || piece.getX() >= GRID_WIDTH || piece.getY() < 0 || piece.getY() >= GRID_HEIGHT;
    }

    /**
     * Pause the game.
     */
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
    /**
     * Launch pop up window.
     *
     * @param content the message to display
     */
    private void launchPopUp(final String content) {
        stopTimer();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Game Over");
        dialog.setContentText(content);

        ButtonType cancelButtonType = new ButtonType("RESTART", ButtonBar.ButtonData.FINISH);
        ButtonType leaveButtonType = new ButtonType("LEAVE", ButtonBar.ButtonData.FINISH);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, leaveButtonType);

        dialog.getDialogPane().getChildren().forEach(node -> node.setStyle("-fx-text-alignment: center;-fx-font-size: 20px;" + GameUIHelper.backgroundColor));

        // Add a CSS stylesheet to the dialog pane
        dialog.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("overWrite.css")).toExternalForm()
        );

        dialog.showAndWait().ifPresent(response -> {
            if (Objects.equals(response.getText(), "LEAVE")) {
                //if game over, clear load.txt.
                File file = new File("src/load.txt");
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.exit(0);
            } else if (Objects.equals(response.getText(), "RESTART")) {
                resetGame();
            }
        });
        timer.start();
    }

    /**
     * Launch the play board.
     *
     * @param stage the stage to launch play board
     * @throws IOException the io exception
     */
    public void launchPlayBoard(final Stage stage) throws IOException {

        // Create the root node (for example, an AnchorPane)
        root = new AnchorPane();

        HBox gameContainer = new HBox();

        // get user screen size
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        double gamePadding = (screenHeight - 750) / 2;

        // Create an ImageView and set an image
        Image image = new Image("file:./src/asset/Image/background.jpg");
        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(root.widthProperty());
        imageView.fitHeightProperty().bind(root.heightProperty());

        gameContainer.setPrefSize(screenWidth, screenHeight);
        gameContainer.setAlignment(Pos.CENTER);

        //create game board boxes
        FlowPane gameBoard = new FlowPane();
        gameBoard.setPrefWidth(600);
        gameBoard.setPadding(new Insets(gamePadding, 0, gamePadding, 0));

        //create score box
        Node scoreBox = GameUIHelper.createScoreBoard(scoreNum, scoreAchieved);

        //create preview box and previewElements
        Node previewBox = GameUIHelper.createPreviewBox();
        previewBox.setId("preview");

        //create LvBox
        Node lvBox = GameUIHelper.createLvBox(level);

        VBox rightWrapper = new VBox();
        rightWrapper.getChildren().addAll(previewBox, lvBox);
        VBox.setMargin(previewBox, new Insets(0, 0, 40, 50));
        VBox.setMargin(lvBox, new Insets(10, 0, 10, 50));

        //create playGround
        Parent playGround = setContent();
        playGround.setStyle(GameUIHelper.boxStyle);

        //link to game board
        gameBoard.getChildren().addAll(scoreBox, playGround, rightWrapper);
        FlowPane.setMargin(scoreBox, new Insets(0, 0, 0, 60));
        FlowPane.setMargin(playGround, new Insets(0, 0, 0, 60));

        gameContainer.getChildren().add(gameBoard);
        root.getChildren().addAll(imageView, gameContainer);

        // Set up the stage and scene
        Scene scene = new Scene(root);
        // keyboard event
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
                deletionMediaPlayer.stop();
                interactMediaPlayer.stop();
                interactMediaPlayer.play();
                makeMove(Mino::rotate, Mino::rotateBack, false);
            } else if (e.getCode() == KeyCode.RIGHT) {
                makeMove(p -> p.move(Direction.RIGHT), p -> p.move(Direction.LEFT), false);
            } else if (e.getCode() == KeyCode.LEFT) {
                makeMove(p -> p.move(Direction.LEFT), p -> p.move(Direction.RIGHT), false);
            } else if (e.getCode() == KeyCode.DOWN) {
                makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
            } else if (e.getCode() == KeyCode.ESCAPE) {
                stopTimer();
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Pause");
                dialog.setContentText("Do you want to restart the game or save & leave?");

                ButtonType okButtonType = new ButtonType("RESUME", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelButtonType = new ButtonType("RESTART", ButtonBar.ButtonData.FINISH);
                ButtonType leaveButtonType = new ButtonType("LEAVE", ButtonBar.ButtonData.CANCEL_CLOSE);


                // ADD BUTTONS
                dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType, leaveButtonType);
                dialog.getDialogPane().getChildren().forEach(node -> node.setStyle(
                        "-fx-text-alignment: center;-fx-font-size: 20px;" + GameUIHelper.backgroundColor)
                );

                // Add a CSS stylesheet to the dialog pane
                dialog.getDialogPane().getStylesheets().add(
                        Objects.requireNonNull(getClass().getResource("overWrite.css")).toExternalForm()
                );


                // show dialog and wait for response
                dialog.showAndWait().ifPresent(response -> {
                    if (Objects.equals(response.getText(), "RESUME")) {
                        System.out.println("User clicked OK");
                    } else if (Objects.equals(response.getText(), "LEAVE")) {
                        stage.close();
                    } else if (Objects.equals(response.getText(), "RESTART")) {
                        resetGame();
                    }
                    timer.start();
                });

            }
            render();
        });
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("EcoStack");
        stage.show();
        String interactFile = "src/asset/sound/UI_interact.mp3"; // 替换为你的音乐文件路径
        Media interactMedia = new Media(new File(interactFile).toURI().toString());
        interactMediaPlayer = new MediaPlayer(interactMedia);
        String deletionFile = "src/asset/sound/deletion.mp3"; // 替换为你的音乐文件路径
        Media deletionMedia = new Media(new File(deletionFile).toURI().toString());
        deletionMediaPlayer = new MediaPlayer(deletionMedia);
    }

    /**
     * Reset the game.
     */
    @Override
    public void resetGame() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                grid[x][y] = 0;
            }
        }
        scoreNum = 0;
        GameUIHelper.updateCurrentScore(scoreNum);
        gc.clearRect(0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        minos.clear();
        minoPreview = original.get(new Random().nextInt(original.size())).copy();
        minoInQueue = minoPreview;  // overwrite the going-to-be-selected mino by the previous preview mino
        minoInQueue.move(GRID_WIDTH / 2, 0);
        minoPreview = original.get(new Random().nextInt(original.size())).copy();
        spawn();
    }

    /**
     * Save the game.
     */
    @Override
    public void saveGame() {
        File file = new File("src/load.txt");

        if (!file.exists()) {
            try {
                // create the file when it does not exist
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JsonFormatter jsonFormatter = new JsonFormatter();
        jsonFormatter.setMinoPreview(this.minoPreview);
        jsonFormatter.setMinoInQueue(this.minoInQueue);
        jsonFormatter.setScoreNum(this.scoreNum);
        jsonFormatter.setScoreAchieved(this.scoreAchieved);
        jsonFormatter.setComboCount(this.comboCount);
        jsonFormatter.setLevel(this.level);
        jsonFormatter.setGrid(this.grid);
        jsonFormatter.setSelected(this.selected);
        jsonFormatter.setMinos(this.minos);

        try (FileOutputStream fileOut = new FileOutputStream("src/load.txt");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
             out.writeObject(jsonFormatter);
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
    /**
     * Load the history record based on data at load.txt.
     */
    private void loadHistoryRecord() {

        File file = new File("src/load.txt");
        if (file.length() == 0) {
            scoreAchieved = 0;
        } else {
            try (FileInputStream fileIn = new FileInputStream("src/load.txt");
                 ObjectInputStream in = new ObjectInputStream(fileIn)) {
                JsonFormatter gameMapper = (JsonFormatter) in.readObject();
                scoreAchieved = gameMapper.getScoreAchieved();
                renderPreviews();
                GameUIHelper.updateHistoryScore(scoreAchieved);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * Load the game based on data at load.txt.
     */
    public void loadGame() {

        try (FileInputStream fileIn = new FileInputStream("src/load.txt");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            JsonFormatter gameMapper = (JsonFormatter) in.readObject();
            minoPreview = gameMapper.getMinoPreview();
            minoInQueue = gameMapper.getMinoInQueue();
            scoreNum = gameMapper.getScoreNum();
            scoreAchieved = gameMapper.getScoreAchieved();
            comboCount = gameMapper.getComboCount();
            level = gameMapper.getLevel();
            minos = gameMapper.getMinos();
            selected = gameMapper.getSelected();
            grid = gameMapper.getGrid();
            renderPreviews();
            GameUIHelper.updateCurrentScore(scoreNum);
            GameUIHelper.updateHistoryScore(scoreAchieved);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * Override the start method in Application.
     */
    @Override
    public void start(final Stage stage) throws IOException {

        Parent welcomeRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        Scene welcomeScene = new Scene(welcomeRoot, 400, 275);
        welcomeScene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("overWrite.css")).toExternalForm()
        );
        stage.setScene(welcomeScene);
        stage.setTitle("EcoStack");
        stage.show();
        Platform.runLater(() -> {
            Button loadBtn = (Button) welcomeRoot.lookup("#loadGameButton");
            File file = new File("src/load.txt");
            if (file.length() == 0) {
                loadBtn.setDisable(true);
            }
        });
    }

    /**
     * Drive the program.
     *
     * @param args the input arguments
     */
    public static void main(final String[] args) {
        String musicFile = "src/asset/sound/bgm.wav"; // 相对路径，假设音频文件与 Java 源代码在同一目录下
        try {
            File audioFile = new File(musicFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop to play BGM
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
        launch();
    }
}
