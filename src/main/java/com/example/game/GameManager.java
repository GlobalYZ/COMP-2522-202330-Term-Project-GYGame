package com.example.game;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.geometry.Pos;


import java.io.IOException;




public class GameManager extends Application {
    public static final int BOOSTER_ID = 18;
    public static final int TILE_SIZE = 40; // Grace's constant don't delete it.
    public static final int GRID_WIDTH = 10;
    public static final int GRID_HEIGHT = 16;
    public GraphicsContext gc;
    private double time;

    private AnchorPane root;

    private Integer scoreNum = 0;

    private Integer scoreAchieved = 0;

    private Integer comboCount = 0;

    private Integer level = 1;

    private AnimationTimer timer;
    private AnimationTimer loadtimer;

    private double loadtime;



    private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];


    private final List<Mino> original = new ArrayList<>();  // initial minos collection
    private List<Mino> minos = new ArrayList<>();  // minos on the board
    private Mino selected; // the mino that is going to be moved
    private Mino minoInQueue; // the mino that is going to be selected on the board
    private Mino minoPreview; // the mino that is going to be placed on the board



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


    @FXML
    public void startNewGame(ActionEvent event) {
        try {
            lunchPlayBoard((Stage) ((Node) event.getSource()).getScene().getWindow());
            spawn();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException appropriately
        }
    }

    @FXML
    public void loadOldGame(ActionEvent event) {
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();

            executor.submit(() -> {
                loadGame();
            });

            executor.shutdown(); // 记得在不需要时关闭 ExecutorService
            lunchPlayBoard((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException appropriately
        }
    }

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

        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                time += 0.03;
                if(time >= 0.9) {
                    if(selected.getPieces() != null){
                        update();
                        render();
                    }
                    time = 0;
                }
            }
        };

        loadtimer = new AnimationTimer(){
            @Override
            public void handle(long now) {
                loadtime += 0.03;
                if(loadtime >= 5) {
                    saveGame();
                    loadtime = 0;
                }
            }

        };
        loadtimer.start();
        timer.start();
        return root;
    }

    private boolean isTagID(final int id) {
        for (Piece.RecycleType t : Piece.RecycleType.values()) {
            if (t.tagID() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidateState() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] > 1 && !isTagID(grid[x][y])) {
                    return false;
                }
            }
        }
        return true;
    }

    private void update() {
        makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
    }

    public void makeMove(Consumer<Mino> onSuccess, Consumer<Mino> onFail, boolean endMove) {
        selected.getPieces().forEach(p -> removePiece(p));
        onSuccess.accept(selected);  // move down 1 unit
        boolean offBoard = selected.getPieces().stream().anyMatch(this::isOffBoard);
        if (!offBoard) {
            selected.getPieces().forEach(p -> placePiece(p));
        } else {
            onFail.accept(selected);  // move back to the last position
            selected.getPieces().forEach(p -> placePiece(p));
            if (endMove) {
                selected.getPieces().forEach(p -> placeTagID(p));
                checkAndRemove();
            }
            return;
        }
        if (!isValidateState()) {
            selected.getPieces().forEach(p -> removePiece(p));
            onFail.accept(selected);
            selected.getPieces().forEach(p -> placePiece(p));
            if (endMove) {
                selected.getPieces().forEach(p -> placeTagID(p));
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
                                continue; // Skip i = 0 since it's not in the original code
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
                System.out.println("Combo!");
            }
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
                || (grid[x][y] != 0 && grid[x][y] == grid[x][y - 1] && grid[x][y] == grid[x][y - 2])  // normal match
                ) {
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
                || (grid[x][y] != 0 && grid[x][y] == grid[x + 1][y] && grid[x][y] == grid[x + 2][y])  // normal match
                ) {
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
    public void render() {
        gc.clearRect(0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        minos.forEach(mino -> mino.draw(gc));
    }

    public void calculateScore() {
        if (comboCount > 0) {
            scoreNum += 10 * comboCount;
        } else {
            scoreNum += 10;
        }
        GameUIHelper.updateCurrentScore(scoreNum);
        if(scoreNum > scoreAchieved){
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
                minoPreview.getPieces().forEach(p -> {
                    elements.add(GameUIHelper.generatePreviewElement(p.getTag().getImageString(), TILE_SIZE));
                });
                ((VBox)targetNode).getChildren().clear();
                ((VBox)targetNode).getChildren().addAll(elements);
            }
        });
    }


    public void spawn() {
        selected = minoInQueue;
        minos.add(minoInQueue);

        renderPreviews();

        for (Piece piece : minoInQueue.getPieces()) {
            placePiece(piece);
        }
        if (!isValidateState()) {
            System.out.println("Game Over");
        }
    }
    public void placeTagID(final Piece piece) {
        grid[piece.getX()][piece.getY()] = piece.getTag().getID();
//        System.out.println(grid[piece.getX()][piece.getY()]);
    }

    public void placePiece(final Piece piece) {
        grid[piece.getX()][piece.getY()]++;
    }

    public void removePiece(final Piece piece) {
        grid[piece.getX()][piece.getY()]--;
    }
    public void clearPiece(final Piece piece) {
        grid[piece.getX()][piece.getY()] = 0;
    }

    public boolean isOffBoard(final Piece piece) {
        return piece.getX() < 0 || piece.getX() >= GRID_WIDTH || piece.getY() < 0 || piece.getY() >= GRID_HEIGHT;
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void lunchPlayBoard(Stage stage) throws IOException {

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
        Node LvBox = GameUIHelper.createLvBox(level);

        VBox rightWrapper = new VBox();
        rightWrapper.getChildren().addAll(previewBox, LvBox);
        rightWrapper.setMargin(previewBox, new Insets(0, 0, 40, 50));
        rightWrapper.setMargin(LvBox, new Insets(10, 0, 10, 50));

        //create playGround
        Parent playGround = setContent();
        playGround.setStyle(GameUIHelper.boxStyle);

        //link to game board
        gameBoard.getChildren().addAll(scoreBox, playGround, rightWrapper);
        gameBoard.setMargin(scoreBox, new Insets(0, 0, 0, 60));
        gameBoard.setMargin(playGround, new Insets(0, 0, 0, 60));

        gameContainer.getChildren().add(gameBoard);
        root.getChildren().addAll(imageView, gameContainer);

        // Set up the stage and scene
        Scene scene = new Scene(root);
        // keyboard event
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
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
                dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType,leaveButtonType);
                dialog.getDialogPane().getChildren().stream().forEach(node -> {
                    node.setStyle("-fx-text-alignment: center;-fx-font-size: 20px;" + GameUIHelper.backgroundColor);
                });

                // Add a CSS stylesheet to the dialog pane
                dialog.getDialogPane().getStylesheets().add(
                        getClass().getResource("overWrite.css").toExternalForm()
                );


                // show dialog and wait for response
                dialog.showAndWait().ifPresent(response -> {
                    if (response.getText() == "RESUME") {
                        System.out.println("User clicked OK");
                    } else if (response.getText() == "LEAVE") {
                        stage.close();
                    } else if (response.getText() == "RESTART") {
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
    }

    public void resetGame(){
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
        jsonFormatter.minoPreview = this.minoPreview;
        jsonFormatter.minoInQueue = this.minoInQueue;
        jsonFormatter.scoreNum = this.scoreNum;
        jsonFormatter.scoreAchieved = this.scoreAchieved;
        jsonFormatter.comboCount = this.comboCount;
        jsonFormatter.level = this.level;
        jsonFormatter.grid = this.grid;
        jsonFormatter.selected = this.selected;
        jsonFormatter.minos = this.minos;

        try (FileOutputStream fileOut = new FileOutputStream("src/load.txt");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
             out.writeObject(jsonFormatter);
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    public void loadGame() {

        try (FileInputStream fileIn = new FileInputStream("src/load.txt");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            JsonFormatter gameMapper = (JsonFormatter) in.readObject();
            minoPreview = gameMapper.minoPreview;
            minoInQueue = gameMapper.minoInQueue;
            scoreNum = gameMapper.scoreNum;
            scoreAchieved = gameMapper.scoreAchieved;
            comboCount = gameMapper.comboCount;
            level = gameMapper.level;
            minos = gameMapper.minos;
            selected = gameMapper.selected;
            grid = gameMapper.grid;
            renderPreviews();
            GameUIHelper.updateCurrentScore(scoreNum);
            GameUIHelper.updateHistoryScore(scoreAchieved);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage stage) throws IOException {

        Parent welcomeRoot = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        Scene welcomeScene = new Scene(welcomeRoot, 400, 275);
        welcomeScene.getStylesheets().add(
                getClass().getResource("overWrite.css").toExternalForm()
        );
        stage.setScene(welcomeScene);
        stage.setTitle("EcoStack");
        stage.show();
        Platform.runLater(()->{
            Button loadBtn = (Button) welcomeRoot.lookup("#loadGameButton");
            File file = new File("src/load.txt");
            if (file.length() == 0) {
                loadBtn.setDisable(true);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}