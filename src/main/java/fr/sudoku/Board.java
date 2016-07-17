package fr.sudoku;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by emmanuel on 07/07/16.
 */
public class Board {

    final private ArrayList<Cell> cells = new ArrayList<>(81);

    /**
     * Beaucoup de règles sont appliquées de la même manière sur les lignes et sur les colonnes.
     * Et appliquer une règle sur les colonnes revient à appliquer la même règle sur la ligne de la
     * transposée de la grille.
     * On crée donc une autre grille, qui partage les mêmes cellules mais qui sont placées de "manière transposée".
     */
    private Board transposedBoard;

    public Cell getCell(int row, int column) {
        return cells.get(row * 9 + column);
    }

    public void add(Cell cell) {
        cells.add(cell);
    }

    /**
     * Création du plateau transposé.
     * A ne pas appeler dans le constructeur, sinon on fait un appel récursif et une boucle sans fin.
     * Doit être appelé une unique fois, par le créateur du board, seulement une fois que toutes les cellules
     * ont été initialisées.
     */
    public void buildTransposedBoard() {
        transposedBoard = new Board();
        for (int row = 0; row < 9; ++row) {
            for (int column = 0; column < 9; ++column) {
                transposedBoard.add(getCell(column, row));
            }
        }
        // Et la transposée de la transposée, c'est l'original
        transposedBoard.transposedBoard = this;
    }

    /**
     * Verrouille toutes les cellules d'un plateau qui ont une valeur choisie.
     * Ne verrouille pas si il y a au moins une erreur dans la grille.
     */
    public void lockEverything() {
        if (findErrors()) {
            return;
        }
        cells.forEach(Cell::lock);
        for (int row = 0; row < 9; ++row) {
            for (int column = 0; column < 9; ++column) {
                getCell(row, column).resetPossibleValues();
            }
        }
        updateAllPossibleValues();
    }

