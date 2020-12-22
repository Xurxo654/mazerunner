package maze;

import java.util.*;

public class MazeSolver {

    private final Maze maze;
    private final int height;
    private final int width;
    private final Cell[][] createdCells;
    private final String[][] solvedGrid;

    //Constants
    private static final String PATH = "//";

    public MazeSolver(Maze maze){
        this.maze = maze;

        this.height = maze.getHeight();
        this.width = maze.getWidth();

        createdCells = new Cell[height][width];

        solvedGrid = Arrays.stream(maze.getMazeGrid())
                .map(String[]::clone)
                .toArray(String[][]::new);
        initCells();
        solveMaze();
    }

    public void draw() {
        for (String[] line : solvedGrid) {
            for (String symbol : line) {
                System.out.print(symbol);
            }
            System.out.println();
        }
    }

    private void solveMaze() {
        int exit = findExit();

        Deque<Cell> pathFound = new ArrayDeque<>();
        pathFound.offerLast(createdCells[0][findStart()]);

        while (true) {
            Cell selected = pathFound.peekLast();
            if (selected.getX() == exit && selected.getY() == height - 1) {
                break;
            }

            Cell next = getValidNeighbor(selected);
            if (next != null) {
                next.setInPath(true);
                next.setVisited(true);
                pathFound.offerLast(next);
            } else {
                selected.setInPath(false);
                pathFound.pollLast();
            }

            if (pathFound.isEmpty()) {
                System.out.println("no solution");
                return;
            }
        }
        drawPath(pathFound);
    }

    private void drawPath(Deque<Cell> pathFound) {
        while (!pathFound.isEmpty()) {
            Cell pathCell = pathFound.pollLast();
            solvedGrid[pathCell.getY()][pathCell.getX()] = PATH;
        }
    }

    private int findStart() {
        for (int i = 0; i < width; i++) {
            if (solvedGrid[0][i].equals("  ")) {
                return i;
            }
        }
        return -1;
    }

    private int findExit() {
        for (int i = 0; i < width; i++) {
            if (solvedGrid[height-1][i].equals("  ")) {
                return i;
            }

        }
        return -1;
    }

    private void initCells() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (solvedGrid[i][j].equals("  ")) {
                    createdCells[i][j] = new Cell(j, i, false);
                } else {
                    createdCells[i][j] = new Cell(j, i, true);
                }
            }
        }
        populateCellsNeighbor();
    }

    private void populateCellsNeighbor() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = createdCells[i][j];
                if (cell.isWall()) {
                    continue;
                }

                List<Cell> neighbors = new ArrayList<>(Arrays.asList(
                        getCell(cell.getX() + 1, cell.getY()),
                        getCell(cell.getX() - 1, cell.getY()),
                        getCell(cell.getX(), cell.getY() + 1),
                        getCell(cell.getX(), cell.getY() - 1)
                ));
                neighbors.removeIf(n -> (n == null || n.isVisited() || n.isWall()));

                if (!neighbors.isEmpty()) {
                    cell.addNeighbor(neighbors);
                }
            }
        }
    }

    private Cell getCell(int x, int y) {
        try {
            return createdCells[y][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private Cell getValidNeighbor(Cell selected) {
        if (selected.hasRightNeighbor()) {
            Cell next = getCell(selected.getX() + 1, selected.getY());
            if (!next.isVisited() && !next.isInPath()) {
                return next;
            }
        }

        if (selected.hasLowerNeighbor()) {
            Cell next = getCell(selected.getX(), selected.getY() + 1);
            if (!next.isVisited() && !next.isInPath()) {
                return next;
            }
        }

        if (selected.hasLeftNeighbor()) {
            Cell next = getCell(selected.getX() - 1, selected.getY());
            if (!next.isVisited() && !next.isInPath()) {
                return next;
            }
        }

        if (selected.hasUpperNeighbor()) {
            Cell next = getCell(selected.getX(), selected.getY() - 1);
            if (!next.isVisited() && !next.isInPath()) {
                return next;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "MazeSolver [maze=" + maze + ", height=" + height + ", width=" + width + "]";
    }
}
