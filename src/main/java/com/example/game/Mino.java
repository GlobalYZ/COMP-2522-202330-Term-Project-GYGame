package com.example.game;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.example.game.GameManager.TILE_SIZE;

/**
 * A Mino class that represents a mino.
 *
 * @author Grace Su
 * @version 2023
 */
public class Mino implements Serializable {
    private int x;

    private int y;

    private final List<Piece> pieces;

    /**
     * Constructor.
     *
     * @param pieces The pieces of the mino.
     */
    public Mino(@JsonProperty("pieces") final Piece... pieces) {
        this.pieces = new ArrayList<>(Arrays.asList(pieces));

        for (Piece piece : this.pieces) {
            piece.setParent(this);
        }
    }
    /**
     * Get X.
     *
     * @return int x
     */
    public int getX() {
        return x;
    }
    /**
     * set X.
     *
     * @param x x
     */
    public void setX(final int x) {
        this.x = x;
    }
    /**
     * Get Y.
     *
     * @return int y
     */
    public int getY() {
        return y;
    }
    /**
     * set Y.
     *
     * @param y y
     */
    public void setY(final int y) {
        this.y = y;
    }
    /**
     * Get pieces.
     *
     * @return List<Piece> pieces of mino
     */
    public List<Piece> getPieces() {
        return pieces;
    }
    /**
     * Move the mino according to dx and dy, both should not be greater than 1.
     *
     * @param dx int distance of x-axis
     * @param dy int distance of y-axis
     */
    public void move(final int dx, final int dy) {
        this.x += dx;
        this.y += dy;
        pieces.forEach(pc -> {
            pc.setX(pc.getX() + dx);
            pc.setY(pc.getY() + dy);
        });
    }
    /**
     * Move the mino by assigning the direction.
     *
     * @param direction Direction
     */
    public void move(final Direction direction) {
        move(direction.getX(), direction.getY());
    }
    /**
     * Draw the mino.
     *
     * @param gc GraphicsContext
     */
    public void draw(final GraphicsContext gc) {

        pieces.forEach(pc -> {
            Image image = new Image(pc.getTag().getImageString());
            gc.drawImage(image, pc.getX() * TILE_SIZE, pc.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        });
    }
    /**
     * rotate mino back.
     */
    public void rotateBack() {
        pieces.forEach(pc -> pc.setDirection(pc.getDirection().prev()));
    }
    /**
     * rotate mino.
     */
    public void rotate() {
        pieces.forEach(pc -> pc.setDirection(pc.getDirection().next()));
    }
    /**
     * detach the mino from the board.
     *
     * @param xOnBoard int x on the board
     * @param yOnBoard int y on the board
     */
    public void detach(final int xOnBoard, final int yOnBoard) {
        pieces.removeIf(pc -> pc.getX() == xOnBoard && pc.getY() == yOnBoard);
    }
    /**
     * copy the mino.
     *
     * @return Mino
     */
    public Mino copy() {
        return new Mino(pieces.stream()
                .map(Piece::copy)
                .toList()
                .toArray(new Piece[0]));
    }

}
