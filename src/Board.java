import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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

    /**
     * Verrouille toutes les cellules d'un plateau qui ont une valeur choisie.
     * Ne verrouille pas si il y a au moins une erreur dans la grille.
     */
    public void lockEverything() {
        if (findErrors()) {
            return;
        }
        cells.forEach(cell -> cell.lock());
        updateAllPossibleValues();
    }

    public void updateAllPossibleValues() {
        for (int row = 0; row < 9; ++row) {
            for (int column = 0; column < 9; ++column) {
                getCell(row, column).resetPossibleValues();
            }
        }
        for (int i = 0; i < 9; ++i) {
            updatePossibleValuesByColumn(i);
            updatePossibleValuesByRow(i);
            updatePossibleValuesBySquare(i);
        }
        for (int row = 0; row < 9; ++row) {
            for (int column = 0; column < 9; ++column) {
                getCell(row, column).updateText();
            }
        }
    }

    /**
     * Quand une seule valeur n'est possible dans une case, en déduire la valeur
     */
    public void solveSinglePossibleValue() {
        cells.forEach(Cell::solveSingleValeurPossible);
    }


    /**
     * Pour chaque colonne, on regarde toutes les valeurs utilisées et on les retire des valeurs possibles de chaque colonne
     *
     * @param i numéro de colonne
     */
    private void updatePossibleValuesByColumn(int i) {
        Set<Integer> values = listAllValuesInColumn(i, Cell::getValue);
        listAllValuesInColumn(i, Function.identity()/*permet de récupérer la cellule*/).forEach(
                cell -> {
                    cell.removePossibleValues(values);
                }
        );
    }

    /**
     * Pour chaque ligne, on regarde toutes les valeurs utilisées et on les retire des valeurs possibles de chaque ligne
     *
     * @param i numéro de ligne
     */
    private void updatePossibleValuesByRow(int i) {
        Set<Integer> values = listAllValuesInRow(i, Cell::getValue);
        listAllValuesInRow(i, Function.identity()/*permet de récupérer la cellule*/).forEach(
                cell -> {
                    cell.removePossibleValues(values);
                }
        );
    }

    /**
     * Pour chaque carré, on regarde toutes les valeurs utilisées et on les retire des valeurs possibles de chaque carré
     *
     * @param i numéro de carré
     */
    private void updatePossibleValuesBySquare(int i) {
        int firstRow = (i / 3) * 3;
        int firstColumn = (i % 3) * 3;
        Set<Integer> values = listAllValuesInSquare(firstRow, firstColumn, Cell::getValue);
        listAllValuesInSquare(firstRow, firstColumn, Function.identity()/*permet de récupérer la cellule*/).forEach(
                cell -> {
                    cell.removePossibleValues(values);
                }
        );
    }

    /**
     * Renvoie toutes les cellules qui sont dans la même ligne que la
     * cellule mentionnée. La valeur de la cellule mentionnée ne sera pas renvoyée
     *
     * @param row           ligne de la cellule mentionnée
     * @param column        colonne de la cellule mentionnée
     * @param extractResult méthode qui extraira le résultat de chaque cellule
     * @return
     */
    public <T> Set<T> listOtherValuesInRow(int row, int column, Function<Cell, T> extractResult) {
        return complexListValuesInRow(row, column, false, extractResult);
    }

    /**
     * Liste toutes les valeurs dans la ligne
     *
     * @param row ligne dont on veut les valeurs
     * @return valeurs de la ligne
     */
    public <T> Set<T> listAllValuesInRow(int row, Function<Cell, T> extractResult) {
        return complexListValuesInRow(row, 0 /* valeur sans importance */, true, extractResult);
    }

    /**
     * Liste Les valeurs de la ligne
     *
     * @param row                   ligne dont on veut les valeurs
     * @param column                colonne de la cellule concernée
     * @param includeMentionnedCell inclure dans le résultat, la valeur de la cellule dont on mentionne la colonne
     * @param extractResult         méthode qui extraira le résultat de chaque cellule
     * @return
     */
    private <T> Set<T> complexListValuesInRow(int row, int column, boolean includeMentionnedCell,
                                              Function<Cell, T> extractResult) {
        HashSet<T> resultSet = new HashSet<>();
        for (int i = 0; i < 9; ++i) {
            if (includeMentionnedCell || i != column) {
                T result = extractResult.apply(getCell(row, i));
                if (result != null) {
                    resultSet.add(result);
                }
            }
        }
        return resultSet;
    }

    /**
     * Renvoie les valeurs de toutes les cellules qui sont dans la même colonne que la
     * cellule mentionnée. La valeur de la cellule mentionnée ne sera pas renvoyée
     *
     * @param row           ligne de la cellule mentionnée
     * @param column        colonne de la cellule mentionnée
     * @param extractResult méthode qui extraira le résultat de chaque cellule
     * @return
     */
    public <T> Set<T> listOtherValuesInColumn(int row, int column, Function<Cell, T> extractResult) {
        return complexListValuesInColumn(row, column, false, extractResult);
    }

    /**
     * Renvoie les valeurs de toutes les cellules qui sont dans la même colonne que la
     * cellule mentionnée. La valeur de la cellule mentionnée sera renvoyée
     *
     * @param column        colonne de la cellule mentionnée
     * @param extractResult méthode qui extraira le résultat de chaque cellule
     * @return
     */
    public <T> Set<T> listAllValuesInColumn(int column, Function<Cell, T> extractResult) {
        return complexListValuesInColumn(0/*inutile*/, column, true, extractResult);
    }

    private <T> Set<T> complexListValuesInColumn(int row, int column, boolean includeMentionnedRow, Function<Cell, T> extractResult) {
        HashSet<T> resultSet = new HashSet<>();
        for (int i = 0; i < 9; ++i) {
            if (includeMentionnedRow || i != row) {
                T result = extractResult.apply(getCell(i, column));
                if (result != null) {
                    resultSet.add(result);
                }
            }
        }
        return resultSet;
    }

    /**
     * Renvoie les valeurs de toutes les cellules qui sont dans le même carré que la
     * cellule mentionnée. La valeur de la cellule mentionnée ne sera pas renvoyée
     *
     * @param row           ligne de la cellule mentionnée
     * @param column        colonne de la cellule mentionnée
     * @param extractResult méthode qui extraira le résultat de chaque cellule
     * @return
     */
    public <T> Set<T> listOtherValuesInSquare(final int row, final int column, Function<Cell, T> extractResult) {
        return complexListValuesInSquare(row, column, false, extractResult);
    }

    /**
     * Renvoie les valeurs de toutes les cellules qui sont dans le même carré que la
     * cellule mentionnée. La valeur de la cellule mentionnée sera renvoyée
     *
     * @param row           ligne de la cellule mentionnée
     * @param column        colonne de la cellule mentionnée
     * @param extractResult méthode qui extraira le résultat de chaque cellule
     * @return
     */
    public <T> Set<T> listAllValuesInSquare(final int row, final int column, Function<Cell, T> extractResult) {
        return complexListValuesInSquare(row, column, true, extractResult);
    }


    private <T> Set<T> complexListValuesInSquare(int row, int column, boolean includeMentionnedCell, Function<Cell, T> extractResult) {
        HashSet<T> resultSet = new HashSet<>();
        final int firstSquareRow = 3 * (row / 3);
        final int firstSquareColumn = 3 * (column / 3);

        for (int c = firstSquareColumn; c < firstSquareColumn + 3; ++c) {
            for (int r = firstSquareRow; r < firstSquareRow + 3; ++r) {
                if (includeMentionnedCell || r != row || c != column) {
                    T result = extractResult.apply(getCell(r, c));
                    if (result != null) {
                        resultSet.add(result);
                    }
                }
            }
        }
        return resultSet;
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

    /**
     * Cherche les erreurs
     *
     * @return Renvoie true si au moins une erreur est rencontrée
     */
    public boolean findErrors() {
        boolean atLeastOneError = false;
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

                boolean isError = listOtherValuesInColumn(row, column, Cell::getValue).contains(cellValue);
                isError = isError || listOtherValuesInRow(row, column, Cell::getValue).contains(cellValue);
                isError = isError || listOtherValuesInSquare(row, column, Cell::getValue).contains(cellValue);
                atLeastOneError = atLeastOneError || isError;
                cell.flagInError(isError);
            }
        }
        return atLeastOneError;
    }


}
