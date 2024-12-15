package org.example;
import java.util.*;

public class LabyrinthGenerator {
    private final int rows;
    private final int cols;
    private final GridCell[][] grid;
    private final Random random = new Random();
    private final Set<GridCell> visited = new HashSet<>();

    public LabyrinthGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new GridCell[rows][cols];
        random.setSeed(1);
    }

    public GameGrid generateLabyrinth() {
        GameGrid gameGrid = new GameGrid(rows, cols);

        // Initialize grid with all cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new GridCell(i, j);
                // Initially all cells have all walls
                grid[i][j].updateBorder();
            }
        }

        // Generate the maze paths
        generateMazePaths(0, 0);

        // Add border walls
        addBorderWalls();

        // Add all cells to the game grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gameGrid.add(grid[i][j]);
            }
        }

        gameGrid.setGameCells(grid);
        return gameGrid;
    }

    private void generateMazePaths(int row, int col) {
        GridCell current = grid[row][col];
        visited.add(current);

        // Define possible directions: up, right, down, left
        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        List<int[]> possibleDirections = new ArrayList<>(Arrays.asList(directions));

        while (!possibleDirections.isEmpty()) {
            // Choose random direction
            int index = random.nextInt(possibleDirections.size());
            int[] dir = possibleDirections.remove(index);

            int newRow = row + dir[0];
            int newCol = col + dir[1];

            // Check if the new position is valid and unvisited
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols
                    && !visited.contains(grid[newRow][newCol])) {

                GridCell next = grid[newRow][newCol];

                // Remove walls between current and next cell
                if (dir[0] == -1) { // Up
                    current.up = next;
                    next.down = current;
                } else if (dir[0] == 1) { // Down
                    current.down = next;
                    next.up = current;
                } else if (dir[1] == -1) { // Left
                    current.left = next;
                    next.right = current;
                } else if (dir[1] == 1) { // Right
                    current.right = next;
                    next.left = current;
                }

                // Update borders for both cells
                current.updateBorder();
                next.updateBorder();

                // Continue with next cell
                generateMazePaths(newRow, newCol);
            }
        }
    }

    private void addBorderWalls() {
        // Add walls around the border
        for (int i = 0; i < rows; i++) {
            // Left border
            grid[i][0].left = null;
            grid[i][0].updateBorder();

            // Right border
            grid[i][cols-1].right = null;
            grid[i][cols-1].updateBorder();
        }

        for (int j = 0; j < cols; j++) {
            // Top border
            grid[0][j].up = null;
            grid[0][j].updateBorder();

            // Bottom border
            grid[rows-1][j].down = null;
            grid[rows-1][j].updateBorder();
        }
    }
}