package com.example.game;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.example.game.GameManager.TILE_SIZE;

public class Mino {
    private int x;
    private int y;
    private final List<Piece> pieces;

    public Mino(final Piece... pieces) {
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
        //render image based on the tag
        pieces.forEach(pc -> {
            gc.drawImage(pc.getTag().getImage(), pc.getX() * TILE_SIZE, pc.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        });
    }
    public void rotateBack() {
        System.out.println("rotate back");
        pieces.forEach(pc -> {
            pc.setDirection(pc.getDirection().prev());
        });
    }
    public void rotate() {
        System.out.println("rotate");
        pieces.forEach(pc -> {
            pc.setDirection(pc.getDirection().next());
        });
    }
    public void detach(final int xOnPane, final int yOnPane) {
        pieces.removeIf(pc -> pc.getX() == xOnPane && pc.getY() == yOnPane);
    }
    public Mino copy() {
        return new Mino(pieces.stream()
                .map(Piece::copy)
                .toList()
                .toArray(new Piece[0]));
    }

}
