package com.example.game;

public enum Direction {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0);

    private int x;
    private int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Get the x value
    public int getX() {
        return x;
    }

    // Get the y value
    public int getY() {
        return y;
    }

    // Get the previous direction
    public Direction prev() {
        int nextIndex = ordinal() - 1;

        if (nextIndex == -1) {
            nextIndex = Direction.values().length - 1;
        }

        return Direction.values()[nextIndex];
    }

    // Get the next direction
    public Direction next() {
        int nextIndex = ordinal() + 1;

        if (nextIndex == Direction.values().length) {
            nextIndex = 0;
        }

        return Direction.values()[nextIndex];
    }

}
