package com.example.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Piece implements Serializable {

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


    public enum RecycleType implements Serializable {
        Plastic(10),
        Paper(12),
        Glass(14),
        Battery(16),
        Booster(18);

        @JsonProperty("id")
        private final int id;
        RecycleType(@JsonProperty("id") final int id) {
            this.id = id;
        }
        public int tagID() {
            return id;
        }
    }

    /**
     * Tag.
     */
    public static final class Tag implements Serializable {

        @JsonProperty("imageString")
        private final String imageString;

        @JsonProperty("type")
        public final RecycleType type;

        @JsonProperty("id")
        public int id;
        /**
         * Constructs an object of type Tag.
         */
        public Tag() {
            List<RecycleType> recycleTypes = new ArrayList<>();
            // chance of getting booster is 1/25
            for (RecycleType t : RecycleType.values()) {
                int count = t == RecycleType.Booster ? 1 : 5;
                for (int i = 0; i < count; i++) {
                    recycleTypes.add(t);
                }
            }
            Random random = new Random();
            type = recycleTypes.get(random.nextInt(recycleTypes.size()));
            imageString = "file:./src/asset/Image/" + type.name() + ".png";
            id = getID();
        }
        public RecycleType getType() {
            return type;
        }

        public String getImageString() {
            return imageString;
        }
        public int getID() {
            return type.tagID();
        }
    }

    public Piece(@JsonProperty("distance") final int distance, @JsonProperty("direction") final Direction direction) {
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
