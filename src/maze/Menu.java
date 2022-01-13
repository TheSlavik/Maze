package maze;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Menu {

    private final Scanner scanner = new Scanner(System.in);
    private final maze.Maze maze = new maze.Maze();
    private boolean isCurrent = false;
    private boolean isExit = false;

    public static void main(String[] args) {
        Menu menu = new Menu();
        do {
            menu.printMenu();
            menu.chooseOption();
        } while (!menu.isExit);
    }

    private void printMenu() {
        System.out.println("=== Menu ===\n" +
                "1. Generate a new maze\n" +
                "2. Load a maze");
        if (isCurrent) {
            System.out.println("3. Save the maze\n" +
                    "4. Display the maze\n" +
                    "5. Find the escape");
        }
        System.out.println("0. Exit");
    }

    private void chooseOption() {
        while (true) {
            int i = Integer.parseInt(scanner.nextLine());
            if (i > -1 && i < 3 || (i > 2 && i < 6 && isCurrent)) {
                switch (i) {
                    case 1 -> {
                        System.out.println("Enter the size of a new maze");
                        maze.generate(Integer.parseInt(scanner.nextLine()));
                        maze.display(false);
                        isCurrent = true;
                    }
                    case 2 -> {
                        Path path = Paths.get(scanner.nextLine());
                        try {
                            maze.load(path.toString());
                            isCurrent = true;
                        } catch (FileNotFoundException e) {
                            System.out.printf("The file %s does not exist\n", path.getFileName());
                        } catch (InvalidObjectException e) {
                            System.out.println(e.getMessage());
                        } catch (IOException e) {
                            System.out.print("");
                        }
                    }
                    case 3 -> maze.save(scanner.nextLine(), false);
                    case 4 -> maze.display(false);
                    case 5 -> {
                        maze.escape(1, 1);
                        maze.display(true);
                    }
                    default -> {
                        System.out.println("Bye!");
                        isExit = true;
                    }
                }
                return;
            } else {
                System.out.println("Incorrect option. Please try again");
            }
        }
    }
}