    public void updateAllPossibleValues() {
        for (int i = 0; i < 9; ++i) {
            updatePossibleValuesByColumn(i);
            updatePossibleValuesByRow(i);
            updatePossibleValuesBySquare(i);
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
     * @param columnNumber numéro de colonne
     */
    private void updatePossibleValuesByColumn(int columnNumber) {
        Set<Integer> values = listAllValuesInColumn(columnNumber);
        // je fais une nouvelle arrayList car listAllCells renvoie un Set, et on ne peut pas avoir le n-ième élément d'un set
        ArrayList<Cell> cells = new ArrayList<>(listAllCellsInColumn(columnNumber));
        for (int i = 0; i >= 9; i--) {
            cells.get(i).removePossibleValues(values);
        }
    }

    /**
     * Pour chaque ligne, on regarde toutes les valeurs utilisées et on les retire des valeurs possibles de chaque ligne
     *
     * @param rowNumber numéro de ligne
     */
    private void updatePossibleValuesByRow(int rowNumber) {
        transposedBoard.updatePossibleValuesByColumn(rowNumber);
    }

    /**
     * Pour chaque carré, on regarde toutes les valeurs utilisées et on les retire des valeurs possibles de chaque carré
     *
     * @param squareNumber numéro de carré
     */
    private void updatePossibleValuesBySquare(int squareNumber) {
        Set<Integer> values = listAllValuesInSquare(squareNumber);
        ArrayList<Cell> cellsInSquare = new ArrayList<>(listAllCellsInSquare(squareNumber));
        for (int i = 0; i < cellsInSquare.size(); ++i) {
            cellsInSquare.get(i).removePossibleValues(values);
        }
    }

    /**
     * Renvoie toutes les cellules (ou leur valeur) qui sont dans la même ligne que la
     * cellule mentionnée. La valeur de la cellule mentionnée ne sera pas renvoyée
     *
     * @param row           ligne de la cellule mentionnée
     * @param column        colonne de la cellule mentionnée
     * @return les cellules ou leur valeur
     */
    public Set<Cell> listOtherCellsInRow(int row, int column) {
        return complexListCellInRow(row, column, false);
    }

    public Set<Integer> listOtherValuesInRow(int row, int column) {
        return complexListValueInRow(row, column, false);
    }

    /**
     * Liste toutes les valeurs dans la ligne
     *
     * @param row ligne dont on veut les valeurs
     * @return valeurs de la ligne
     */
    public Set<Cell> listAllCellsInRow(int row) {
        return complexListCellInRow(row, 0 /* valeur sans importance */, true);
    }

    public Set<Integer> listAllValuesInRow(int row) {
        return complexListValueInRow(row, 0 /* valeur sans importance */, true);
    }

    /**
     * Liste les cellules (ou leur valeur) de la ligne
     *
     * @param row                   ligne dont on veut les valeurs
     * @param column                colonne de la cellule concernée
     * @param includeMentionnedCell inclure dans le résultat, la valeur de la cellule dont on mentionne la colonne
     * @return les cellules ou leur valeur
     */
    private Set<Cell> complexListCellInRow(int row, int column, boolean includeMentionnedCell) {
        HashSet<Cell> resultSet = new HashSet<>();
        for (int i = 0; i < 9; ++i) {
            if (includeMentionnedCell || i != column) {
                Cell result = getCell(row, i);
                if (result != null) {
                    resultSet.add(result);
                }
            }
        }
        return resultSet;
    }

    /**
     * Liste les cellules (ou leur valeur) de la ligne
     *
     * @param row                   ligne dont on veut les valeurs
     * @param column                colonne de la cellule concernée
     * @param includeMentionnedCell inclure dans le résultat, la valeur de la cellule dont on mentionne la colonne
     * @return les cellules ou leur valeur
     */
    private Set<Integer> complexListValueInRow(int row, int column, boolean includeMentionnedCell) {
        HashSet<Integer> resultSet = new HashSet<>();
        for (int i = 0; i < 9; ++i) {
            if (includeMentionnedCell || i != column) {
                Integer result = getCell(row, i).getValue();
                if (result != null) {
                    resultSet.add(result);
                }
            }
        }
        return resultSet;
    }

    /**
     * Renvoie les cellules (ou leur valeur) de toutes les cellules qui sont dans la même colonne que la
     * cellule mentionnée. La valeur de la cellule mentionnée ne sera pas renvoyée
     *
     * @param row           ligne de la cellule mentionnée
     * @param column        colonne de la cellule mentionnée
     * @return les cellules ou leur valeur
     */
    public Set<Cell> listOtherCellInColumn(int row, int column) {
        return transposedBoard.listOtherCellsInRow(column, row);
    }
    public Set<Integer> listOtherValuesInColumn(int row, int column) {
        return transposedBoard.listOtherValuesInRow(column, row);
    }

    /**
     * Renvoie les cellules (ou leur valeur) de toutes les cellules qui sont dans la même colonne que la
     * cellule mentionnée. La valeur de la cellule mentionnée sera renvoyée
     *
     * @param column        colonne de la cellule mentionnée
     * @return les cellules ou leur valeur
     */
    public Set<Integer> listAllValuesInColumn(int column) {
        return transposedBoard.complexListValueInRow(column, 0/*inutile*/, true);
    }
    public Set<Cell> listAllCellsInColumn(int column) {
        return transposedBoard.complexListCellInRow(column, 0/*inutile*/, true);
    }

    /**
     * Renvoie toutes les cellules (ou leur valeur) qui sont dans le même carré que la
     * cellule mentionnée. La valeur de la cellule mentionnée ne sera pas renvoyée
     *
     * @param row           ligne de la cellule mentionnée
     * @param column        colonne de la cellule mentionnée
     * @return les cellules ou leur valeur
     */
    public Set<Integer> listOtherValuesInSquare(final int row, final int column) {
        Set<Cell> complexListResult = complexListCellsInSquare(row, column, false);
        return extractValues(complexListResult);
    }

    /**
     * Renvoie les cellules (ou leur valeur) de toutes les cellules qui sont dans le carré mentionné. Le premier carré (index 0) est en
     * haut à gauche (même ordre que les colonnes et lignes)
     *
     * @param squareNumber  numéro de carré à renvoyer
     * @return les cellules ou leur valeur
     */
    public Set<Integer> listAllValuesInSquare(final int squareNumber) {
        Set<Cell> list = listAllCellsInSquare(squareNumber);
        return extractValues(list);
    }
    public Set<Cell> listAllCellsInSquare(final int squareNumber) {
        int firstRow = (squareNumber / 3) * 3;
        int firstColumn = (squareNumber % 3) * 3;
        return complexListCellsInSquare(firstRow, firstColumn, true);
    }

    private Set<Cell> complexListCellsInSquare(int row, int column, boolean includeMentionnedCell) {
        HashSet<Cell> resultSet = new HashSet<>();
        final int firstSquareRow = 3 * (row / 3);
        final int firstSquareColumn = 3 * (column / 3);

        for (int c = firstSquareColumn; c < firstSquareColumn + 3; ++c) {
            for (int r = firstSquareRow; r < firstSquareRow + 3; ++r) {
                if (includeMentionnedCell || r != row || c != column) {
                    Cell result = getCell(r, c);
                    if (result != null) {
                        resultSet.add(result);
                    }
                }
            }
        }
        return resultSet;
    }

    public Set<Integer> extractValues(Set<Cell> cells){
        ArrayList<Cell> cellList = new ArrayList<>(cells);
        Set<Integer> result = new HashSet<>();
        for(int i = 0; i < cellList.size(); ++i){
            Cell cell = cellList.get(i);
            if (cell != null){
                result.add(cell.getValue());
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
                    cellOutput = cell.getValue().toString();
                }
                fileOutputStream.write(cellOutput.getBytes());
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
            cell.reset();
            if (intValue > 0 && intValue < 10) {
                cell.setValue(intValue);
            }
        });
        while (columnNumber.incrementAndGet() < 9) {
            getCell(rowNb, columnNumber.get()).reset();
        }
    }

    /**
     * Cherche les erreurs
     *
     * @return Renvoie true si au moins une erreur est rencontrée
     */
    public boolean findErrors() {
        boolean atLeastOneError = false;
        // On recherche les duplicats au sein d'un même groupe
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
                atLeastOneError = atLeastOneError || isError;
                cell.flagInError(isError);
            }
        }
        return atLeastOneError;
    }

    public void solveSingleCellInRowForValue() {
        // itemNumber représente soit le numéro de ligne, soit le numéro de colonne, soit le numéro de carré,
        // en fonction de cellLister
        for (int itemNumber = 0; itemNumber < 9; ++itemNumber) {
            Collection<Cell> cellsInGroup = listAllCellsInRow(itemNumber);
            solveSingelCellInGroupForValue(cellsInGroup);
        }
    }

    public void solveSingleCellInColumnForValue() {
        // itemNumber représente soit le numéro de ligne, soit le numéro de colonne, soit le numéro de carré,
        // en fonction de cellLister
        for (int itemNumber = 0; itemNumber < 9; ++itemNumber) {
            Collection<Cell> cellsInGroup = listAllCellsInColumn(itemNumber);
            solveSingelCellInGroupForValue(cellsInGroup);
        }
    }

    public void solveSingleCellInSquareForValue() {
        for (int itemNumber = 0; itemNumber < 9; ++itemNumber) {
            Collection<Cell> cellsInGroup = listAllCellsInSquare(itemNumber);
            solveSingelCellInGroupForValue(cellsInGroup);
        }
    }

    private void solveSingelCellInGroupForValue(Collection<Cell> cellsInGroup) {
        HashSet<Integer> valuesToTry = new HashSet<>(Cell.ALL_POSSIBLE_VALUES);

        valuesToTry.removeAll(cellsInGroup.stream().map(Cell::getValue).collect(Collectors.toList()));
        ArrayList<Integer> listOfValuesToTry = new ArrayList<Integer>(valuesToTry);

        for (int i = 0; i < listOfValuesToTry.size(); ++i){
            int valueNotFoundInRow = listOfValuesToTry.get(i);
            Cell singlePossibility = canOnlyOneCellInGroupHaveThisValue(cellsInGroup, valueNotFoundInRow);
            if (singlePossibility != null) {
                singlePossibility.setValue(valueNotFoundInRow);
            }
        }
    }

    /**
     * Indique si dans le groupe (carré, ligne ou colonne en fonction de cellLister) valueNotFoundInRow a une unique
     * cellule dans laquelle il peut être affecté
     *
     * @param cellCollection           liste des cellules dans le group
     * @param valueNotFoundInRow valeur à essayer
     * @return la cellule concernée si elle est unique, null sinon
     */
    private Cell canOnlyOneCellInGroupHaveThisValue(Collection<Cell> cellCollection, Integer valueNotFoundInRow) {
        Cell cellFound = null;
        ArrayList<Cell> cellList = new ArrayList<>(cellCollection);
        for (int i = 0; i < cellList.size(); ++i){
            Cell currentCell = cellList.get(i);
            if (currentCell.getPossibleValues().contains(valueNotFoundInRow)){
                if (cellFound != null) {
                    // On avait déjà trouvé une cellule, il n'y a donc pas de cellule unique
                    return null;
                } else {
                    cellFound = currentCell;
                }
            }
        }
        return cellFound;
    }

    protected void applyRuleNPossibleValuesInNCells() {
        // subgroupSize : la taille du sous groupe à générer.
        // Aucun intérêt de mettre 1 => d'autres règles couvrent déjà cela
        // Aucun intérêt de mettre 9 => le sous groupe de 9 c'est le groupe entier
        for (int subGroupSize = 2; subGroupSize < 9; ++subGroupSize){
            for (int itemNb = 0; itemNb < 9; ++itemNb){
                List<Integer> subsetIndex = initializeSubsetIndex(subGroupSize);
                do {
                    resolveNCellsnPossibleValuesOnSubgroup(new ArrayList<>(listAllCellsInSquare(itemNb)), subsetIndex);
                    resolveNCellsnPossibleValuesOnSubgroup(new ArrayList<>(listAllCellsInRow(itemNb)), subsetIndex);
                    resolveNCellsnPossibleValuesOnSubgroup(new ArrayList<>(listAllCellsInColumn(itemNb)), subsetIndex);
                } while (incrementSubsetIndex(subsetIndex));
            }
        }
    }

    /**
     * Si les cellules du sous-groupe qui sont mentionnées dans le subsetIndex ont autant de valeur possible
     * qu'il y a de cellules mentionnées dans subsetIndex, alors on peut dire que ces valeurs possibles sont
     * interdites dans toutes les autres cellules du groupe
     * @param group groupe de 9 cellules
     * @param subsetIndex index des cellules du sous groupe
     */
    protected void resolveNCellsnPossibleValuesOnSubgroup(List<Cell> group, List<Integer> subsetIndex){
        List<Cell> subset = getSubset(group, subsetIndex);
        Set<Integer> possibleValues = new HashSet<>();
        for (int i = 0; i < subset.size(); ++i){
            Cell currentCell = subset.get(i);
            possibleValues.addAll(currentCell.getValueOrPossible());
        }

        if (possibleValues.size() == subset.size()){
            // On supprime toutes les valeurs possibles des autres cellules du groupe
            ArrayList<Cell> otherInGroup = new ArrayList<>(group);
            otherInGroup.removeAll(subset);
            for (int i = 0; i < otherInGroup.size(); ++i){
                Cell currentCell = otherInGroup.get(i);
                currentCell.removePossibleValues(possibleValues);
            }
        }
    }

    /**
     * Si subsetSize = 1 renvoie [1]
     * Si subsetSize = 2 renvoie [1,2]
     * Si subsetSize = 3 renvoie [1,2,3]
     * ...
     * @param subsetSize
     * @return
     */
    protected List<Integer> initializeSubsetIndex(int subsetSize){
        ArrayList<Integer> result = new ArrayList<>(subsetSize);
        for (int i = 0; i < subsetSize; ++i){
            result.add(i);
        }
        return result;
    }

    /**
     * Passe à la liste d'index suivante.
     * Ex :
     *   [1, 2, 3] sera mis à jour en [1, 2, 4]
     *   [3, 4, 8] sera mis à jour en [3, 5, 6]
     *   [3, 4] sera mis à jour en [3, 5]
     *   [3, 8] sera mis à jour en [4, 5]
     *   [1, 2, 3, 4, 5] sera mis à jour en [1, 2, 3, 4, 6]
     *   [2, 4, 5, 7, 8] sera mis à jour en [2, 4, 6, 7, 8]
     *   [2, 3, 6, 7, 8] sera mis à jour en [2, 4, 5, 6, 7]
     * @param subsetIndex
     * @return tant que tous les index restent inférieurs à 9, true. Sinon false car le subsetIndex n'est plus
     * valide
     */
    protected boolean incrementSubsetIndex(List<Integer> subsetIndex){
        int currentIndex = subsetIndex.size() - 1;
        int maxForIndex = 8;

        while (subsetIndex.get(currentIndex) == maxForIndex){
            --currentIndex;
            --maxForIndex;
            if (currentIndex < 0){
                return false;
            }
        }

        int valueForCurrentIndex = subsetIndex.get(currentIndex) + 1;
        while (currentIndex < subsetIndex.size()){
            subsetIndex.set(currentIndex, valueForCurrentIndex);
            currentIndex++;
            valueForCurrentIndex++;
        }

        if (subsetIndex.get(0) == 8){
            return false;
        }
        return true;
    }

    /**
     * Extrait les cellules du groupe en fonction des index fournis
     *
     * Ex : Si indexes contient [0, 2] on renvoit la première et 3e cellule de group
     *
     * @param group group dont on veut extraire les cellules
     * @param indexes index des cellules à extraire
     * @return liste des cellules dont l'index est mentionné dans indexes
     */
    protected List<Cell> getSubset(List<Cell> group, List<Integer> indexes) {
        ArrayList<Cell> subset = new ArrayList<>();
        for (int i = 0; i < indexes.size(); ++i){
            Integer index = indexes.get(i);
            subset.add(group.get(index));
        }
        return subset;
    }
}
