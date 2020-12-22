package maze;

import java.io.*;
import java.util.*;

public class Maze {
    private static final Random random = new Random();
    private int height;
    private int width;
    private int cellHeight;
    private int cellWidth;
    private String[][] mazeGrid;
    private Cell[][] cellGrid;

    //Constants
    private static final String FREE = "  ";
    private static final String WALL = "\u2588\u2588";

    //Constructors
    public Maze(int size){
        this(size, size);
    }

    public Maze(int height, int width){
        initDimensions(height, width);

        initCells();
        generateMaze();
    }

    public Maze(FileReader existingMaze) throws IOException {
        int qtdColumns;
        int qtdLines;
        StringBuilder mazeString = new StringBuilder();

        try (BufferedReader readMaze = new BufferedReader(existingMaze)){
            String line = readMaze.readLine();
            qtdColumns = line.length()/2;
            qtdLines = 0;

            while (line != null){
                qtdLines++;
                mazeString.append(line).append("#");
                line = readMaze.readLine();
            }
        }

        initDimensions(qtdLines, qtdColumns);
        importMazeToGrid(mazeString.toString());
    }

    //init
    private void initDimensions(int height, int width) {
        this.height = height;
        this.width = width;
        mazeGrid = new String[height][width];

        this.cellHeight = (height - 1)/2;
        this.cellWidth = (width - 1)/2;
    }

    private void initCells() {
        cellGrid = new Cell[cellHeight][cellWidth];
        for(int i = 0; i < cellHeight; i++){
            for( int j = 0; j < cellWidth; j++){
                cellGrid[i][j] = new Cell(j, i, false);
            }
        }
    }

    //Maze Creation
    private void generateMaze(){
        generateMaze(random.nextInt(cellHeight), 0);
    }

    private void generateMaze(int startX, int startY){
        Cell startCell = cellGrid[startX][startY]; //check later
        startCell.setVisited(true);
        List<Cell> cellsVisited = new ArrayList<>();
        cellsVisited.add(startCell);

        while(!cellsVisited.isEmpty()) {
            Cell cell;

            cell = cellsVisited.remove(cellsVisited.size() - 1);

            List<Cell> neighbors = new ArrayList<>(Arrays.asList(
                    getCell(cell.getX() + 1, cell.getY()),
                    getCell(cell.getX() - 1, cell.getY()),
                    getCell(cell.getX(), cell.getY() + 1),
                    getCell(cell.getX(), cell.getY() - 1)
            ));
            neighbors.removeIf(n -> (n == null || n.isVisited() || n.isWall()));

            if(!neighbors.isEmpty()) {
                Cell selected = neighbors.get(random.nextInt(neighbors.size()));
                cell.addNeighbor(selected);
                selected.setVisited(true);

                cellsVisited.add(cell);
                cellsVisited.add(selected);
            }
        }

        createGrid();
    }

    public void draw() {
        for (String[] line : mazeGrid) {
            for (String symbol : line) {
                System.out.print(symbol);
            }
            System.out.println();
        }
    }

    private Cell getCell(int x, int y) {
        try {
            return cellGrid[y][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private void createGrid() {
        createDefaultWalls();

        for (int i = 0; i < cellHeight; i++) {
            for(int j = 0; j < cellWidth; j++){
                Cell selected = cellGrid[i][j];

                int mazeI = 2*i + 1;
                int mazeJ = 2*j + 1;
                mazeGrid[mazeI][mazeJ] = FREE;

                if(selected.hasRightNeighbor()) {
                    mazeGrid[mazeI][mazeJ + 1] = FREE;
                } else {
                    mazeGrid[mazeI][mazeJ + 1] = WALL;
                }

                if(selected.hasLowerNeighbor()) {
                    mazeGrid[mazeI + 1][mazeJ] = FREE;
                } else {
                    mazeGrid[mazeI + 1][mazeJ] = WALL;
                }

            }
        }
        drawRemainingWalls();
        drawRemainingFree();
        insertEntranceAndExit();
    }

    //creates top row and left col walls
    private void createDefaultWalls() {
        for (int i = 0; i < width; i++) {
            mazeGrid[0][i] = WALL;
        }

        for (int i = 0; i < height; i++) {
            mazeGrid[i][0] = WALL;

        }
    }

    private void drawRemainingWalls() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if(mazeGrid[i][j] == null ) {
                    mazeGrid[i][j] = WALL;
                }
            }
        }
    }

    private void insertEntranceAndExit() {
        int randomEntrance = random.nextInt(cellGrid[0].length);
        mazeGrid[0][2*randomEntrance + 1] = FREE;

        int randomExit = random.nextInt(cellGrid.length);
        while (randomExit == randomEntrance) {
            randomExit = random.nextInt(cellGrid.length);
        }

        mazeGrid[mazeGrid[0].length - 1][2*randomExit + 1] = FREE;

        // Adjust the output for the even-sized case. Adds more white space
        if ( height % 2 == 0){
            mazeGrid[mazeGrid[0].length - 2][2*randomExit + 1] = FREE;
        }
    }

    private void drawRemainingFree() {
        if ( height % 2 == 0) {
            for (int i = 0; i < cellHeight; i++) {
                if(random.nextInt(2) == 1) {
                    mazeGrid[2*i+1][mazeGrid[0].length - 2] = FREE;
                }

            }
        }

        if (width % 2 == 0) {
            for (int i = 0; i < cellWidth; i++) {
                if(random.nextInt(2) == 1) {
                    mazeGrid[mazeGrid[0].length - 2][2*i+1] = FREE;
                }
            }
        }
    }

    private void importMazeToGrid(String mazeString) {
        String[] lines = mazeString.split("#");

        for (int i = 0; i < lines.length; i++) {
            mazeGrid[i] = lines[i].split("(?<=\\G.{2})");
        }
    }

    public void saveMaze(String filename) throws IOException {
        try (BufferedWriter mazeArq = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    mazeArq.write(mazeGrid[i][j]);
                }
                mazeArq.write("\n");
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String[][] getMazeGrid() {
        return mazeGrid;
    }

    @Override
    public String toString() {
        return "Maze [CellHeight=" +cellHeight+", cellWidth=" + cellWidth + ", mazeHeight=" + height +", mazeWidth=" + width +"]";
    }
}
