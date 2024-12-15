package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GridCell extends JLabel {
    public int x;
    public int y;
    public GridCell left, right, up, down;
    public Color cellColor = Color.WHITE;
    private final Color BORDER_COLOR = Color.RED;
    private static final int BORDER_WIDTH = 1;

    public GridCell(int x, int y) {
        this.x = x;
        this.y = y;
        setText("");
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        setBackground(cellColor);
        updateBorder();
    }

    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
        setBackground(cellColor);
        repaint();
        revalidate();
    }

    public Color getCellColor() {
        return cellColor;
    }

    public void updateBorder() {
        int leftBorder = left == null ? 1 : 0;
        int rightBorder = right == null ? 1 : 0;
        int upBorder = up == null ? 1 : 0;
        int downBorder = down == null ? 1 : 0;

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        upBorder * BORDER_WIDTH,    // top thickness
                        leftBorder * BORDER_WIDTH,  // left thickness
                        downBorder * BORDER_WIDTH,  // bottom thickness
                        rightBorder * BORDER_WIDTH, // right thickness
                        BORDER_COLOR
                ),
                BorderFactory.createEmptyBorder( // Inner padding
                        10, 10, 10, 10 // Top, Left, Bottom, Right padding
                )
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Add any custom painting here if needed
    }

    public List<GridCell> getNeighbors() {
        var neighbors = new ArrayList<GridCell>();
        if (left != null) neighbors.add(left);
        if (right != null) neighbors.add(right);
        if (up != null) neighbors.add(up);
        if (down != null) neighbors.add(down);

        return neighbors;
    }
}