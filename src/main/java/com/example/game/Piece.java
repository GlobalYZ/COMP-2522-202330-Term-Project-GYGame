package com.example.game;

public class Piece {
    private int distance;
    private Direction direction;
    private Tag tag;
    private Mino parent;
    private boolean isSpecial;
    private int x;
    private int y;

    public Piece(final int distance, final Direction direction) {
        this.distance = distance;
        this.direction = direction;
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
    public void setTag(final Tag tag) {
        this.tag = tag;
    }
    public boolean isSpecial() {
        return isSpecial;
    }
    // Copy the piece object to maintain immutability.
    public Piece copy() {
        return new Piece(distance, direction);
    }

}
