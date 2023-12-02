package com.example.game;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.example.game.GameManager.TILE_SIZE;

public class Mino implements Serializable {


    private int x;

    private int y;

    private final List<Piece> pieces;


    public Mino(@JsonProperty("pieces") final Piece... pieces) {
        this.pieces = new ArrayList<>(Arrays.asList(pieces));

        for (Piece piece : this.pieces) {
            piece.setParent(this);
        }
    }
    public int getX() {
        return x;
    }
    public void setX(final int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(final int y) {
        this.y = y;
    }
    public List<Piece> getPieces() {
        return pieces;
    }
    // move the mino by dx and dy, both should not be greater than 1.
    public void move(final int dx, final int dy) {
        this.x += dx;
        this.y += dy;
        pieces.forEach(pc -> {
            pc.setX(pc.getX() + dx);
            pc.setY(pc.getY() + dy);
        });
    }
    // move the mino by assigning the direction.
    public void move(final Direction direction) {
        move(direction.getX(), direction.getY());
    }



    public void draw(final GraphicsContext gc) {

        pieces.forEach(pc -> {
            Image image = new Image(pc.getTag().getImageString());
            gc.drawImage(image, pc.getX() * TILE_SIZE, pc.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        });
    }
    public void rotateBack() {
        pieces.forEach(pc -> {
            pc.setDirection(pc.getDirection().prev());
        });
    }
    public void rotate() {
        pieces.forEach(pc -> {
            pc.setDirection(pc.getDirection().next());
        });
    }
    public void detach(final int xOnBoard, final int yOnBoard) {
        pieces.removeIf(pc -> pc.getX() == xOnBoard && pc.getY() == yOnBoard);
    }
    public Mino copy() {
        return new Mino(pieces.stream()
                .map(Piece::copy)
                .toList()
                .toArray(new Piece[0]));
    }

}
