package org.example;

import java.awt.Color;
import java.util.*;

public class GridPathfinder {
    // Helper class to store path information during BFS
    private static class PathNode {
        GridCell cell;
        List<GridCell> pathSoFar;
        Set<Color> colorsFound;

        PathNode(GridCell cell, List<GridCell> pathSoFar, Set<Color> colorsFound) {
            this.cell = cell;
            this.pathSoFar = new ArrayList<>(pathSoFar);
            this.pathSoFar.add(cell);
            this.colorsFound = new HashSet<>(colorsFound);
            if (isTargetColor(cell.getCellColor())) {
                this.colorsFound.add(cell.getCellColor());
            }
        }

        static boolean isTargetColor(Color color) {
            return color.equals(Color.ORANGE) ||
                    color.equals(Color.BLUE) ||
                    color.equals(Color.GREEN);
        }

        boolean hasFoundAllColors() {
            return colorsFound.size() == 3;
        }
    }

    public static List<GridCell> findShortestPathToColors(GridCell start) {
        Queue<PathNode> queue = new LinkedList<>();
        Set<GridCell> visited = new HashSet<>();
        Map<String, Set<Color>> visitedStates = new HashMap<>();

        // Initialize the search with the starting cell
        queue.add(new PathNode(start, new ArrayList<>(), new HashSet<>()));

        while (!queue.isEmpty()) {
            PathNode current = queue.poll();
            GridCell currentCell = current.cell;

            // Create a unique state key for this cell and its collected colors
            String stateKey = currentCell.x + "," + currentCell.y;

            // Skip if we've been here before with the same or better color collection
            Set<Color> previousColors = visitedStates.get(stateKey);
            if (previousColors != null && previousColors.containsAll(current.colorsFound)) {
                continue;
            }

            // Update the visited states
            visitedStates.put(stateKey, current.colorsFound);

            // Check if we've found all colors
            if (current.hasFoundAllColors()) {
                return current.pathSoFar;
            }

            // Explore neighbors
            for (GridCell neighbor : currentCell.getNeighbors()) {
                if (!visited.contains(neighbor)) {
                    queue.add(new PathNode(neighbor, current.pathSoFar, current.colorsFound));
                } else {

                }
            }

            visited.add(currentCell);
        }

        return new ArrayList<>(); // Return empty list if no path is found
    }

    // Helper method to visualize the path by setting background colors
    public static void visualizePath(List<GridCell> path) {
        if (path.isEmpty()) {
            System.out.println("No valid path found!");
            return;
        }

        // Store original colors
        // Map<GridCell, Color> originalColors = new HashMap<>();
        for (GridCell cell : path) {
            // originalColors.put(cell, cell.getCellColor());
            // Skip coloring cells that are already target colors
            if (!PathNode.isTargetColor(cell.getCellColor())) {
                cell.setCellColor(new Color(255, 255, 200)); // Light yellow for path
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}