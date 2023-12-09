package com.example.game;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
     * Constructs a Mino object.
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
     * Gets X in the board.
     *
     * @return int x
     */
    public int getX() {
        return x;
    }

    /**
     * Gets Y in the board.
     *
     * @return int y
     */
    public int getY() {
        return y;
    }

    /**
     * Gets pieces.
     *
     * @return List<Piece> pieces of mino
     */
    public List<Piece> getPieces() {
        return pieces;
    }
    /**
     * Moves the mino according to dx and dy, both should not be greater than 1.
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
     * Moves the mino by assigning the direction.
     *
     * @param direction Direction
     */
    public void move(final Direction direction) {
        move(direction.getX(), direction.getY());
    }
    /**
     * Draws the mino.
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
     * rotates mino back.
     */
    public void rotateBack() {
        pieces.forEach(pc -> pc.setDirection(pc.getDirection().prev()));
    }
    /**
     * rotates mino.
     */
    public void rotate() {
        pieces.forEach(pc -> pc.setDirection(pc.getDirection().next()));
    }
    /**
     * detaches the mino from the board.
     *
     * @param xOnBoard int x on the board
     * @param yOnBoard int y on the board
     */
    public void detach(final int xOnBoard, final int yOnBoard) {
        pieces.removeIf(pc -> pc.getX() == xOnBoard && pc.getY() == yOnBoard);
    }
    /**
     * copies the mino.
     *
     * @return Mino
     */
    public Mino copy() {
        return new Mino(pieces.stream()
                .map(Piece::copy)
                .toList()
                .toArray(new Piece[0]));
    }
    /**
     * Generates a string representation of the mino.
     *
     * @return String representation of the mino
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Mino{" + "x=").append(x).append(", y=").append(y);
        for (Piece piece : pieces) {
            output.append(", ").append(piece.toString());
        }
        output.append("}");
        return output.toString();
    }

    /**
     * Checks if the mino is equal to another object.
     *
     * @param o Object
     * @return boolean
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Mino mino = (Mino) o;
        return getX() == mino.getX() && getY() == mino.getY() && Objects.equals(getPieces(), mino.getPieces());
    }

    /**
     * Generates a hashcode for the mino.
     *
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getPieces());
    }
}
