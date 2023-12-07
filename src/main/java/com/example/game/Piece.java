package com.example.game;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A Piece class that represents a piece in a mino.
 *
 * @author Grace Su
 * @version 2023
 */
public class Piece implements Serializable {

    private final int distance;

    private Direction direction;

    private final Tag tag;

    private Mino parent;

    private int x;

    private int y;


    /**
     * RecycleType to demonstrate the type of the tag.
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
        /**
         * Get the id of the tag.
         *
         * @return int id
         */
        public int tagID() {
            return id;
        }
    }

    /**
     * Tag.
     */
    public static final class Tag implements Serializable {

        /**
         * RecycleType to demonstrate the type of the tag.
         */
        @JsonProperty("type")
        public final RecycleType type;

        @JsonProperty("imageString")
        private final String imageString;
        /**
         * Constructs an object of type Tag.
         */
        public Tag() {
            final int four = 4;
            List<RecycleType> recycleTypes = new ArrayList<>();
            // chance of getting booster is 1/25
            for (RecycleType t : RecycleType.values()) {
                int count;
                if (t == RecycleType.Booster) {
                    count = 1;
                } else {
                    count = four;
                }
                for (int i = 0; i < count; i++) {
                    recycleTypes.add(t);
                }
            }
            Random random = new Random();
            type = recycleTypes.get(random.nextInt(recycleTypes.size()));
            imageString = "file:./src/asset/Image/" + type.name() + ".png";
        }
        /**
         * Get the image string of the tag.
         *
         * @return String imageString
         */
        public String getImageString() {
            return imageString;
        }
        /**
         * Get the id of the tag.
         *
         * @return int id
         */
        public int getID() {
            return type.tagID();
        }
    }

    /**
     * Constructs an object of type Piece.
     *
     * @param distance The distance of the piece from the parent.
     * @param direction The direction of the piece from the parent.
     */
    public Piece(@JsonProperty("distance") final int distance, @JsonProperty("direction") final Direction direction) {
        this.distance = distance;
        this.direction = direction;
        this.tag = new Tag();
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
     * Set X.
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
     * Set Y.
     *
     * @param y y
     */
    public void setY(final int y) {
        this.y = y;
    }

    /**
     * Get the tag of the piece.
     *
     * @return Tag tag
     */
    public Tag getTag() {
        return tag;
    }


    /**
     * Set the parent of the piece.
     *
     * @param parent The parent of the piece.
     */
    public void setParent(final Mino parent) {
        this.parent = parent;
        x = parent.getX() + distance * direction.getX();
        y = parent.getY() + distance * direction.getY();
    }

    /**
     * Set the direction of the piece.
     *
     * @param direction The direction of the piece.
     */
    public void setDirection(final Direction direction) {
        this.direction = direction;
        x = parent.getX() + distance * direction.getX();
        y = parent.getY() + distance * direction.getY();
    }

    /**
     * Get the direction of the piece.
     *
     * @return Direction direction
     */
    public Direction getDirection() {
        return direction;
    }
    /**
     * Copy the piece.
     *
     * @return Piece copied piece
     */
    public Piece copy() {
        return new Piece(distance, direction);
    }
}
