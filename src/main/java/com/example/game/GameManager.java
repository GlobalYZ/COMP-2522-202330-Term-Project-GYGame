package com.example.game;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
    private double time;

    public GraphicsContext gc;

    private Integer scoreNum = 0;

    private Integer scoreAchieved = 0;

    private Integer comboCount = 0;

    private Integer level = 1;

    public static final int TILE_SIZE = 40; // Grace's constant don't delete it.
    public static final int GRID_WIDTH = 10;
    public static final int GRID_HEIGHT = 14;

    private final int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];


    private final List<Mino> original = new ArrayList<>();  // initial minos
    private final List<Mino> minos = new ArrayList<>();  // minos on the board
    private Mino selected; // using user input to move this mino


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
        //1
        //0
        //1
        original.add(new Mino(
                new Piece(0, Direction.DOWN),
                new Piece(1, Direction.UP),
                new Piece(1, Direction.DOWN)));
        //  1
        //1 0 1
        //  1
        original.add(new Mino(
                new Piece(0, Direction.DOWN),
                new Piece(1, Direction.UP),
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



    private Parent setContent(){
        Pane root = new Pane();
        root.setPrefSize(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);

        Canvas canvas = new Canvas(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        generateBasicMinos();
        spawn();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                time += 0.03;
                if(time >= 0.5) {
                    update();
                    render();
                    time = 0;
                }
            }
        };
        timer.start();
        return root;
    }

    private void update() {
        makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
    }

    private void makeMove(Consumer<Mino> onSuccess, Consumer<Mino> onFail, boolean endMove) {
        selected.move(Direction.DOWN);
        onSuccess.accept(selected);
        boolean offBoard = selected.getPieces().stream().anyMatch(this::isOffBoard);
        if(!offBoard) {
            selected.getPieces().forEach(p -> placePiece(p));
        } else {
            onFail.accept(selected);
            selected.getPieces().forEach(p -> placePiece(p));
        }
        if (endMove) {
            System.out.println("end move");
        }
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
        Mino mino = original.get(new Random().nextInt(original.size())).copy();
        mino.move(GRID_WIDTH / 2, 0);
        selected = mino;
        minos.add(mino);
        // to be continued.
        for (Piece piece : mino.getPieces()) {
            placePiece(piece);
        }
    }

    public void placePiece(final Piece piece) {
        grid[piece.getX()][piece.getY()]++;
    }

    public void removePiece(final Piece piece) {
        grid[piece.getX()][piece.getY()] = 0;
    }

    private boolean isOffBoard(final Piece piece) {
        return piece.getX() < 0 || piece.getX() >= GRID_WIDTH || piece.getY() < 0 || piece.getY() >= GRID_HEIGHT;
    }

    public void lunchPlayBoard(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("play-board.fxml"));
        Parent root = loader.load(); // Load the FXML file and get the root node

        // Cast the root node to AnchorPane (or the appropriate type)
        AnchorPane anchorPane = (AnchorPane) root;

        HBox gameContainer = new HBox();

        // get user screen size
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        double gamePadding = (screenHeight - 670) / 2;

        // Create an ImageView and set an image
        Image image = new Image("file:./src/asset/Image/background.jpg");
        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(anchorPane.widthProperty());
        imageView.fitHeightProperty().bind(anchorPane.heightProperty());

        gameContainer.setPrefSize(screenWidth, screenHeight);
        gameContainer.setAlignment(Pos.CENTER);

        //create game board boxes
        FlowPane gameBoard = new FlowPane();
        gameBoard.setPrefWidth(600);
        gameBoard.setPadding(new Insets(gamePadding, 0, gamePadding, 0));

        //create score box
        Node scoreBox = GameUIHelper.createScoreBoard(scoreNum, scoreAchieved);

        //create playGround
        Parent playGround = setContent();
        playGround.setStyle(GameUIHelper.boxStyle);

        //create preview box and previewElements
        Node previewBox = GameUIHelper.createPreviewBox();

        //create LvBox
        Node LvBox = GameUIHelper.createLvBox(level);

        VBox rightWrapper = new VBox();
        rightWrapper.getChildren().addAll(previewBox, LvBox);
        rightWrapper.setMargin(previewBox, new Insets(0, 0, 40, 50));
        rightWrapper.setMargin(LvBox, new Insets(10, 0, 10, 50));

        //link to game board
        gameBoard.getChildren().addAll(scoreBox, playGround, rightWrapper);
        gameBoard.setMargin(scoreBox, new Insets(0, 0, 0, 60));
        gameBoard.setMargin(playGround, new Insets(0, 0, 0, 60));

        gameContainer.getChildren().add(gameBoard);
        anchorPane.getChildren().addAll(imageView, gameContainer);

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