package com.example.game;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
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
        Plastic(10),
        Paper(11),
        Glass(12),
        Battery(13),
        Booster(14);
        private final int id;
        RecycleType(final int id) {
            this.id = id;
        }
        public int tagID() {
            return id;
        }
    }

    /**
     * Tag.
     */
    public static final class Tag {
        private final Image image;
        public final RecycleType type;
        /**
         * Constructs an object of type Tag.
         */
        public Tag() {
            List<RecycleType> recycleTypes = new ArrayList<>();
            // chance of getting booster is 1/16
            for (RecycleType t : RecycleType.values()) {
                int count = t == RecycleType.Booster ? 1 : 4;
                for (int i = 0; i < count; i++) {
                    recycleTypes.add(t);
                }
            }
            Random random = new Random();
            type = recycleTypes.get(random.nextInt(recycleTypes.size()));
            image = new Image("file:./src/asset/Image/" + type.name() + ".png");
        }
        public RecycleType getType() {
            return type;
        }
        public Image getImage() {
            return image;
        }
        public int getID() {
            return type.tagID();
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

    public void setParent(final Mino parent) {
        this.parent = parent;
        x = parent.getX() + distance * direction.getX();
        y = parent.getY() + distance * direction.getY();
    }
    public void setDirection(final Direction direction) {
        this.direction = direction;
        x = parent.getX() + distance * direction.getX();
        y = parent.getY() + distance * direction.getY();
    }
    public Direction getDirection() {
        return direction;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public Piece copy() {
        return new Piece(distance, direction);
    }



}
