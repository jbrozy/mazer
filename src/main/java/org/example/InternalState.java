package org.example;

public class InternalState {
    public int[][] field;

    public void init(int width, int height) {
        field = new int[width][height];
    }

    public void update(InternalState internalState){
        this.field = internalState.field;
    }
}
