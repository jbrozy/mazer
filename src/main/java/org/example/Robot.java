package org.example;

import java.util.HashSet;

enum Rotation {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    /**
     * Rotate the current rotation to the right.
     *
     * @return The new rotation after rotating right.
     */
    public Rotation rotateRight() {
        return values()[(ordinal() + 1) % values().length];
    }

    /**
     * Rotate the current rotation to the left.
     *
     * @return The new rotation after rotating left.
     */
    public Rotation rotateLeft() {
        return values()[(ordinal() + values().length - 1) % values().length];
    }
}

public class Robot {
    public int x;
    public int y;
    public Rotation rotation = Rotation.UP;
    public GridCell startingCell;
    public HashSet<GridCell> cells = new HashSet<>();

    public Robot(int x, int y) {
        startingCell = new GridCell(x, y);
    }

    public void rotateRight(){
        rotation.rotateRight();
    }
    public void rotateLeft(){
        rotation.rotateLeft();
    }

    public GridCell move(){
        int nextX = x;
        int nextY = y;
        GridCell nextCell = new GridCell(nextX, nextY);
        return nextCell;
    }
}
