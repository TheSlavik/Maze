package maze;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Maze {

    private CellStatus[][] structure;
    private boolean found;
    private final Random random = new Random();

    public void generate(int size) {
        structure = new CellStatus[size][size];
        found = false;
        // Заполнение стенами
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                structure[x][y] = CellStatus.WALL;
            }
        }
        // Установка старта и финиша
        size = setStartAndEnd(size);
        // Отделение границ от внутренней части (необходимо для алгоритма генерации)
        for (int y = 1; y < size - 1; y++) {
            for (int x = 1; x < size - 1; x++) {
                structure[x][y] = CellStatus.WALL_OR_EMPTY;
            }
        }
        // Расстановка перекрёстков
        for (int y = 0; y < size / 2; y++) {
            for (int x = 0; x < size / 2; x++) {
                structure[x * 2 + 1][y * 2 + 1] = CellStatus.CHECKED;
            }
        }
        // Основная генерация
        mainGeneration(1, 1);
    }

    public void load(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        if (!fileName.endsWith(".txt")) {
            throw new InvalidObjectException("Cannot load the maze. It has an invalid format");
        }
        String s = bufferedReader.readLine();
        int size = s.length() / 2;
        structure = new CellStatus[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                switch (s.charAt(x * 2)) {
                    case '\u2588' -> structure[y][x] = CellStatus.WALL;
                    case ' ' -> structure[y][x] = CellStatus.EMPTY;
                    default -> structure[y][x] = CellStatus.PATH;
                }
            }
            s = bufferedReader.readLine();
        }
        bufferedReader.close();
        setStartAndEnd(size);
        found = structure[1][1].equals(CellStatus.PATH);
    }

    public void save(String file, boolean withPath) {
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            for (CellStatus[] line : structure) {
                for (CellStatus cell : line) {
                    fileWriter.append(!withPath && "//".equals(cell.value) ? "  " : cell.value);
                }
                fileWriter.append("\n");
            }
        } catch (Exception e) {
            System.out.print("");
        }
    }

    public void display(boolean withPath) {
        for (CellStatus[] line : structure) {
            for (CellStatus cell : line) {
                System.out.print(!withPath && "//".equals(cell.value) ? "  " : cell.value);
            }
            System.out.println();
        }
    }

    public void escape(int y, int x) {
        structure[y][x] = CellStatus.PATH;
        if (!getNeighbours(y, x, CellStatus.END).isEmpty()) {
            found = true;
        } else {
            List<int[]> list = getNeighbours(y, x, CellStatus.EMPTY);
            while (!list.isEmpty() && !found) {
                switch (list.remove(0)[2]) {
                    case 3 -> escape(y, x + 1);
                    case 6 -> escape(y + 1, x);
                    case 9 -> escape(y, x - 1);
                    default -> escape(y - 1, x);
                }
            }
            if (!found) {
                structure[y][x] = CellStatus.CHECKED;
            }
        }
    }

    private void mainGeneration(int y, int x) {
        structure[y][x] = CellStatus.EMPTY;
        List<int[]> list = getNeighbours(y, x, CellStatus.WALL_OR_EMPTY);
        while (!list.isEmpty()) {
            int[] arr = list.remove(random.nextInt(list.size()));
            switch (arr[2]) {
                case 3:
                    if (structure[arr[0]][arr[1] + 1].equals(CellStatus.CHECKED)) {
                        structure[arr[0]][arr[1]] = CellStatus.EMPTY;
                        mainGeneration(arr[0], arr[1] + 1);
                    }
                    break;
                case 6:
                    if (structure[arr[0] + 1][arr[1]].equals(CellStatus.CHECKED)) {
                        structure[arr[0]][arr[1]] = CellStatus.EMPTY;
                        mainGeneration(arr[0] + 1, arr[1]);
                    }
                    break;
                case 9:
                    if (structure[arr[0]][arr[1] - 1].equals(CellStatus.CHECKED)) {
                        structure[arr[0]][arr[1]] = CellStatus.EMPTY;
                        mainGeneration(arr[0], arr[1] - 1);
                    }
                    break;
                default:
                    if (structure[arr[0] - 1][arr[1]].equals(CellStatus.CHECKED)) {
                        structure[arr[0]][arr[1]] = CellStatus.EMPTY;
                        mainGeneration(arr[0] - 1, arr[1]);
                    }
                    break;
            }
        }
    }

    private List<int[]> getNeighbours(int y, int x, CellStatus status) {
        List<int[]> list = new ArrayList<>();
        if (structure[y][x + 1].equals(status)) {
            list.add(new int[]{y, x + 1, 3});
        }
        if (structure[y + 1][x].equals(status)) {
            list.add(new int[]{y + 1, x, 6});
        }
        if (structure[y][x - 1].equals(status)) {
            list.add(new int[]{y, x - 1, 9});
        }
        if (structure[y - 1][x].equals(status)) {
            list.add(new int[]{y - 1, x, 12});
        }
        return list;
    }

    private int setStartAndEnd(int size) {
        structure[1][0] = CellStatus.PATH;
        if (size % 2 == 0) {
            size -= 1;
            structure[size - 2][size] = CellStatus.END;
        }
        structure[size - 2][size - 1] = CellStatus.END;
        return size;
    }
}