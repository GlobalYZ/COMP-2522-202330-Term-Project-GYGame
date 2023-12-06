package com.example.game;

/**
 * A Direction class that represents the direction of a piece position.
 *
 * @author Grace Su
 * @version 2023
 */
public enum Direction {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0);

    private final int x;
    private final int y;

    Direction(final int x, final int y) {
        this.x = x;
        this.y = y;
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
     * Get Y.
     *
     * @return int y
     */
    public int getY() {
        return y;
    }

    /**
     * Get the previous direction.
     *
     * @return the previous Direction
     */
    public Direction prev() {
        int nextIndex = ordinal() - 1;

        if (nextIndex == -1) {
            nextIndex = Direction.values().length - 1;
        }

        return Direction.values()[nextIndex];
    }

    /**
     * Get the next direction.
     *
     * @return the next Direction
     */
    public Direction next() {
        int nextIndex = ordinal() + 1;

        if (nextIndex == Direction.values().length) {
            nextIndex = 0;
        }

        return Direction.values()[nextIndex];
    }

}
