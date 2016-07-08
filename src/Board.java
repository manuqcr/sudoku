import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by emmanuel on 07/07/16.
 */
public class Board {

    private ArrayList<Cell> cells = new ArrayList<>(81);

    Cell getCell(int row, int column) {
        return cells.get(row * 9 + column);
    }

    public void add(Cell cell) {
        cells.add(cell);
    }

    public void lockEverything() {
        cells.forEach(cell -> cell.lock());
    }

    /**
     * Renvoie les valeurs de toutes les cellules qui sont dans la même ligne que la
     * cellule mentionnée. La valeur de la cellule mentionnée ne sera pas renvoyée
     * @param row ligne de la cellule mentionnée
     * @param column colonne de la cellule mentionnée
     * @return
     */
    public Set<Integer> listOtherValuesInRow(int row,int column){
        HashSet<Integer> result = new HashSet<>();
        for (int i = 0; i < 9; ++i){
            if (i != column){
                Integer cellValue = getCell(row, i).getValue();
                if (cellValue != null){
                    result.add(cellValue);
                }
            }
        }
        return result;
    }

    /**
     * Renvoie les valeurs de toutes les cellules qui sont dans la même colonne que la
     * cellule mentionnée. La valeur de la cellule mentionnée ne sera pas renvoyée
     * @param row ligne de la cellule mentionnée
     * @param column colonne de la cellule mentionnée
     * @return
     */
    public Set<Integer> listOtherValuesInColumn(int row,int column){
        HashSet<Integer> result = new HashSet<>();
        for (int i = 0; i < 9; ++i){
            if (i != row){
                Integer cellValue = getCell(i, column).getValue();
                if (cellValue != null){
                    result.add(cellValue);
                }
            }
        }
        return result;
    }


    /**
     * Renvoie les valeurs de toutes les cellules qui sont dans le même carré que la
     * cellule mentionnée. La valeur de la cellule mentionnée ne sera pas renvoyée
     * @param row ligne de la cellule mentionnée
     * @param column colonne de la cellule mentionnée
     * @return
     */
    public Set<Integer> listOtherValuesInSquare(final int row, final int column){
        HashSet<Integer> result = new HashSet<>();
        final int firstSquareRow = 3 * (row / 3);
        final int firstSquareColumn = 3 * (column / 3);

        for (int c = firstSquareColumn; c < firstSquareColumn + 3; ++c){
            for (int r = firstSquareRow; r < firstSquareRow + 3; ++r) {
                if (r != row || c != column){
                    Integer cellValue = getCell(r, c).getValue();
                    if (cellValue != null){
                        result.add(cellValue);
                    }
                }
            }
        }
        return result;
    }


    public void saveToFile(File targetFile) {

        try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
            for (int i = 0; i < 81; ++i) {
                if (i > 0 && i % 9 == 0) {
                    fileOutputStream.write('\n');
                }
                Cell cell = cells.get(i);
                String cellOutput = " ";
                if (cell.isLocked()) {
                    cellOutput = cell.chosenValue.toString();
                }
                fileOutputStream.write(cellOutput.getBytes());
            }
            fileOutputStream.flush();
        } catch (IOException e) {

        }
    }

    public void loadFile(File inputFile) {
        cells.forEach(Cell::reset);

        AtomicInteger lineNb = new AtomicInteger(0);
        try (Stream<String> stream = Files.lines(Paths.get(inputFile.getAbsolutePath()))) {
            stream.forEach(line -> loadLine(line, lineNb.getAndIncrement()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lockEverything();
    }

    private void loadLine(String line, int rowNb) {
        AtomicInteger columnNumber = new AtomicInteger(0);
        line.chars().forEach(character -> {
                    Cell cell = getCell(rowNb, columnNumber.get());
                    int intValue = character - '0';
                    columnNumber.incrementAndGet();
                    if (intValue > 0 && intValue < 10) {
                        cell.setValue(intValue);
                    }
                    cell.updateText();
                }
        );
    }

    public void findErrors() {
        HashSet<Integer> foundIntegerInRow = new HashSet<>();
        // On recherche les duplicats au sein d'une même ligne
        for (int row = 0; row < 9; ++row) {
            for (int column = 0; column < 9; ++column) {
                Cell cell = getCell(row, column);
                if (cell.isLocked()) {
                    continue;
                }
                Integer cellValue = cell.getValue();
                if (cellValue == null) {
                    continue;
                }

                boolean isError = listOtherValuesInColumn(row, column).contains(cellValue);
                isError = isError || listOtherValuesInRow(row, column).contains(cellValue);
                isError = isError || listOtherValuesInSquare(row, column).contains(cellValue);
                cell.flagInError(isError);
            }
        }

    }


}
