package com.example.game;

import javafx.scene.image.Image;

import java.util.Random;

public class Piece {
    private final int distance;
    private Direction direction;
    private final Tag tag;
    private Mino parent;
    private boolean isSpecial;
    private int x;
    private int y;
    /**
     * RecycleType.
     */
    public enum RecycleType {
        Plastic,
        Paper,
        Glass,
        Battery,
        Booster,
        Recycling
    }

    /**
     * Nested class Tag.
     */
    public static final class Tag {
        private final Image image;
        private final RecycleType type;
        /**
         * Constructs an object of type Tag.
         */
        public Tag() {
            Random random = new Random();
            RecycleType[] recycleTypes = RecycleType.values();
            type = recycleTypes[random.nextInt(recycleTypes.length)];
            image = new Image("file:./src/asset/Image/" + type + ".png");
        }
        public RecycleType getType() {
            return type;
        }
        public Image getImage() {
            return image;
        }

    }

    public Piece(final int distance, final Direction direction) {
        this.distance = distance;
        this.direction = direction;
        this.tag = new Tag();
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
    public Tag getTag() {
        return tag;
    }
    public void setDirection(final Direction direction) {
        this.direction = direction;
        x = parent.getX() + distance * direction.getX();
        y = parent.getY() + distance * direction.getY();
    }
    public Direction getDirection() {
        return direction;
    }
    public void setParent(final Mino parent) {
        this.parent = parent;
        x = parent.getX() + distance * direction.getX();
        y = parent.getY() + distance * direction.getY();
    }
    public boolean isSpecial() {
        return isSpecial;
    }

    public Piece copy() {
        return new Piece(distance, direction);
    }

}
