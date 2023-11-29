package com.example.game;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.geometry.Pos;



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

        AnimationTimer timer = new AnimationTimer() {
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
        onSuccess.accept(selected);
        boolean offBoard = selected.getPieces().stream().anyMatch(this::isOffBoard);
        if(!offBoard) {
            selected.getPieces().forEach(p -> placePiece(p));
        } else {
            onFail.accept(selected);
            selected.getPieces().forEach(p -> placePiece(p));
            if (endMove) {
                //TODO to assign tagID in the grid
                checkPieces();
            }
            return;
        }
        if(!isValidateState()) {
            selected.getPieces().forEach(p -> removePiece(p));
            onFail.accept(selected);
            selected.getPieces().forEach(p -> placePiece(p));
            if (endMove) {
                //TODO to assign tagID in the grid
                checkPieces();
            }
        }
    }

    private void checkPieces(){

        selected.getPieces().forEach(p -> {
            // TODO to be continued
        });
        minoInQueue = minoPreview;  // overwrite the going-to-be-selected mino by the previous preview mino
        minoInQueue.move(GRID_WIDTH / 2, 0);
        minoPreview = original.get(new Random().nextInt(original.size())).copy();
        // overwrite the preview mino by the next previewing mino
        spawn();
    }

    private List<Integer> sweepRows() {
        List<Integer> rows = new ArrayList<>();
        outer:
        for (int y=0; y < GRID_HEIGHT; y++) {
            for (int x=0; x < GRID_WIDTH; x++) {
                if (grid[x][y] != 1) {  // TODO logic needs to be changed
                    continue outer;
                }
            }
            rows.add(y);
        }
        return rows;
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

    public Node getNode(Node root, String id) {
        final Node node = root.lookup(id);
        if (node == null) {
            throw new NullPointerException(
                    "cannot find child node fx:id for argument: " + id);
        }

        return node;
    }


    private void spawn() {
        selected = minoInQueue;
        minos.add(minoInQueue);
        // TODO update the preview here (refresh the preview box dynamically)
        // TODO for Muyang: Use "minoPreview" variable to access the preview mino
//        GameUIHelper helper = new GameUIHelper();
//        helper.updatePreviewBox(root, minoPreview);
        Platform.runLater(() -> {
            Node previewContainer = root.lookup("#preview");
            List<Node> elements = new ArrayList<>();
            if (previewContainer == null) {
                throw new NullPointerException("cannot find child node fx:id for argument: preview");
            } else {
                minoPreview.getPieces().forEach(p -> {
                    elements.add(GameUIHelper.generatePreviewElement(p.getTag().getImageString(), TILE_SIZE));
                });
                ((VBox)previewContainer).getChildren().clear();
                ((VBox)previewContainer).getChildren().addAll(elements);
            }
        });


        for (Piece piece : minoInQueue.getPieces()) {
            placePiece(piece);
        }
        if(!isValidateState()) {
            System.out.println("Game Over");
        }
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

    public void lunchPlayBoard(Stage stage) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("play-board.fxml"));
//        root = loader.load(); // Load the FXML file and get the root node

        // Cast the root node to AnchorPane (or the appropriate type)
//        AnchorPane anchorPane = (AnchorPane) root;

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