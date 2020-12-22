package maze;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static Maze maze;
    private static boolean isRun = true;

    public static void main(String[] args) {

        while(isRun){
            menu();
        }

    }

    public static void menu() {
        int option;
        if(maze != null) {
            System.out.println("=== Menu ===");
            System.out.println("1. Generate a new maze");
            System.out.println("2. Load a maze");
            System.out.println("3. Save the maze");
            System.out.println("4. Display the maze");
            System.out.println("5. Solve the maze");
            System.out.println("0. Exit");


            try {
                option = sc.nextInt();
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println("Incorrect option. Please try again1");
                return;
            }

            switch (option) {
                case 1:
                    maze = generate();
                    maze.draw();
                    break;
                case 2:
                    maze = load();
                    break;
                case 3:
                    save();
                    break;
                case 4:
                    maze.draw();
                    break;
                case 5:
                    solveMaze();
                    break;
                case 0:
                    isRun = false;
                    break;
                default:
                    System.out.println("Error: invalid option");
                    break;
            }
        } else {
            System.out.println("=== Menu ===");
            System.out.println("1. Generate a new maze");
            System.out.println("2. Load a maze");
            System.out.println("0. Exit");


            try {
                option = sc.nextInt();
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println("Incorrect option. Please try again1");
                return;
            }

            switch (option) {
                case 1:
                    maze = generate();
                    maze.draw();
                    break;
                case 2:
                    maze = load();
                    break;
                case 0:
                    isRun = false;
                    break;
                default:
                    System.out.println("Error: invalid option");
                    break;
            }
            }
    }


    public static Maze generate() {
        System.out.println("Enter the size of a new maze");
        int size;

        try {
            size = sc.nextInt();
        } catch (InputMismatchException e){
            sc.nextLine();
            System.out.println("Error: invalid number");
            return null;
        }

        if (size <= 4) {
            System.out.println("Error: Maze must be at least 5");
            return null;
        }

        return new Maze(size);

    }

    public static Maze load() {
        sc.nextLine();
        System.out.print("name of file: ");
        String filename = sc.nextLine();

        try(FileReader mazeFile = new FileReader(filename)){
            maze = new Maze(mazeFile);
        } catch (IOException e){
            System.out.println("File not found");
        }

        return maze;
    }

    public static void save() {
        sc.nextLine();
        System.out.print("Name the maze");
        String filename = sc.nextLine();
        if(filename.endsWith(".txt")) {
            try {
                maze.saveMaze(filename);
            } catch (IOException e) {
                System.out.println("No file named " +filename);
            }
        }else {
            System.out.println("File needs to be .txt");
        }
    }

    //Solver
    public static void solveMaze() {
        MazeSolver mazeSolver = new MazeSolver(maze);
        mazeSolver.draw();
    }
}
