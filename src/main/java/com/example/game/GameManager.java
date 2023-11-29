package com.example.game;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import javafx.stage.StageStyle;


public class GameManager extends Application {
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



    private final int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];


    private final List<Mino> original = new ArrayList<>();  // initial minos collection
    private final List<Mino> minos = new ArrayList<>();  // minos on the board
    private Mino selected; // the mino that is going to be moved
    private Mino minoInQueue; // the mino that is going to be selected on the board
    private Mino minoPreview; // the mino that is going to be placed on the board



    private void generateBasicMinos() {
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
    private void startNewGame(ActionEvent event) {
        try {
            lunchPlayBoard((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException appropriately
        }
    }

    private Parent setContent() {
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
                    update();
                    render();
                    time = 0;
                }
            }
        };
        timer.start();
        return root;
    }

    private boolean isTagID(final int id) {
        System.out.println("id is " + id);
        for (Piece.RecycleType t : Piece.RecycleType.values()) {
            if (t.tagID() == id) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidateState() {
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

    private void makeMove(Consumer<Mino> onSuccess, Consumer<Mino> onFail, boolean endMove) {
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

    private void checkAndRemove() {
        // TODO to be continued
        boolean[][] toRemove = checkMatches();

        for (int x = 0; x < GRID_WIDTH; x++) {
            int shift = 0;
            for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
                if (toRemove[x][y]) {
                    for (Mino mino : minos) {
                        int finalX1 = x;
                        int finalY1 = y;
                        mino.getPieces().removeIf(p -> p.getX() == finalX1 && p.getY() == finalY1);
                    }
                    grid[x][y] = 0;
                    shift++;
                } else if (shift > 0) {
                    int finalX = x;
                    int finalY = y;
                    int finalShift = shift;
                    for (Mino mino : minos) {
                        mino.getPieces().stream()
                                .filter(p -> p.getX() == finalX && p.getY() < finalY)
                                .forEach(p -> {
                                    removePiece(p);
                                    p.setY(p.getY() + finalShift);
                                    placeTagID(p);
                                });
                    }
                    // TODO map the grid to the minos
                }
            }
        }

        minoInQueue = minoPreview;  // overwrite the going-to-be-selected mino by the previous preview mino
        minoInQueue.move(GRID_WIDTH / 2, 0);
        minoPreview = original.get(new Random().nextInt(original.size())).copy();
        // overwrite the preview mino by the next previewing mino
        spawn();
    }

    private boolean[][] checkMatches() {  // return a boolean matrix to represent the matches to remove
        boolean[][] toRemove = new boolean[GRID_WIDTH][GRID_HEIGHT];

        // Check for vertical matches
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = GRID_HEIGHT - 1; y - 2 >= 0; y--) {
//                if (grid[x][y] != 0) {
//                    System.out.println("grid (" + x + ", " + y + ") value: " + grid[x][y]);
//                }  // check vertical grids for debug
                if (grid[x][y] != 0 && grid[x][y] == grid[x][y - 1] && grid[x][y] == grid[x][y - 2]) {
                    System.out.println("Vertical Match found at (" + x + ", " + y
                            + "; " + x + ", " + (y - 1)
                            + "; " + x + ", " + (y - 2)
                            + ") with value " + grid[x][y]
                            + " and " + grid[x][y - 1]
                            + " and " + grid[x][y - 2]);
                    toRemove[x][y] = true;
                    toRemove[x][y - 1] = true;
                    toRemove[x][y - 2] = true;
                }
            }
        }

        // Check for horizontal matches
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH - 2; x++) {
                if (grid[x][y] != 0 && grid[x][y] == grid[x + 1][y] && grid[x][y] == grid[x + 2][y]) {
                    System.out.println("Horizontal Match found at (" + x + ", " + y
                            + "; " + (x + 1) + ", " + y
                            + "; " + (x + 2) + ", " + y
                            + ") with value " + grid[x][y]
                            + " and " + grid[x + 1][y]
                            + " and " + grid[x + 2][y]);
                    toRemove[x][y] = true;
                    toRemove[x + 1][y] = true;
                    toRemove[x + 2][y] = true;
                }
            }
        }

        return toRemove;
    }
    private void render() {
        gc.clearRect(0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        minos.forEach(mino -> mino.draw(gc));
    }

    private void calculateScore(Piece piece) {
        if (comboCount > 0) {
            scoreNum += 10 * comboCount;
        } else {
            scoreNum += 10;
        }
    }


    private void spawn() {
        selected = minoInQueue;
        minos.add(minoInQueue);

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

    private boolean isOffBoard(final Piece piece) {
        return piece.getX() < 0 || piece.getX() >= GRID_WIDTH || piece.getY() < 0 || piece.getY() >= GRID_HEIGHT;
    }

    private void stopTimer() {
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
                ButtonType cancelButtonType = new ButtonType("RESTART", ButtonBar.ButtonData.CANCEL_CLOSE);


                // ADD BUTTONS
                dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
                dialog.getDialogPane().getChildren().stream().forEach(node -> {
                    node.setStyle("-fx-text-alignment: center;-fx-font-size: 20px;" + GameUIHelper.backgroundColor);
                });

                // Add a CSS stylesheet to the dialog pane
                dialog.getDialogPane().getStylesheets().add(
                        getClass().getResource("overWrite.css").toExternalForm()
                );

                // add ESC key listener
                Scene dialogScene = dialog.getDialogPane().getScene();


                // show dialog and wait for response
                dialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        System.out.println("User clicked OK");
                    } else if (response == ButtonType.CANCEL) {
                        System.out.println("User clicked Cancel");
                    }
                    timer.start();
                });

                dialogScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        dialog.close(); // 关闭 Dialog 对象，关闭对话框
                        timer.start();
                    } else if (event.getCode() == KeyCode.RIGHT) {
                        // Move focus to the next button to the right
                        ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
                        if (buttonBar != null) {
                            System.out.println("found");
                            buttonBar.getButtons().stream()
                                    .findFirst()
                                    .ifPresent(Node::requestFocus);
                        }
                    } else if (event.getCode() == KeyCode.LEFT) {
                        // Move focus to the previous button to the left
                        ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
                        if (buttonBar != null) {
                            ObservableList<Node> buttons = buttonBar.getButtons();
                            for (int i = buttons.size() - 1; i >= 0; i--) {
                                if (i > 0) {
                                    buttons.get(i - 1).requestFocus();
                                }
                                break;
                            }
                        }
                    }
                });

            }


            render();
        });
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("EcoStack");
        stage.show();
        spawn();
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