package org.example;

import javax.swing.*;
import java.awt.*;

public class GameGrid extends JPanel {
    private int rows;
    private int columns;
    private GridCell[][] gameCells;

    public GameGrid(int rows, int columns) {
        this.setLayout(new GridLayout(rows, columns, 2, 2));
        this.rows = rows;
        this.columns = columns;
    }

    public void setGameCells(GridCell[][] gameCells) {
        this.gameCells = gameCells;
    }

    public GridCell[][] getGameCells() {
        return gameCells;
    }

    @Override
    public Dimension getPreferredSize() {
        int width = getParent().getWidth();
        int cellHeight = width / columns;
        return new Dimension(width, cellHeight * rows);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Add any custom painting here if needed
    }
}
