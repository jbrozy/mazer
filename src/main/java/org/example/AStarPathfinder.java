package org.example;

import java.awt.Color;
import java.util.*;

public class AStarPathfinder {
    // Node class to store path and state information for A* search
    private static class PathNode implements Comparable<PathNode> {
        GridCell cell;                // Current cell
        List<GridCell> pathSoFar;     // Path taken to reach this cell
        Set<Color> colorsFound;       // Colors found along the path
        double gScore;                // Cost from start to current node
        double fScore;                // Estimated total cost (g + h)

        PathNode(GridCell cell, List<GridCell> pathSoFar, Set<Color> colorsFound, double gScore, double hScore) {
            this.cell = cell;
            this.pathSoFar = new ArrayList<>(pathSoFar);
            this.pathSoFar.add(cell);
            this.colorsFound = new HashSet<>(colorsFound);
            if (isTargetColor(cell.getCellColor())) {
                this.colorsFound.add(cell.getCellColor());
            }
            this.gScore = gScore;
            this.fScore = gScore + hScore;
        }

        @Override
        public int compareTo(PathNode other) {
            return Double.compare(this.fScore, other.fScore);
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

    // Helper class to store state information for visited nodes
    private static class VisitedState {
        double gScore;
        Set<Color> colors;

        VisitedState(double gScore, Set<Color> colors) {
            this.gScore = gScore;
            this.colors = colors;
        }
    }

    // Calculate Manhattan distance between two cells
    private static double manhattanDistance(GridCell a, GridCell b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    // Calculate heuristic value for remaining unvisited colored cells
    private static double calculateHeuristic(GridCell current, List<GridCell> coloredCells, Set<Color> foundColors) {
        double minDistance = Double.MAX_VALUE;

        // If we've found all colors, no additional heuristic needed
        if (foundColors.size() == 3) {
            return 0;
        }

        // Find the nearest unvisited colored cell
        for (GridCell coloredCell : coloredCells) {
            if (!foundColors.contains(coloredCell.getCellColor())) {
                double distance = manhattanDistance(current, coloredCell);
                minDistance = Math.min(minDistance, distance);
            }
        }

        return minDistance;
    }

    // Find all colored cells in the grid
    private static List<GridCell> findColoredCells(GridCell start) {
        List<GridCell> coloredCells = new ArrayList<>();
        Set<GridCell> visited = new HashSet<>();
        Queue<GridCell> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            GridCell current = queue.poll();

            if (PathNode.isTargetColor(current.getCellColor())) {
                coloredCells.add(current);
            }

            for (GridCell neighbor : current.getNeighbors()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return coloredCells;
    }

    public static List<GridCell> findOptimalPath(GridCell start) {
        // Find all colored cells first for heuristic calculation
        List<GridCell> coloredCells = findColoredCells(start);

        // Priority queue for A* open set
        PriorityQueue<PathNode> openSet = new PriorityQueue<>();

        // Map to track best known scores for each state
        Map<String, VisitedState> bestScores = new HashMap<>();

        // Initialize search with starting node
        PathNode startNode = new PathNode(start, new ArrayList<>(), new HashSet<>(), 0,
                calculateHeuristic(start, coloredCells, new HashSet<>()));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            PathNode current = openSet.poll();

            // Check if we've found all colors
            if (current.hasFoundAllColors()) {
                return current.pathSoFar;
            }

            // Generate state key for current node
            String stateKey = current.cell.x + "," + current.cell.y + "," +
                    current.colorsFound.toString();

            // Check if we've found a better path to this state
            VisitedState bestScore = bestScores.get(stateKey);
            if (bestScore != null && bestScore.gScore <= current.gScore) {
                continue;
            }

            // Update best score for this state
            bestScores.put(stateKey, new VisitedState(current.gScore, current.colorsFound));

            // Explore neighbors
            for (GridCell neighbor : current.cell.getNeighbors()) {
                // Calculate new g score (cost to reach neighbor)
                double newGScore = current.gScore + 1;

                // Calculate heuristic for neighbor
                double hScore = calculateHeuristic(neighbor, coloredCells, current.colorsFound);

                // Create new path node for neighbor
                PathNode neighborNode = new PathNode(
                        neighbor,
                        current.pathSoFar,
                        current.colorsFound,
                        newGScore,
                        hScore
                );

                openSet.add(neighborNode);
            }
        }

        return new ArrayList<>(); // Return empty list if no path is found
    }
    // Helper method to visualize the path
    public static void visualizePath(List<GridCell> path) {
        if (path.isEmpty()) {
            System.out.println("No valid path found!");
            return;
        }

        // Store original colors and visited cells
        Map<GridCell, Color> originalColors = new HashMap<>();
        Set<GridCell> visitedCells = new HashSet<>();

        for (GridCell cell : path) {
            // Store original color if not already done
            originalColors.putIfAbsent(cell, cell.getCellColor());

            // Check if the cell was visited before
            if (visitedCells.contains(cell)) {
                // Change color to magenta if revisited
                if(!PathNode.isTargetColor(cell.getCellColor()))
                    cell.setCellColor(Color.YELLOW); // Magenta
            } else {
                // Set color for the first visit (e.g., light yellow)
                if (!PathNode.isTargetColor(cell.getCellColor())) {
                    cell.setCellColor(new Color(255, 255, 200)); // Light yellow
                }
                visitedCells.add(cell);
            }
        }
    }
}